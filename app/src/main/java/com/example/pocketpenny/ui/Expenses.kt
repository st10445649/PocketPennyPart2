@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pocketpenny.ui

import android.R.attr.category
import android.R.attr.contentDescription
import android.R.attr.description
import android.R.attr.label
import android.R.attr.onClick
import android.net.Uri
import android.os.Build
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import kotlinx.coroutines.launch



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseScreen(
    navController: NavController,
    dao: ExpenseDao,
    categories: List<Category>
) {
    val expenses by dao.getAllExpenses().collectAsState(initial = emptyList())

    //group by date and/or day
    val groupedExpenses = remember(expenses){
        expenses.groupBy { expense ->
            java.time.Instant.ofEpochMilli(expense.date)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
            .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd"))

        }
    }

    val scope = rememberCoroutineScope()

    //background colour
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xff00a9fc), Color(0xff54c7ff), Color(0xffddf4ff))
    )

    //UI elements
    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "All Transactions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1.2f))
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4FC3F7))
                            Text("All", modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF4FC3F7))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Grouped items by date
                    groupedExpenses.forEach { (date, items) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A5276),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(items) { expense ->
                            TransactionItem(expense, dao)
                        }

                }
            }
        }
    }
}}

//function to set layout for groups of items/groups of expenses

