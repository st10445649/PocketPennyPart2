@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pocketpenny.ui

import android.R.attr.contentDescription
import android.R.attr.description
import android.net.Uri
import android.os.Build
import android.widget.DatePicker
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Transaction
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.Expense
import com.example.pocketpenny.data.ExpenseDao
import coil.compose.AsyncImage
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    dao: ExpenseDao,
    categories: List<Category>
) {
    //state variables
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableIntStateOf(-1) }
    var selectedCategoryName by remember { mutableStateOf("Select Category") }
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") } // Add this line
    var showCategorySheet by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Green) }
    val controller = rememberColorPickerController()

    //error handling message
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    //photo attachment
    var selectedImageUri: Uri? by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )


    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    //date formatting for the UI
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val dateDisplayString = datePickerState.selectedDateMillis?.let {
        java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            .format(dateFormatter)
    } ?: "Select Date"

    val scope = rememberCoroutineScope()


    //background colour
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xff00a9fc), Color(0xff54c7ff), Color(0xffddf4ff))
    )

    //UI elements
    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //heading bar that is consistent across all screens
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Add Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1.2f))
            }

            // Displays error message
            if (errorMessage != null && !showCategorySheet) {
                errorCard(error = errorMessage)
                Spacer(modifier = Modifier.height(8.dp))
            }

            //card for adding expense (top card)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Expense", modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color(0xFF1A5276)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TransactionInput(
                        value = amount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.toDoubleOrNull() != null || newValue.endsWith(
                                    "."
                                )
                            ) {
                                amount = newValue
                            }

                        },
                        label = "Amount",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    //select category
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0F9FF))
                            .clickable {errorMessage = null
                                showCategorySheet = true }
                            .padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategoryName,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF5C7A89)
                            )
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF1A5276)
                            )

//                            IconButton(onClick = { showCategorySheet = true }) {
//                                Icon(Icons.Default.Add, contentDescription = "Add Category")
//                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    //date input and date picker
                    TransactionInput(
                        value = dateDisplayString,
                        onValueChange = {},
                        label = "Date",
                        enabled = false, //typing disabled so that the date picker is only used
                        leadingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFF1A5276)
                                )
                            }
                        }

                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Section Card (Title, Description, Attachment)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TransactionInput(
                        value = title,
                        onValueChange = { title = it
                            errorMessage = null},
                        label = "Title"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TransactionInput(
                        value = description,
                        onValueChange = { description = it
                            errorMessage = null},
                        label = "Description",
                        singleLine = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TransactionInput(
                value = if (selectedImageUri != null) "selectedImageUri" else "",
                onValueChange = {},
                label = "Add Attachment",
                leadingIcon = {
                    IconButton(onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add attachment (optional)",
                            tint = Color(0xFF1A5276)
                        )
                    }

                }

            )
            Spacer(modifier = Modifier.weight(1f))

            if (selectedImageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            //saves expense to db
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0

                    when {
                        title.isBlank() -> {
                            errorMessage = "Please enter a title"
                            android.widget.Toast.makeText(
                                context,
                                errorMessage,
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }

                        amountDouble <= 0 -> {
                            errorMessage = "Please enter a valid amount"
                            android.widget.Toast.makeText(
                                context,
                                errorMessage,
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }

                        selectedCategoryId == -1 -> {
                            errorMessage = "Please select a category"
                            android.widget.Toast.makeText(
                                context,
                                errorMessage,
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            scope.launch {
                                try {
                                    //save attachment to be able to be accessed later
                                    val finalFilePath = selectedImageUri?.let { uri ->
                                        saveImageToInternalStorage(context, uri) ?: throw Exception(
                                            "Failed to save image"
                                        )
                                    }
                                    dao.insertExpense(
                                        Expense(
                                            amount = amountDouble,
                                            description = description,
                                            categoryId = selectedCategoryId,
                                            title = title,
                                            date = datePickerState.selectedDateMillis
                                                ?: System.currentTimeMillis(),
                                            filePath = finalFilePath
                                        )
                                    )
                                    navController.popBackStack()// goes back to page
                                } catch (e: Exception) {
                                    errorMessage = "Database error: ${e.localizedMessage}"
                                    android.widget.Toast.makeText(
                                        context,
                                        errorMessage,
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Add Transaction", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }


        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showCategorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showCategorySheet = false
                    errorMessage = null },
                containerColor = Color(0xff99ddff)
            ) {

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showCategorySheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                        }

                        Text(
                            "Select Category",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.Add, contentDescription = null,
                            tint = Color.White
                        )
                    }

                    if (errorMessage != null) {
                        errorCard(error = errorMessage)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Existing categories
                    categories.forEach { category ->
                        Button(
                            onClick = {
                                selectedCategoryId = category.id
                                selectedCategoryName = category.name
                                showCategorySheet = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(category.color)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                category.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))


                    Text(
                        "Create New Category", style = MaterialTheme.typography.titleMedium,
                        color = Color.White, fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        HsvColorPicker(
                            modifier = Modifier.size(180.dp),
                            controller = controller,
                            onColorChanged = { colorEnvelope ->
                                selectedColor = colorEnvelope.color
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //input for name with colour preview
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(selectedColor, CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        TextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            placeholder = {
                                Text(
                                    "Type in Category Name",
                                    color = Color(0xFF1A5276)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        IconButton(onClick = {
                            if (newCategoryName.isNotBlank()) {
                                scope.launch {
                                    try{
                                        dao.insertCategory(
                                            Category(
                                                name = newCategoryName,
                                                color = selectedColor.toArgb()
                                            )
                                        )

                                    newCategoryName = ""
                                        errorMessage = null
                                }catch (e: Exception) {
                                        errorMessage = "Could not save category: ${e.localizedMessage}"
                                }
                            }
                        }else {
                                errorMessage = "Category name cannot be empty"
                        }
                        }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Save",
                                tint = Color(0xFF1A5276),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))


                }
            }
        }
    }
}

    @Composable
    fun TransactionInput(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        singleLine: Boolean = true
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            placeholder = { Text(label, color = Color(0xFF5C7A89)) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F9FF),
                unfocusedContainerColor = Color(0xFFF0F9FF),
                disabledContainerColor = Color(0xFFF0F9FF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = singleLine
        )
    }

private fun saveImageToInternalStorage(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "receipt_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}


@Composable
fun errorCard(error : String?){
    error?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFEBEE)
            ),
            border = BorderStroke(1.dp, Color(0xFFEF9A9A))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = error!!,
                    color = Color(0xFFD32F2F),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
