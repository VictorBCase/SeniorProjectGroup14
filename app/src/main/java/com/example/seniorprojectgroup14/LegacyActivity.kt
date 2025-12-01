package com.example.seniorprojectgroup14

/* import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.seniorprojectgroup14.ui.theme.SeniorProjectGroup14Theme

class LegacyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView() {
            SeniorProjectGroup14Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "V-Queue",
                        modifier = Modifier.padding(innerPadding)
                    )
                    RectangleButton("Button!")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SeniorProjectGroup14Theme {
        Greeting("Android")
    }
}

@Composable
fun RectangleButton(buttonText: String) {
    Column(
        modifier = Modifier.size(width = 900.dp, height = 100.dp)
            .padding(100.dp)

    ) {
        Text(buttonText)
        Row {
            Text("Click me!")
        }
    }
} */