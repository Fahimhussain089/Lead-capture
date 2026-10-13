package com.assignment.dynamicform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.assignment.dynamicform.designsystem.theme.DesignTheme
import com.assignment.dynamicform.lead.LeadFormScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesignTheme {
                LeadFormScreen()
            }
        }
    }
}
