package com.kikkia.project_lucian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kikkia.project_lucian.clients.LEDClient
import com.kikkia.project_lucian.clients.RequestResult
import com.kikkia.project_lucian.enums.LEDController
import com.kikkia.project_lucian.ui.theme.ProjectlucianTheme

class MainActivity : ComponentActivity() {
    val LEDClient = LEDClient()
    val LEDStatusText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectlucianTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UI("Android")
                }
            }
        }
    }
}

@Composable
fun UI(name: String, modifier: Modifier = Modifier) {
    var count by remember {
        mutableStateOf(0)
    }
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        Button(onClick = { count-- }) {
            Text(
                text = "Hello $name!",
                modifier = modifier,
                color = Color.Black,
                fontSize = 20.sp,
            )
        }
        Button(onClick = { count++ }) {
            Text(
                text = "Hello Lucian!",
                modifier = modifier,
                color = Color.Black,
                fontSize = 20.sp,
            )
        }
        Text(text = count.toString(), color = Color.White, fontSize = 15.sp)
        MyButtonWithViewModel()
    }
}

// Create a Composable function for the UI
@Composable
fun MyButtonWithViewModel() {
    // Create an instance of the ViewModel
    val viewModel = LEDClient()
    val requestResult by viewModel.requestResult.collectAsState()
    var respText by remember {
        mutableStateOf("")
    }


    // Create a state to hold the response message
    var responseMessage by remember {
        mutableStateOf("")
    }

    // Compose UI components
    Column {
        Button(
            onClick = {
                viewModel.getState(LEDController.LASER)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Send Request")
        }

        when (val result = requestResult) {
            is RequestResult.Success -> {
                // Handle success case
                respText = result.data
            }
            is RequestResult.Error -> {
                // Handle error case
                respText = result.error.message ?: "Unknown error occurred"
            }
            else -> {
            }
        }

        Text(text = respText)
        ImageButtons()
    }
}

@Composable
fun ImageButtons() {
    val imageList = listOf(
        R.drawable.lightslinger,
        R.drawable.piercing_light,
        R.drawable.ardent_blaze,
        R.drawable.the_culling
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (image in imageList) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier.scale(3f).weight(1f).padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun controllerStateUI() {
    // TODO: Update all states of each controller programmatically on a period.
}

// Preview the UI
@Preview
@Composable
fun PreviewMyButtonWithViewModel() {
    MyButtonWithViewModel()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjectlucianTheme {
        UI("Lucian", modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
            .background(Color.Green));
    }
}

fun onNightModeChange(enabled: Boolean) {

}