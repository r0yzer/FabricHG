package de.royzer.fabrichg.game

import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import net.axay.fabrik.core.task.coroutineTask
import net.axay.fabrik.core.text.literalText
import net.minecraft.Util
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.world.level.GameRules
import java.util.concurrent.atomic.AtomicInteger

object GamePhaseManager {
    lateinit var server: DedicatedServer
    val timer = AtomicInteger()
    var currentPhase: GamePhase = LobbyPhase
    val currentPhaseType: PhaseType get() = currentPhase.phaseType

    fun enable(minecraftDedicatedServer: DedicatedServer) {
        server = minecraftDedicatedServer
        server.gameRules.getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, server)
        server.overworld().dayTime = 0
        server.overworld().worldBorder.size = 1000.0
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

fun broadcast(text: TextComponent) {
    GamePhaseManager.server.playerList.broadcastMessage(text, ChatType.SYSTEM, Util.NIL_UUID)
}

fun broadcast(textString: String) {
    broadcast(literalText(textString))
}
