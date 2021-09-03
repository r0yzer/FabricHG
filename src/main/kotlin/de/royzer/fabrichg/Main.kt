package de.royzer.fabrichg

import de.royzer.fabrichg.commands.infoCommand
import de.royzer.fabrichg.commands.kitCommand
import de.royzer.fabrichg.commands.listCommand
import de.royzer.fabrichg.commands.startCommand
import de.royzer.fabrichg.events.PlayerDeath
import de.royzer.fabrichg.events.ConnectEvents
import de.royzer.fabrichg.game.GamePhaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.util.Identifier

val String.hgId get() = Identifier("fabrichg", this)

val fabrichgScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

fun initServer() {

    registerCommands()
    ConnectEvents
    PlayerDeath

    ServerLifecycleEvents.SERVER_STARTED.register {
        GamePhaseManager.enable(it as MinecraftDedicatedServer)
        registerCommands()
    }

}

fun registerCommands() {
    startCommand
    infoCommand
    listCommand
    kitCommand
}