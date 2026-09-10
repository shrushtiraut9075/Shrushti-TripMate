package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TripEntity::class,
        ActivityEntity::class,
        ChecklistItemEntity::class,
        NoteEntity::class,
        ExpenseEntity::class,
        UserEntity::class,
        TripCollaboratorEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun activityDao(): ActivityDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun noteDao(): NoteDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun userDao(): UserDao
    abstract fun collaboratorDao(): TripCollaboratorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tripmate_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate sample data
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                populateSampleData(database)
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateSampleData(database: AppDatabase) {
            // Seed trips
            database.tripDao().insertTrip(SampleData.sampleTrip)
            database.tripDao().insertTrip(SampleData.sampleSharedTrip)

            // Seed activities
            database.activityDao().insertActivities(SampleData.sampleActivities)
            database.activityDao().insertActivities(SampleData.sampleSharedTripActivities)

            // Seed checklist
            database.checklistDao().insertItems(SampleData.sampleChecklist)
            database.checklistDao().insertItems(SampleData.sampleSharedTripChecklist)

            // Seed notes & expenses
            database.noteDao().insertNotes(SampleData.sampleNotes)
            database.expenseDao().insertExpenses(SampleData.sampleExpenses)

            // Seed users & collaborators
            database.userDao().insertUsers(SampleData.sampleUsers)
            database.collaboratorDao().insertCollaborators(SampleData.sampleCollaborators)
        }
    }
}
