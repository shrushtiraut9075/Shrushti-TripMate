package com.example.data.auth

import android.content.Context
import android.util.Log
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.security.MessageDigest
import kotlin.coroutines.resume

data class AppUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String = "",
    val isFirebaseManaged: Boolean = false
)

sealed class AuthResult {
    data class Success(val user: AppUser, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthService(
    private val context: Context,
    private val userDao: UserDao,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val TAG = "AuthService"
    private val prefs = context.getSharedPreferences("tripmate_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    private val _isFirebaseAvailable = MutableStateFlow(false)
    val isFirebaseAvailable: StateFlow<Boolean> = _isFirebaseAvailable.asStateFlow()

    private var firebaseAuth: FirebaseAuth? = null

    init {
        checkFirebaseAvailability()
        restoreSession()
    }

    private fun checkFirebaseAvailability() {
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                firebaseAuth = FirebaseAuth.getInstance()
                _isFirebaseAvailable.value = true
                Log.d(TAG, "Firebase Authentication initialized successfully.")
            } else {
                Log.i(TAG, "FirebaseApp not initialized. Operating in local auth mode with Firebase sync readiness.")
                _isFirebaseAvailable.value = false
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase initialization exception: ${e.message}")
            _isFirebaseAvailable.value = false
        }
    }

    private fun restoreSession() {
        externalScope.launch {
            // Check Firebase Auth current user first
            val fbUser = firebaseAuth?.currentUser
            if (fbUser != null) {
                val appUser = AppUser(
                    uid = fbUser.uid,
                    email = fbUser.email ?: "user@travel.com",
                    displayName = fbUser.displayName?.ifBlank { null } ?: (fbUser.email?.substringBefore("@") ?: "Traveler"),
                    isFirebaseManaged = true
                )
                _currentUser.value = appUser
                return@launch
            }

            // Otherwise restore stored local session
            val savedUid = prefs.getString("active_uid", null)
            val savedEmail = prefs.getString("active_email", null)
            val savedName = prefs.getString("active_display_name", null)

            if (savedUid != null && savedEmail != null) {
                _currentUser.value = AppUser(
                    uid = savedUid,
                    email = savedEmail,
                    displayName = savedName ?: savedEmail.substringBefore("@"),
                    isFirebaseManaged = false
                )
            } else {
                // Default to Alex Carter (Trip creator) so user lands on an authenticated, usable state
                val defaultUser = AppUser(
                    uid = "alex_carter_01",
                    email = "alex@travel.com",
                    displayName = "Alex Carter",
                    isFirebaseManaged = false
                )
                saveSession(defaultUser)
                _currentUser.value = defaultUser
            }
        }
    }

    private fun saveSession(user: AppUser) {
        prefs.edit()
            .putString("active_uid", user.uid)
            .putString("active_email", user.email)
            .putString("active_display_name", user.displayName)
            .apply()
    }

    private fun clearSession() {
        prefs.edit().clear().apply()
    }

    suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        val cleanEmail = email.trim()
        val cleanName = displayName.trim().ifBlank { cleanEmail.substringBefore("@") }

        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters long.")
        }

        // Try Firebase Authentication if initialized
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth!!.createUserWithEmailAndPassword(cleanEmail, password).awaitResult()
                val fbUser = authResult.user
                if (fbUser != null) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(cleanName)
                        .build()
                    try {
                        fbUser.updateProfile(profileUpdate).awaitResult()
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to update Firebase profile: ${e.message}")
                    }

                    val appUser = AppUser(
                        uid = fbUser.uid,
                        email = cleanEmail,
                        displayName = cleanName,
                        isFirebaseManaged = true
                    )
                    // Also mirror into local user database for offline access
                    userDao.insertUser(
                        UserEntity(
                            uid = fbUser.uid,
                            email = cleanEmail,
                            displayName = cleanName,
                            passwordHash = hashPassword(password)
                        )
                    )
                    saveSession(appUser)
                    _currentUser.value = appUser
                    return AuthResult.Success(appUser, "Account created with Firebase Authentication!")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase sign-up failed or offline: ${e.message}")
                // If it's a specific Firebase user collision error:
                if (e.message?.contains("already in use", ignoreCase = true) == true) {
                    return AuthResult.Error("An account with this email already exists.")
                }
            }
        }

        // Local Authentication Fallback / Mode
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return AuthResult.Error("An account with email $cleanEmail already exists.")
        }

        val uid = "user_" + System.currentTimeMillis()
        val newUser = UserEntity(
            uid = uid,
            email = cleanEmail,
            displayName = cleanName,
            passwordHash = hashPassword(password)
        )
        userDao.insertUser(newUser)

        val appUser = AppUser(
            uid = uid,
            email = cleanEmail,
            displayName = cleanName,
            isFirebaseManaged = false
        )
        saveSession(appUser)
        _currentUser.value = appUser
        return AuthResult.Success(appUser, "Welcome to TripMate, $cleanName!")
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || password.isBlank()) {
            return AuthResult.Error("Please enter both email and password.")
        }

        // Try Firebase Authentication
        if (firebaseAuth != null) {
            try {
                val authResult = firebaseAuth!!.signInWithEmailAndPassword(cleanEmail, password).awaitResult()
                val fbUser = authResult.user
                if (fbUser != null) {
                    val appUser = AppUser(
                        uid = fbUser.uid,
                        email = cleanEmail,
                        displayName = fbUser.displayName ?: cleanEmail.substringBefore("@"),
                        isFirebaseManaged = true
                    )
                    saveSession(appUser)
                    _currentUser.value = appUser
                    return AuthResult.Success(appUser, "Logged in via Firebase Auth")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase sign-in error: ${e.message}")
            }
        }

        // Local Authentication check
        val localUser = userDao.getUserByEmail(cleanEmail)
        if (localUser != null) {
            if (localUser.passwordHash.isNotBlank() && localUser.passwordHash != hashPassword(password)) {
                return AuthResult.Error("Invalid password. Please check your credentials.")
            }
            val appUser = AppUser(
                uid = localUser.uid,
                email = localUser.email,
                displayName = localUser.displayName,
                isFirebaseManaged = false
            )
            saveSession(appUser)
            _currentUser.value = appUser
            return AuthResult.Success(appUser, "Welcome back, ${appUser.displayName}!")
        }

        // Demo user check if not in DB
        val defaultKnown = when (cleanEmail.lowercase()) {
            "alex@travel.com" -> AppUser("alex_carter_01", "alex@travel.com", "Alex Carter")
            "maya@travel.com" -> AppUser("maya_lin_02", "maya@travel.com", "Maya Lin")
            "sam@travel.com" -> AppUser("sam_patel_03", "sam@travel.com", "Sam Patel")
            else -> null
        }
        if (defaultKnown != null) {
            saveSession(defaultKnown)
            _currentUser.value = defaultKnown
            return AuthResult.Success(defaultKnown, "Welcome back, ${defaultKnown.displayName}!")
        }

        return AuthResult.Error("No account found for $cleanEmail. Please sign up.")
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Error during Firebase sign out: ${e.message}")
        }
        clearSession()
        _currentUser.value = null
    }

    fun switchAccount(user: AppUser) {
        saveSession(user)
        _currentUser.value = user
    }

    private fun hashPassword(password: String): String {
        return try {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            password
        }
    }
}

// Suspend extension for Firebase Task without external dependencies
suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->
            if (cont.isActive) cont.resume(result)
        }
        addOnFailureListener { exception ->
            if (cont.isActive) cont.resumeWith(Result.failure(exception))
        }
        addOnCanceledListener {
            if (cont.isActive) cont.cancel()
        }
    }
