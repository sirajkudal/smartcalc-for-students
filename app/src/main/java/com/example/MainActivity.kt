package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.screens.CalculatorMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    
    // Instantiating modern viewmodel lazily using official standard extension delegates
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val isDarkModeState by viewModel.isDarkMode.collectAsState()
            var showSplashState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
            
            // Render beautiful student blue-and-white theme
            MyApplicationTheme(
                darkTheme = isDarkModeState
            ) {
                if (showSplashState.value) {
                    com.example.ui.screens.SplashCoordinator(onSplashFinished = {
                        showSplashState.value = false
                    })
                } else {
                    // Main layout component receiving VM
                    CalculatorMainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
