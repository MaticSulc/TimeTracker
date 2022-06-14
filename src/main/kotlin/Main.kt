// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    val showHistory = remember { mutableStateOf(false)}
    val timer = remember { Timer() }

    MaterialTheme {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight().background(Color.LightGray)
        ){

            TimerDisplay(
                formatted = timer.currentTime,
                lastEvent = timer.lastEvent,
                isClockedIn = timer.isClockedIn,
                onStartClick = timer::start,
                onEndClick = timer::pause
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()

        ){
            Button(onClick = {
                showHistory.value = true
            }, modifier = Modifier.padding(5.dp, 0.dp) ){
                Text("View history")
            }
        }

        if(showHistory.value){
            HistoryView(showHistory, timer.log)
        }

    }
}

fun main() = application {

    Window(onCloseRequest = ::exitApplication, title = "Time Tracker") {
        App()
    }
}
