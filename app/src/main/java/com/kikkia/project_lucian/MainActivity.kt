package com.kikkia.project_lucian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.kikkia.project_lucian.clients.GetAllRequestResult
import com.kikkia.project_lucian.clients.GetRequestResult
import com.kikkia.project_lucian.clients.LEDClient
import com.kikkia.project_lucian.enums.AnimationStates
import com.kikkia.project_lucian.enums.LEDController
import com.kikkia.project_lucian.ui.theme.ProjectlucianTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectlucianTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UI()
                }
            }
        }
    }
}

@Composable
fun UI(modifier: Modifier = Modifier) {
        MyButtonWithViewModel(LEDClient())
}

// Create a Composable function for the UI
@Composable
fun MyButtonWithViewModel(viewModel: LEDClient) {
    var selectedController by remember { mutableStateOf(LEDController.REVOLVER) }
    var all by remember { mutableStateOf(true) }
    // Compose UI components
    Column( modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center) {

        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Row(
                modifier = Modifier.fillMaxWidth(.9f),
                verticalAlignment = Alignment.CenterVertically
            ) {
            for (controller in LEDController.values()) {

                    RadioButton(selected = selectedController == controller && !all, onClick = {
                        selectedController = controller
                        all = false
                    })
                    Text(text = controller.name)
                }
                RadioButton(selected = all, onClick = { all = true })
                Text(text = "All")
            }
        }
        ImageButtons(viewModel, selectedController, all)
        basicControlButtons(selectedController, all)
        StatusIndicators()
    }
}

@Composable
fun ImageButtons(client: LEDClient, selectedController: LEDController, all: Boolean) {
    val imageList = listOf(
        Pair(R.drawable.lightslinger, AnimationStates.TWOSHOT),
        Pair(R.drawable.piercing_light, AnimationStates.LASER),
        Pair(R.drawable.ardent_blaze, AnimationStates.BIGSHOT),
        Pair(R.drawable.the_culling, AnimationStates.ULTIMATE)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth()) {
        Text(text = "Lucian abilities")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .wrapContentSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (pair in imageList) {
                Image(
                    painter = painterResource(id = pair.first),
                    contentDescription = null,
                    modifier = Modifier
                        .scale(3f)
                        .weight(1f)
                        .padding(8.dp)
                        .wrapContentSize()
                        .clickable {
                            if (all) {
                                for (controller in LEDController.values()) {
                                    client.setPlaylist(controller, pair.second)
                                }
                            } else {
                                client.setPlaylist(selectedController, pair.second)
                            }
                        },
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
fun basicControlButtons(selectedController: LEDController, all: Boolean) {
    val viewModel = LEDClient()
    var lightText by remember { mutableStateOf("Turn LEDs off") }
    var brightness by remember { mutableStateOf(128.0f) }
    var lightToggle = false

    Row( modifier = Modifier
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center) {
        Button(onClick = {
            lightText = if (!lightToggle) "Turn LEDs off" else "Turn LEDs on"
            if (all) {
                for (controller in LEDController.values()) {
                    viewModel.toggleLEDsOn(controller, lightToggle)
                }
            } else {
                viewModel.toggleLEDsOn(selectedController, lightToggle)
            }
            lightToggle = !lightToggle
        },
            modifier = Modifier
                .scale(1f)
                .weight(1f)
                .padding(8.dp)
                .wrapContentSize()) {
            Text(text = lightText)
        }
        Button(onClick = {
            if (all) {
                for (controller in LEDController.values()) {
                    viewModel.setPlaylist(controller, AnimationStates.IDLE)
                }
            } else {
                viewModel.setPlaylist(selectedController, AnimationStates.IDLE)
            }
        },
            modifier = Modifier
                .scale(1f)
                .weight(1f)
                .padding(8.dp)
                .wrapContentSize()) {
            Text(text = "Force Idle anim")
        }
        Button(onClick = {
            if (all) {
                for (controller in LEDController.values()) {
                    viewModel.restartController(controller)
                }
            } else {
                viewModel.restartController(selectedController)
            }
        },
            modifier = Modifier
                .scale(1f)
                .weight(1f)
                .padding(8.dp)
                .wrapContentSize()) {
            Text(text = "Restart controller")
        }
    }
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth()) {
        Text(text = "Brightness control")
        Text(text = brightness.toInt().toString())
        Row(modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.1f),
            horizontalArrangement = Arrangement.Center) {
            Slider(value = brightness, onValueChange = {it -> brightness = it}, steps = 255, valueRange = 0f..255f)
        }
        Button(onClick = {
            if (all) {
                for(controller in LEDController.values()) {
                    viewModel.setLEDBrightness(controller, brightness.toInt())
                }
            } else {
                viewModel.setLEDBrightness(selectedController, brightness.toInt())
            }
        }) {
            Text(text = "Set brightness")
        }
    }
}

// Preview the UI
@Preview
@Composable
fun PreviewMyButtonWithViewModel() {
    MyButtonWithViewModel(LEDClient())
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjectlucianTheme {
        UI(
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp)
                .background(Color.Green)
        );
    }
}

@Composable
fun StatusIndicators() {
    val viewModel = LEDClient()
    val coroutineScope = rememberCoroutineScope()
    val requestResult by viewModel.getAllRequestResult.collectAsState()
    val statusMap = mutableMapOf<LEDController, GetRequestResult>()
    var statusMapState by remember { mutableStateOf(statusMap) }
    val brightnessMap = mutableMapOf<LEDController, Int>()
    var brightnessMapState by remember { mutableStateOf(brightnessMap) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1) // Delay between button clicks (5 seconds in this example)
            coroutineScope.launch {
                viewModel.getAllStates()
            }
        }
    }

    when (val result = requestResult) {
        is GetAllRequestResult.Success -> {
            // Handle success case
            statusMapState = result.data
        }
        else -> {
        }
    }

    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentSize()) {
        for ((controller, ledResult) in statusMapState.entries) {
            var statusText = ""
            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .wrapContentSize(),
                horizontalArrangement = Arrangement.Center) {
                Text(text = controller.name + ": ")
                when (ledResult) {
                    is GetRequestResult.Success -> {
                        // Handle success case
                        statusText =ledResult.data.toString()
                        Slider(value = ledResult.data.brightness.toFloat(),
                            onValueChange = {br -> brightnessMap[controller] = br.toInt()},
                            onValueChangeFinished = {viewModel.setLEDBrightness(controller, brightnessMap[controller]!!)})
                    }
                    is GetRequestResult.Error -> {
                        statusText = ledResult.error.message ?: "Unknown error occurred"
                    }
                }
            }
            Column() {
                Text(text = statusText)
            }
        }
    }
}