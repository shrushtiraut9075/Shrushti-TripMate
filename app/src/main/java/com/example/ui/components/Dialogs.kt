package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.ActivityEntity
import com.example.data.local.NoteEntity
import com.example.data.local.TripEntity

@Composable
fun CreateEditTripDialog(
    tripToEdit: TripEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        destination: String,
        startDate: String,
        endDate: String,
        travelers: Int,
        budget: Double,
        description: String,
        coverImageUrl: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(tripToEdit?.name ?: "") }
    var destination by remember { mutableStateOf(tripToEdit?.destination ?: "") }
    var startDate by remember { mutableStateOf(tripToEdit?.startDate ?: "15 Oct 2026") }
    var endDate by remember { mutableStateOf(tripToEdit?.endDate ?: "20 Oct 2026") }
    var travelersStr by remember { mutableStateOf(tripToEdit?.travelers?.toString() ?: "2") }
    var budgetStr by remember { mutableStateOf(tripToEdit?.budget?.toInt()?.toString() ?: "25000") }
    var description by remember { mutableStateOf(tripToEdit?.description ?: "") }
    var coverImageUrl by remember { mutableStateOf(tripToEdit?.coverImageUrl ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (tripToEdit == null) "Create New Trip" else "Edit Trip",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Trip Name *") },
                    placeholder = { Text("e.g. Goa Adventure 2026") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trip_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it; errorMessage = null },
                    label = { Text("Destination *") },
                    placeholder = { Text("e.g. Goa, India") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trip_destination_input"),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Start Date *") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trip_start_date_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("End Date *") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trip_end_date_input"),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = travelersStr,
                        onValueChange = { travelersStr = it },
                        label = { Text("Travelers *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trip_travelers_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = budgetStr,
                        onValueChange = { budgetStr = it },
                        label = { Text("Budget *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("trip_budget_input"),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Brief notes about what you plan to do") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trip_description_input"),
                    minLines = 2
                )

                OutlinedTextField(
                    value = coverImageUrl,
                    onValueChange = { coverImageUrl = it },
                    label = { Text("Cover Image URL (optional)") },
                    placeholder = { Text("https://... or leave empty for default") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trip_cover_url_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || destination.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                        errorMessage = "Please fill in all required fields."
                        return@Button
                    }
                    val travelers = travelersStr.toIntOrNull() ?: 1
                    val budget = budgetStr.toDoubleOrNull() ?: 0.0
                    onSave(
                        name.trim(),
                        destination.trim(),
                        startDate.trim(),
                        endDate.trim(),
                        travelers,
                        budget,
                        description.trim(),
                        coverImageUrl.trim()
                    )
                },
                modifier = Modifier.testTag("trip_dialog_save_button")
            ) {
                Text(if (tripToEdit == null) "Create Trip" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateEditActivityDialog(
    tripId: Long,
    activityToEdit: ActivityEntity? = null,
    defaultDate: String = "15 Oct 2026",
    onDismiss: () -> Unit,
    onSave: (ActivityEntity) -> Unit
) {
    var name by remember { mutableStateOf(activityToEdit?.name ?: "") }
    var date by remember { mutableStateOf(activityToEdit?.date ?: defaultDate) }
    var startTime by remember { mutableStateOf(activityToEdit?.startTime ?: "09:00 AM") }
    var endTime by remember { mutableStateOf(activityToEdit?.endTime ?: "11:00 AM") }
    var location by remember { mutableStateOf(activityToEdit?.location ?: "") }
    var category by remember { mutableStateOf(activityToEdit?.category ?: "Sightseeing") }
    var description by remember { mutableStateOf(activityToEdit?.description ?: "") }

    val categories = listOf("Transport", "Hotel", "Food", "Sightseeing", "Shopping", "Adventure", "Other")
    var expandedCategory by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (activityToEdit == null) "Add Itinerary Activity" else "Edit Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Activity Name *") },
                    placeholder = { Text("e.g. Baga Beach Sunset") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location *") },
                    placeholder = { Text("e.g. Baga Beach, North Goa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_location_input"),
                    singleLine = true
                )

                // Category Dropdown
                Box {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expandedCategory = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedCategory = true }
                    )
                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Notes, directions, or booking confirmation") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || location.isBlank()) {
                        errorMessage = "Please enter an activity name and location."
                        return@Button
                    }
                    val entity = activityToEdit?.copy(
                        name = name.trim(),
                        date = date.trim(),
                        startTime = startTime.trim(),
                        endTime = endTime.trim(),
                        location = location.trim(),
                        category = category,
                        description = description.trim()
                    ) ?: ActivityEntity(
                        tripId = tripId,
                        name = name.trim(),
                        date = date.trim(),
                        startTime = startTime.trim(),
                        endTime = endTime.trim(),
                        location = location.trim(),
                        category = category,
                        description = description.trim(),
                        // Default coordinate near Goa
                        latitude = 15.4989,
                        longitude = 73.8278
                    )
                    onSave(entity)
                },
                modifier = Modifier.testTag("activity_dialog_save_button")
            ) {
                Text(if (activityToEdit == null) "Add Activity" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddChecklistDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Packing") }
    var customCategory by remember { mutableStateOf("") }
    var isCustom by remember { mutableStateOf(false) }

    val defaultCategories = listOf("Documents", "Packing", "Travel Preparation", "Custom")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Checklist Item", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task / Item Title *") },
                    placeholder = { Text("e.g. Travel Insurance, Sunscreen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checklist_title_input"),
                    singleLine = true
                )

                Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    defaultCategories.forEach { cat ->
                        OutlinedButton(
                            onClick = {
                                selectedCategory = cat
                                isCustom = (cat == "Custom")
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedCategory == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (isCustom) {
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Custom Category Name") },
                        placeholder = { Text("e.g. Photography Gear") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val categoryToUse = if (isCustom && customCategory.isNotBlank()) customCategory.trim() else selectedCategory
                        onSave(title.trim(), categoryToUse)
                    }
                },
                modifier = Modifier.testTag("checklist_dialog_save_button")
            ) {
                Text("Add Item")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateEditNoteDialog(
    tripId: Long,
    noteToEdit: NoteEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var content by remember { mutableStateOf(noteToEdit?.content ?: "") }
    var category by remember { mutableStateOf(noteToEdit?.category ?: "Travel tips") }
    var isPinned by remember { mutableStateOf(noteToEdit?.isPinned ?: false) }

    val categories = listOf(
        "Places to visit",
        "Restaurant recommendations",
        "Important information",
        "Emergency contacts",
        "Travel tips",
        "Personal reminders"
    )
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (noteToEdit == null) "Create Note" else "Edit Note",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title *") },
                    placeholder = { Text("e.g. Emergency Contacts, Best Cafes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    singleLine = true
                )

                Box {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content *") },
                    placeholder = { Text("Write your notes, contact numbers, recommendations...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_content_input"),
                    minLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = null,
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pin to top of notes", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title.trim(), content.trim(), category, isPinned)
                    }
                },
                modifier = Modifier.testTag("note_dialog_save_button")
            ) {
                Text(if (noteToEdit == null) "Create Note" else "Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddExpenseDialog(
    currencySymbol: String,
    defaultDate: String = "15 Oct 2026",
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: String, date: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }
    var date by remember { mutableStateOf(defaultDate) }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Transport", "Accommodation", "Food", "Activities", "Shopping", "Other")
    var expanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Trip Expense", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMsg = null },
                    label = { Text("Expense Title *") },
                    placeholder = { Text("e.g. Seafood Dinner at Britto's") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it; errorMsg = null },
                    label = { Text("Amount ($currencySymbol) *") },
                    placeholder = { Text("e.g. 1800") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input"),
                    singleLine = true
                )

                Box {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text("Paid via card, split between 3...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull()
                    if (title.isBlank() || amount == null || amount <= 0) {
                        errorMsg = "Please enter a valid expense title and amount."
                        return@Button
                    }
                    onSave(title.trim(), amount, category, date.trim(), description.trim())
                },
                modifier = Modifier.testTag("expense_dialog_save_button")
            ) {
                Text("Add Expense")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.testTag("confirm_delete_button"),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
