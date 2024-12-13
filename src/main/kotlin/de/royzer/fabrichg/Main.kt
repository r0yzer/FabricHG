package de.royzer.fabrichg

import de.royzer.fabrichg.command.commands.cooldownCommand
import de.royzer.fabrichg.command.commands.feastCommand
import de.royzer.fabrichg.command.commands.gameCommand
import de.royzer.fabrichg.command.commands.gameSettingsCommand
import de.royzer.fabrichg.command.commands.hgbotCommand
import de.royzer.fabrichg.command.commands.infoCommand
import de.royzer.fabrichg.command.commands.kitCommand
import de.royzer.fabrichg.command.commands.kitinfoCommand
import de.royzer.fabrichg.command.commands.listCommand
import de.royzer.fabrichg.command.commands.minifeastCommand
import de.royzer.fabrichg.command.commands.phaseCommand
import de.royzer.fabrichg.command.commands.reviveCommand
import de.royzer.fabrichg.command.commands.spawnCommand
import de.royzer.fabrichg.command.commands.startCommand
import de.royzer.fabrichg.command.commands.statsCommand
import de.royzer.fabrichg.events.ConnectEvents
import de.royzer.fabrichg.events.PlayerDeath
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.proxy.ProxyManager
import de.royzer.fabrichg.settings.ConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameRules
import net.silkmc.silk.commands.registration.setupRegistrationCallback
import kotlin.properties.Delegates

//val String.hgId get() = Identifier("fabrichg", this)

val server get() = GamePhaseManager.server

val fabrichgScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

const val TEXT_BLUE = 0x00FFFF
const val TEXT_GRAY = 0x7A7A7A

lateinit var proxyManager: ProxyManager
var isFFA by Delegates.notNull<Boolean>()

fun initServer() {
    kits

    registerCommands()
    ConnectEvents
    PlayerDeath

    ServerLifecycleEvents.SERVER_STARTING.register {
        GamePhaseManager.server = it as DedicatedServer
    }

    ServerLifecycleEvents.SERVER_STARTED.register {
        GamePhaseManager.enable(it as DedicatedServer)
        ConfigManager
        proxyManager = ProxyManager(ConfigManager.serverInfoData.proxyHost, ConfigManager.serverInfoData.proxyPort)
        proxyManager.sendStatus(ProxyManager.ServerStatus.REACHABLE)
        registerCommands()
        it.overworld().dayTime = 0L
        it.gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, it)
        it.overworld().setDefaultSpawnPos(BlockPos.ZERO, 0f)
    }
}

fun registerCommands() {
    listOf(
        startCommand,
        infoCommand,
        listCommand,
        kitCommand,
        kitinfoCommand,
        feastCommand,
        phaseCommand,
        gameCommand,
        hgbotCommand,
        spawnCommand,
        minifeastCommand,
        gameSettingsCommand,
        cooldownCommand,
        statsCommand,
        reviveCommand
    ).forEach { it.register() }
}

fun ServerPlayer.sendPlayerStatus() = GamePhaseManager.server.playerList.sendAllPlayerInfo(this) // ?
