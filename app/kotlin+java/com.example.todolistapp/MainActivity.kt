package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.example.todolistapp.ui.theme.*
import androidx.compose.ui.layout.ContentScale
// DATA CLASS — "Blueprint" untuk satu task

// Kenapa guna data class? Sebab kita nak simpan 2 maklumat:
// nama task DAN status siap/belum.Kalau guna String biasa (macam code asal), kita tak boleh track sama ada task tu dah siap ke belum.
data class Task(
    val name: String,
    val isDone: Boolean = false  // default = belum siap
)

// MAIN ACTIVITY — pintu masuk app

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Bungkus semua UI dalam theme kita
            // Supaya semua screen auto ikut warna dari Theme.kt
            ToDoListTheme {
                ToDoApp()
            }
        }
    }
}

// NAVIGATION — "Traffic light" yang control screen mana nak tunjuk

@Preview(showBackground = true)
@Composable
fun ToDoApp() {

    // "remember" = suruh Compose ingat value ni walaupun redraw
    // "mutableStateOf" = bila value berubah, Compose auto update UI

    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onStartClick = { currentScreen = "tasks" },
            onAboutClick = { currentScreen = "about" }
        )
        "tasks" -> TaskScreen(
            onBackClick = { currentScreen = "home" }
        )
        "about" -> AboutScreen(
            onBackClick = { currentScreen = "home" }
        )
    }
}

// SCREEN 1: HOME SCREEN

// Scaffold = rangka, template standard,
// Dia bagi kita topBar (atas), content (tengah), bottomBar (bawah)
// Macam template yang Google sediakan supaya app nampak standard
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)  // Penting! Elak content kena tindih app bar
                .padding(30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon besar kat tengah
            Image(
                painter = painterResource(id = R.drawable.todobanner),
                contentDescription = "To-Do List Illustration",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Button utama — guna warna primary(utama) dari theme
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.btn_start))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button kedua — guna OutlinedButton supaya nampak beza dari button utama
            // button utama filled, button sekunder outlined
            OutlinedButton(
                onClick = onAboutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.btn_about))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ToDoListTheme {
        HomeScreen(onStartClick = {}, onAboutClick = {})
    }
}

// SCREEN 2: TASK SCREEN (Main Function)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    onBackClick: () -> Unit
) {
    // State untuk senarai task
    // Kita guna data class Task (bukan String) supaya boleh track done/undone
    var taskList by remember {
        mutableStateOf(
            listOf(
                Task("Buy groceries"),
                Task("Finish homework")
            )
        )
    }

    // State untuk text input
    var newTask by remember { mutableStateOf("") }

    // Kira berapa task dah siap (untuk tunjuk progress)
    val completedCount = taskList.count { it.isDone }
    val totalCount = taskList.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.task_screen_title))
                },
                navigationIcon = {
                    // button back kat app bar (standard Android pattern)
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            // Progress text — guna stringResource dengan format
            // %1$d = completedCount, %2$d = totalCount
            Text(
                text = stringResource(
                    R.string.task_count_format,
                    completedCount,
                    totalCount
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input field — guna OutlinedTextField (lebih moden dari TextField biasa)
            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                label = { Text(stringResource(R.string.task_input_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Add button
            Button(
                onClick = {
                    //isNotEmpty() boleh juga.
                    //reject string yang ada space je ("   ")
                    if (newTask.isNotBlank()) {
                        taskList = taskList + Task(newTask.trim())
                        newTask = ""  // Clear input lepas add
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.btn_add_task))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Senarai task
            if (taskList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_tasks_message),
                        color = TextGray
                    )
                }
            } else {
                // LazyColumn = RecyclerView
                // Kalau ada 1000 task, dia tak load semua sekali — jimat memory

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(taskList) { task ->
                        TaskItem(
                            task = task,
                            onToggleDone = {
                                // Toggle = tukar antara done ↔ undone
                                // .map() = buat list baru, tukar item yang match je
                                taskList = taskList.map {
                                    if (it == task) it.copy(isDone = !it.isDone)
                                    else it
                                }
                            },
                            onDelete = {
                                // .filter() = buang item yang match, simpan yang lain
                                taskList = taskList.filter { it != task }
                            }
                        )
                    }
                }
            }
        }
    }
}

// TASK ITEM — Satu card untuk satu task
// asingkan sebab:
// 1. Code lebih kemas, senang baca
// 2. Boleh reuse kalau nak guna kat screen lain
// 3. Senang nak debug — kalau card ada masalah, cari kat sini je
@Composable
fun TaskItem(
    task: Task,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(

            // Tukar warna card berdasarkan status task
            containerColor = if (task.isDone) CardDoneBackground
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox — lebih intuitive
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggleDone() },
                colors = CheckboxDefaults.colors(
                    checkedColor = TaskDoneGreen
                )
            )

            // Task name — ada strikethrough kalau dah siap
            Text(
                text = task.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isDone) TextDecoration.LineThrough
                else TextDecoration.None,
                color = if (task.isDone) TextGray
                else MaterialTheme.colorScheme.onSurface
            )

            // Delete button — guna IconButton supaya tak makan banyak space
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.btn_delete),
                    tint = DeleteRed
                )
            }
        }
    }
}


// SCREEN 3: ABOUT SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.about_screen_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.about_screen_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card untuk info student — nampak lebih kemas dari plain text
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    InfoRow(icon = Icons.Default.Person, text = stringResource(R.string.about_name))
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(icon = Icons.Default.Info, text = stringResource(R.string.about_student_id))
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(icon = Icons.Default.List, text = stringResource(R.string.about_programme))
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(icon = Icons.Default.Check, text = stringResource(R.string.about_course))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.btn_back_home))
            }
        }
    }
}

// HELPER COMPOSABLE — untk satu baris info dlm About Screen
//why? Supaya tak repeat code Icon + Text banyak kali
@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}
