package de.royzer.fabrichg

import de.royzer.fabrichg.commands.*
import de.royzer.fabrichg.events.ConnectEvents
import de.royzer.fabrichg.events.PlayerDeath
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.kit.achievements.AchievementManager
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.mongodb.MongoManager
import de.royzer.fabrichg.mongodb.mongoScope
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.stats.Stats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameRules
import net.silkmc.silk.core.logging.logger
import net.silkmc.silk.core.task.mcCoroutineTask

//val String.hgId get() = Identifier("fabrichg", this)

val server get() = GamePhaseManager.server

val fabrichgScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

const val TEXT_BLUE = 0x00FFFF
const val TEXT_GRAY = 0x7A7A7A
const val TEXT_LIGHT_GRAY = 0xAAAAAA
const val TEXT_YELLOW = 0xFFEE40
const val TEXT_BRIGHT_YELLOW = 0xFFEE00
const val TEXT_GREEN = 0x33FF33
const val TEXT_YELLOW_CHAT = 0xFFFF55

fun initServer() {
    registerCommands()
    ConnectEvents
    PlayerDeath

    mongoScope.launch {
        runCatching {
            MongoManager.connect()
        }.onFailure {
            logger().warn("Failed to establish mongodb connection")
            it.printStackTrace()
        }.onSuccess {
            logger().warn("Successfully established mongodb connection")
        }
    }


    ServerLifecycleEvents.SERVER_STARTING.register {
        GamePhaseManager.server = it as DedicatedServer
        mongoScope.launch {
            Stats.init()
            AchievementManager.init()
        }
        kits
        ConfigManager
    }

    ServerLifecycleEvents.SERVER_STARTED.register {
        GamePhaseManager.enable(it as DedicatedServer)
        registerCommands()
        it.overworld().dayTime = 0L
        it.gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, it)
        it.gameRules.getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, it)
        it.overworld().setDefaultSpawnPos(BlockPos.ZERO, 0f)
    }


}


fun registerCommands() {
    startCommand
    infoCommand
    listCommand
    kitCommand
    kitinfoCommand
    feastCommand
    phaseCommand
    gameCommand
    hgbotCommand
    spawnCommand
    minifeastCommand
    gameSettingsCommand
    cooldownCommand
    statsCommand
    reviveCommand
    gulagCommand
    achievementsCommand
    kititemCommand
    invCommand
    teamCommand
    gueldemuerCommand
    teamChatCommand
    hgplayerRemoveCommand
    banditKitCommand
}

fun ServerPlayer.sendPlayerStatus() = GamePhaseManager.server.playerList.sendAllPlayerInfo(this) // ?
