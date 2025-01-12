package de.royzer.fabrichg.settings

import de.royzer.fabrichg.commands.teamCommand
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.property.Value
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.silkmc.silk.commands.registration.setupRegistrationCallback
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
        // was ist das für eine spassten scheisse hier
        gameSettings.kitAmount = gameConfigData.kitAmount.also {
            require(it <= 8)
        }
        gameSettings.minPlayers = gameConfigData.minPlayers
        gameSettings.maxIngameTime = gameConfigData.maxIngameTime
        gameSettings.feastStartTime = gameConfigData.feastStartTime
        gameSettings.minifeastEnabled = gameConfigData.minifeastEnabled
        gameSettings.mushroomCowNerf = gameConfigData.mushroomCowNerf
        gameSettings.pitEnabled = gameConfigData.pitEnabled
        gameSettings.pitStartTime = gameConfigData.pitStartTime
        gameSettings.gulagEnabled = gameConfigData.gulagEnabled
        gameSettings.achievementsEnabled = gameConfigData.achievementsEnabled
        gameSettings.gulagEndTime = gameConfigData.gulagEndTime
        gameSettings.minPlayersOutsideGulag = gameConfigData.minPlayersOutsideGulag.also {
            require(it > 2)
        }
        gameSettings.critDamage = gameConfigData.critDamage
        gameSettings.maxRecraftBeforeFeast = gameConfigData.maxRecraftBeforeFeast
        gameSettings.surpriseOnlyEnabledKits = gameConfigData.surpriseOnlyEnabledKits
        gameSettings.teamsEnabled = gameConfigData.teamsEnabled
        gameSettings.teamSize = gameConfigData.teamSize
        gameSettings.invincibilityTime = gameConfigData.invincibilityTime
        gameSettings.friendlyFire = gameConfigData.friendlyFire
        gameSettings.forbiddenKitCombinations = gameConfigData.forbiddenKitCombinations
        gameSettings.soupMode = gameConfigData.soupMode

        if (gameSettings.teamsEnabled) {
            teamCommand.setupRegistrationCallback()
        }

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
                kitConfig.additionalProperties?.forEach { (s, d) ->
                    it.properties[s] = d
                }
            } else {
                kitConfigs[it.name] = KitConfigData(
                    it.name,
                    it.enabled,
                    true,
                    it.cooldown,
                    it.maxUses,
                    it.properties
                )
            }
            updateConfigFile()
        }
    }

    fun updateKit(name: String) {
        val kit = kits.first { it.name == name }
        kitConfigs[name] =
            KitConfigData(name, kit.enabled, kit.usableInInvincibility, kit.cooldown, kit.maxUses, kit.properties)
    }

    fun updateConfigFile() =
        mcCoroutineTask(sync = false) {
            kitConfigFile.writeText(json.encodeToString(kitConfigs.values.sortedBy { it.name }.toList()))
        }

    private fun updateGameConfigFile() =
        mcCoroutineTask(sync = false) { gameConfigFile.writeText(json.encodeToString(gameSettings)) }
}

@Serializable
sealed class KitProperty {
    @Serializable
    data class BooleanKitProperty(override var data: Boolean) : KitProperty(), Value<Boolean>

    @Serializable
    data class IntKitProperty(override var data: Int) : KitProperty(), Value<Int>

    @Serializable
    data class DoubleKitProperty(override var data: Double) : KitProperty(), Value<Double>

    @Serializable
    data class FloatKitProperty(override var data: Float) : KitProperty(), Value<Float>
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
    val maxUses: Int? = null,
    @EncodeDefault
    val additionalProperties: HashMap<String, KitProperty>? = hashMapOf()
)
