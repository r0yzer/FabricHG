package de.royzer.fabrichg.settings

import de.royzer.fabrichg.kit.kits
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.silkmc.silk.core.task.mcCoroutineTask
import java.io.File

object ConfigManager {

    private val kitConfigs = HashMap<String, KitConfigData>()

    val gameSettings = GameSettings()

    private val configDirectory = File("config")
    private val kitConfigFile = File(configDirectory, "kitconfig.json")
    private val gameConfigFile = File(configDirectory, "gameconfig.json")
    private val json = Json {
        prettyPrint = true
    }

    init {
        if (!kitConfigFile.exists()) {
            configDirectory.mkdirs()
            kitConfigFile.createNewFile()
            kitConfigFile.writeText(json.encodeToString(listOf<KitConfigData>()))
        }

        if (!gameConfigFile.exists()) {
            gameConfigFile.createNewFile()
            gameConfigFile.writeText(json.encodeToString(gameSettings))
        }

        json.decodeFromString<List<KitConfigData>>(kitConfigFile.readText()).forEach {
            kitConfigs[it.name] = it
        }
        val gameConfigData = json.decodeFromString<GameSettings>(gameConfigFile.readText())
        gameSettings.kitAmount = gameConfigData.kitAmount.also {
            require(it <= 4)
        }
        gameSettings.minifeastEnabled = gameConfigData.minifeastEnabled
        gameSettings.mushroomCowNerf = gameConfigData.mushroomCowNerf

        setKitValues()
        updateGameConfigFile()
    }

    private fun setKitValues() {
        kits.forEach {
            if (kitConfigs.contains(it.name)) {
                val kitConfig = kitConfigs[it.name]!!
                it.enabled = kitConfig.enabled
                it.cooldown = kitConfig.cooldown
                it.usableInInvincibility = kitConfig.usableInInvincibility
                it.maxUses = kitConfig.maxUses
            } else {
                kitConfigs[it.name] = KitConfigData(
                    it.name,
                    it.enabled,
                    true,
                    it.cooldown,
                    it.maxUses,
                )
            }
            updateConfigFile()
        }
    }

     fun updateKit(name: String){
        val kit = kits.first { it.name == name }
        kitConfigs[name] = KitConfigData(name, kit.enabled, kit.usableInInvincibility, kit.cooldown, kit.maxUses)
    }

    fun updateConfigFile() =
        mcCoroutineTask(sync = false) { kitConfigFile.writeText(json.encodeToString(kitConfigs.values.toList())) }

    private fun updateGameConfigFile() =
        mcCoroutineTask(sync = false) { gameConfigFile.writeText(json.encodeToString(gameSettings)) }
}

@Serializable
data class KitConfigData @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault
    val name: String,
    @EncodeDefault
    val enabled: Boolean = true,
    @EncodeDefault
    val usableInInvincibility: Boolean = true,
    val cooldown: Double? = null,
    val maxUses: Int? = null
)
