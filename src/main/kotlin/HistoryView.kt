import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryView(showHistory: MutableState<Boolean>, log: Clockins, modifier: Modifier = Modifier){
    AlertDialog(onDismissRequest = {showHistory.value = false},
    title = {
        Text("Attendance history")
    },
    modifier = Modifier.width(250.dp),
    confirmButton = {
        Button(onClick = {}) {
            Text("Export as CSV")
        }
    },
    text = {
        log.list.forEach {
            Text(it.toString())
        }
    })
}