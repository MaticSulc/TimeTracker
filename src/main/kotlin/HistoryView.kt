import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryView(showHistory: MutableState<Boolean>, log: Clockins, modifier: Modifier = Modifier){

    val Icons = Icons.Rounded
    AlertDialog(onDismissRequest = {showHistory.value = false},
    modifier = Modifier.height(400.dp).width(500.dp),
    confirmButton = {
        Button(onClick = {exportCSV(showHistory, log)}) {
            Text("Export as CSV")
        }
    },
    text = {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            log.list.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if(it.type) Icons.Home else Icons.ExitToApp, contentDescription = "")
                    Row {
                        Text(text = if(it.type) "Arrival" else "Departure", fontWeight = FontWeight.Bold)
                        Text(" - ")
                        Text(it.time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }

    })
}

private fun exportCSV(showHistory: MutableState<Boolean>, log: Clockins){
    val path = Paths.get("src\\main\\kotlin\\export.csv").toAbsolutePath()

    val writer = Files.newBufferedWriter(path)
    val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Type", "Timestamp"))
    log.list.forEach {
        csvPrinter.printRecord(it.type, it.time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
    }
    csvPrinter.close(true)
    showHistory.value = false

}