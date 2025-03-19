package com.example.inventoryapptest.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.inventoryapptest.data.model.Item

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private var apiToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")
        
        apiToken = intent.getStringExtra("api_token")
        if (apiToken == null) {
            Log.e("MainActivity", "No API token provided")
            Toast.makeText(this, "No API token provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Log.d("MainActivity", "API token received: $apiToken")

        try {
            // Initialize ViewModel with factory
            val factory = MainViewModelFactory(this)
            Log.d("MainActivity", "MainViewModelFactory created")
            
            viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
            Log.d("MainActivity", "ViewModel initialized successfully")

            // Load items from API
            viewModel.loadItemsFromApi(apiToken!!)
            Log.d("MainActivity", "Loading items from API started")

            setContent {
                MaterialTheme {
                    MainScreen(viewModel, apiToken!!)
                }
            }
            Log.d("MainActivity", "Content set successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error during initialization", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, apiToken: String) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Item?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Item?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadItemsFromApi(apiToken) },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            val items by viewModel.items.collectAsState(initial = emptyList())
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    ItemCard(
                        item = item,
                        onItemClick = { showEditDialog = item },
                        onItemLongClick = { showDeleteDialog = item }
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }
        }

        // Add Item Dialog
        if (showAddDialog) {
            ItemDialog(
                title = "Add New Item",
                onDismiss = { showAddDialog = false },
                onConfirm = { name, stock, unit ->
                    val newItem = Item(
                        id = System.currentTimeMillis().toInt(),
                        item_name = name,
                        stock = stock,
                        unit = unit
                    )
                    viewModel.addItem(newItem)
                    showAddDialog = false
                }
            )
        }

        // Edit Item Dialog
        showEditDialog?.let { item ->
            ItemDialog(
                title = "Edit Item",
                initialName = item.item_name,
                initialStock = item.stock,
                initialUnit = item.unit,
                onDismiss = { showEditDialog = null },
                onConfirm = { name, stock, unit ->
                    val updatedItem = item.copy(
                        item_name = name,
                        stock = stock,
                        unit = unit
                    )
                    viewModel.updateItem(updatedItem)
                    showEditDialog = null
                }
            )
        }

        // Delete Item Dialog
        showDeleteDialog?.let { item ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Item") },
                text = { Text("Are you sure you want to delete this item?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteItem(item)
                            showDeleteDialog = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    // Error handling
    LaunchedEffect(viewModel.error.value) {
        viewModel.error.value?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ItemCard(
    item: Item,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .pointerInteropFilter { event ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    onItemLongClick()
                    true
                }
                false
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.item_name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stock: ${item.stock} ${item.unit}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ItemDialog(
    title: String,
    initialName: String = "",
    initialStock: Int = 0,
    initialUnit: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var stock by remember { mutableStateOf(initialStock.toString()) }
    var unit by remember { mutableStateOf(initialUnit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val stockInt = stock.toIntOrNull() ?: 0
                    if (name.isNotEmpty() && stockInt >= 0 && unit.isNotEmpty()) {
                        onConfirm(name, stockInt, unit)
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 