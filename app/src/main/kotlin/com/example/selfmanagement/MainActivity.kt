package com.example.selfmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.selfmanagement.db.initAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAndroidContext(applicationContext)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
