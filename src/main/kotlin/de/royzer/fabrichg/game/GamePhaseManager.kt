package de.royzer.fabrichg.game

import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.axay.fabrik.core.task.coroutineTask
import net.axay.fabrik.core.text.literalText
import net.minecraft.network.MessageType
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.text.LiteralText
import net.minecraft.util.Util
import net.minecraft.world.GameRules
import java.util.concurrent.atomic.AtomicInteger

object GamePhaseManager {
    lateinit var server: MinecraftDedicatedServer
    val timer = AtomicInteger()
    var currentPhase: GamePhase = LobbyPhase
    val currentPhaseType: PhaseType get() = currentPhase.phaseType

    fun enable(minecraftDedicatedServer: MinecraftDedicatedServer) {
        server = minecraftDedicatedServer
        server.gameRules[GameRules.SHOW_DEATH_MESSAGES].set(false, server)
        server.overworld.timeOfDay = 0
        server.overworld.worldBorder.size = 1000.0
        currentPhase.init()
        coroutineTask(period = 1000, howOften = Long.MAX_VALUE) {
            currentPhase.tick(timer.getAndIncrement())
        }
    }
    fun resetTimer() = timer.set(0)

    val isBuildingForbidden get() = currentPhaseType == PhaseType.LOBBY || currentPhaseType == PhaseType.END

    val isNotStarted get() = currentPhaseType == PhaseType.LOBBY || currentPhaseType == PhaseType.INVINCIBILITY

    val isIngame get() = currentPhaseType == PhaseType.INGAME || currentPhaseType == PhaseType.INVINCIBILITY
}

fun broadcast(text: LiteralText) {
    GamePhaseManager.server.playerManager.broadcastChatMessage(text, MessageType.SYSTEM, Util.NIL_UUID)
}

fun broadcast(text: String) {
    broadcast(literalText(text))
}
