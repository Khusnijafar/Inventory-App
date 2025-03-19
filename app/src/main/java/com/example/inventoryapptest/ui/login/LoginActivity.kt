package com.example.inventoryapptest.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.inventoryapptest.data.api.ApiService
import com.example.inventoryapptest.data.api.RetrofitClient
import com.example.inventoryapptest.ui.main.MainActivity

class LoginActivity : ComponentActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LoginActivity", "onCreate started")

        // Initialize API Service
        apiService = RetrofitClient.apiService
        Log.d("LoginActivity", "API Service initialized")

        // Initialize ViewModel with factory
        val factory = LoginViewModelFactory(apiService)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        Log.d("LoginActivity", "ViewModel initialized")

        setContent {
            MaterialTheme {
                LoginScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    var email by remember { mutableStateOf("programmer@da") }
    var password by remember { mutableStateOf("Prog123!") }
    val context = LocalContext.current

    // Observe states from ViewModel
    val loginResult by viewModel.loginResult.observeAsState()
    val error by viewModel.error.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)

    // Handle login result
    LaunchedEffect(loginResult) {
        loginResult?.let { response ->
            Log.d("LoginScreen", "Login result received: ${response.statusCode}")
            if (response.statusCode == 1) {
                try {
                    Log.d("LoginScreen", "Login successful, preparing to navigate to MainActivity")
                    val intent = Intent(context, MainActivity::class.java).apply {
                        putExtra("api_token", response.data.api_token)
                    }
                    Log.d("LoginScreen", "Intent created with token: ${response.data.api_token}")
                    context.startActivity(intent)
                    Log.d("LoginScreen", "MainActivity started")
                    (context as? ComponentActivity)?.finish()
                    Log.d("LoginScreen", "LoginActivity finished")
                } catch (e: Exception) {
                    Log.e("LoginScreen", "Error during navigation", e)
                    Toast.makeText(context, "Error navigating to main screen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("LoginScreen", "Login failed with status code: ${response.statusCode}")
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle error
    LaunchedEffect(error) {
        error?.let { errorMessage ->
            Log.e("LoginScreen", "Error received: $errorMessage")
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    Log.d("LoginScreen", "Login button clicked, starting login process")
                    viewModel.login(email, password)
                } else {
                    Log.w("LoginScreen", "Login button clicked with empty fields")
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }
    }
}