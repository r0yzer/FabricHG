package de.royzer.fabrichg

import de.royzer.fabrichg.commands.*
import de.royzer.fabrichg.events.ConnectEvents
import de.royzer.fabrichg.events.PlayerDeath
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.kit.kits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameRules

//val String.hgId get() = Identifier("fabrichg", this)

val server get() = GamePhaseManager.server

val fabrichgScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

const val TEXT_BLUE = 0x00FFFF
const val TEXT_GRAY = 0x7A7A7A

fun initServer() {
    kits

    registerCommands()
    ConnectEvents
    PlayerDeath

    ServerLifecycleEvents.SERVER_STARTED.register {
        GamePhaseManager.enable(it as DedicatedServer)
        registerCommands()
        it.overworld().dayTime = 0L
        it.gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, it)
        it.gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, it)
    }

//    val moreMushroomsFeature = MoreMushroomsFeature(DefaultFeatureConfig.CODEC)
//    Registry.register(Registry.FEATURE, "more_mushrooms".hgId, moreMushroomsFeature)

//    val moreMushrooms = moreMushroomsFeature.configure(DefaultFeatureConfig())
//        .decorate(Decorator.HEIGHTMAP.configure(HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE)))
//        .spreadHorizontally()
//
//    val moreMushroomsKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, "configured_more_mushrooms".hgId)
//    Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, moreMushroomsKey.value, moreMushrooms)

//    BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.SURFACE_STRUCTURES, moreMushroomsKey)
}

fun registerCommands() {
    startCommand
    infoCommand
    listCommand
    kitCommand
    feastCommand
    phaseCommand
    gameCommand
    hgbotCommand
}

fun ServerPlayer.sendPlayerStatus() = GamePhaseManager.server.playerList.sendAllPlayerInfo(this) // ?
