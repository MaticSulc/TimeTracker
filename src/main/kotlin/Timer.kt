
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Schedule(val requiredHours: MutableList<String>)
data class AutoLeave(val enabled: Boolean, val treshold: Int)
data class Config(val schedule: Schedule, val autoleave: AutoLeave)

class Timer {

    var currentTime by mutableStateOf("")
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var config: Config? = null

    init{
        coroutineScope.launch {
            while(true){
                delay(10L)
                currentTime = formatDate(System.currentTimeMillis())
            }
        }
        readConfig()
    }

    fun start(){

        println(config)

    }

    fun pause(){
    }

    private fun readConfig(){

        try{
            val path = Paths.get("src\\main\\kotlin\\config.yaml").toAbsolutePath()
            val mapper = ObjectMapper(YAMLFactory())
            mapper.registerModule(KotlinModule())
            Files.newBufferedReader(path).use {
                config = mapper.readValue(it, Config::class.java)
            }
        } catch(e: Exception){
            println(e.localizedMessage)
        }

    }

    private fun formatDate(ms: Long): String{
        val local = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.of("Europe/Ljubljana"))

        val formatter = DateTimeFormatter.ofPattern("hh:mm:ss")
        return local.format(formatter)

    }

}