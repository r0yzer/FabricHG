package de.royzer.fabrichg

import de.royzer.fabrichg.commands.infoCommand
import de.royzer.fabrichg.commands.kitCommand
import de.royzer.fabrichg.commands.listCommand
import de.royzer.fabrichg.commands.startCommand
import de.royzer.fabrichg.events.ConnectEvents
import de.royzer.fabrichg.events.PlayerDeath
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.kits.anchorKit
import de.royzer.fabrichg.kit.kits.magmaKit
import de.royzer.fabrichg.world.MoreMushroomsFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig
import net.minecraft.world.gen.feature.DefaultFeatureConfig

val String.hgId get() = Identifier("fabrichg", this)

val fabrichgScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

const val TEXT_BLUE = 0x00FFFF
const val TEXT_GRAY = 0x7A7A7A

fun initServer() {

    kits

    registerCommands()
    ConnectEvents
    PlayerDeath

    ServerLifecycleEvents.SERVER_STARTED.register {
        GamePhaseManager.enable(it as MinecraftDedicatedServer)
        registerCommands()
    }

    val moreMushroomsFeature = MoreMushroomsFeature(DefaultFeatureConfig.CODEC)
    Registry.register(Registry.FEATURE, "more_mushrooms".hgId, moreMushroomsFeature)

    val moreMushrooms = moreMushroomsFeature.configure(DefaultFeatureConfig())
        .decorate(Decorator.HEIGHTMAP.configure(HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE)))
        .spreadHorizontally()

    val moreMushroomsKey = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, "configured_more_mushrooms".hgId)
    Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, moreMushroomsKey.value, moreMushrooms)

    BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.SURFACE_STRUCTURES, moreMushroomsKey)
}

fun registerCommands() {
    startCommand
    infoCommand
    listCommand
    kitCommand
}

fun ServerPlayerEntity.sendPlayerStatus() = GamePhaseManager.server.playerManager.sendPlayerStatus(this)
