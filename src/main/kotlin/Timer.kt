import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.io.path.exists


data class Schedule(val requiredHours: MutableList<String>)
data class AutoLeave(val enabled: Boolean, val treshold: Int)
data class Config(val schedule: Schedule, val autoleave: AutoLeave)

data class Entry(val type: Boolean, val time: LocalDateTime = LocalDateTime.now())
data class Clockins(var list: MutableList<Entry> = mutableListOf())

class Timer {

    var currentTime by mutableStateOf("")
    var isClockedIn by mutableStateOf(false)
    var lastEvent by mutableStateOf("")

    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var config: Config
    var log: Clockins = Clockins()

    val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
        JsonDeserializer { json, type, jsonDeserializationContext ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        }).registerTypeAdapter(LocalDateTime::class.java,
        JsonSerializer { obj: LocalDateTime, type, jsonSerializationContext ->
            JsonPrimitive(obj.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
        }).create()

    init{
        coroutineScope.launch {
            while(true){
                delay(10L)

                currentTime = formatDate(System.currentTimeMillis())
            }
        }
        readConfig()
        initJSON()
    }

    fun start(){
        isClockedIn = true
        log.list.add(Entry(true))
        saveClockins()
    }

    fun pause(){
        isClockedIn = false
        log.list.add(Entry(false))
        saveClockins()
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

    private fun initJSON() {

        val path = Paths.get("src\\main\\kotlin\\log.json").toAbsolutePath()

        if(!path.exists()){
            FileWriter(path.toFile()).write("") //make empty config file
        }
        else{
            Files.newBufferedReader(path).use {
                val data = it.readLine()
                if(data != null){ //can be empty too
                    log = gson.fromJson(data, Clockins::class.java)
                    isClockedIn = log.list.last().type

                    val hoursSinceLastEntry = ChronoUnit.HOURS.between(log.list.last().time, LocalDateTime.now())

                    if(log.list.last().type && config.autoleave.enabled && hoursSinceLastEntry > config.autoleave.treshold){ //auto clock out
                        //check how many hours per day and auto calculate end time!
                        val weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2 //1 is sunday
                        val newDate = log.list.last().time.plusHours(config.schedule.requiredHours[weekDay].toLong())
                        log.list.add(Entry(false, newDate))
                        saveClockins()
                        isClockedIn = false
                    }

                    lastEvent = "${if(log.list.last().type) "Check in" else "Check out"} @ ${log.list.last().time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}"

                }
            }
        }
    }

    private fun saveClockins(){

        val path = Paths.get("src\\main\\kotlin\\log.json").toAbsolutePath()

        Files.newBufferedWriter(path).use {
            val json = gson.toJson(log)
            it.write(json) //overwrites, not appends!
        }

        lastEvent = "${if(log.list.last().type) "Check in" else "Check out"} @ ${log.list.last().time.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}"

    }



    private fun formatDate(ms: Long): String{
        val local = LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.of("Europe/Ljubljana"))
        val formatter = DateTimeFormatter.ofPattern("hh:mm:ss")
        return local.format(formatter)

    }

}