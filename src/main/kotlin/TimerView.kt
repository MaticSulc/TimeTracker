import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerDisplay(formatted: String, lastEvent: String, isClockedIn: Boolean, onStartClick: () -> Unit, onEndClick: () -> Unit, modifier: Modifier = Modifier){

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = formatted,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black
        )
        if(lastEvent != ""){
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Latest event",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = lastEvent,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ){
            Button(
                onClick = onStartClick,
                enabled = !isClockedIn
            ){
                Text("Arrival")
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = onEndClick,
                enabled = isClockedIn
            ){
                Text("Departure")
            }
        }
    }

}