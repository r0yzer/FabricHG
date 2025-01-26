package de.royzer.fabrichg.game

import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.game.teams.hgTeam
import de.royzer.fabrichg.kit.kits.demomanKit
import de.royzer.fabrichg.kit.kits.trymacsKit
import de.royzer.fabrichg.util.sendEntityDataUpdate
import net.silkmc.silk.core.text.literalText
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.GameRules
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.logging.logError
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import net.silkmc.silk.core.text.literal
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

object GamePhaseManager {
    lateinit var server: DedicatedServer
    val timer = AtomicInteger()
    var currentPhase: GamePhase = LobbyPhase
    const val MOTD_STRING = "FABRIC HG 1.20.4 #1 1.20.4 HG SERVER"

    val currentPhaseType: PhaseType get() = currentPhase.phaseType

    fun enable(minecraftDedicatedServer: DedicatedServer) {
        server = minecraftDedicatedServer
        server.gameRules.getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, server)
        server.overworld().dayTime = 0
        server.overworld().worldBorder.size = 1000.0
        currentPhase.init()
        infiniteMcCoroutineTask(period = 1000.milliseconds, delay = 0.milliseconds) {
            try {
                currentPhase.tick(timer.getAndIncrement())
            } catch (e: Exception) {
                broadcastComponent("error: $e wird ignoriert".literal)
                logError(e)
                logError(e.stackTrace)
            }
        }

        infiniteMcCoroutineTask(period = 1.ticks) {
            tick() // hoffe das geht von performance her das fixt bisschen dass man oft fast 1 sekunde nicht leuchtet
        }

        demomanKit.enabled = false
        trymacsKit.enabled = false
    }

    private fun tick() {
        PlayerList.players.forEach { (uuid, hgPlayer) ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@forEach

            PlayerList.players.forEach { _, player ->
                player.serverPlayer?.let {
                    val hasGlowingEffect = it.hasEffect(MobEffects.GLOWING)

                    serverPlayer.sendEntityDataUpdate(it, 6, hasGlowingEffect)
                }
            }

            val team = hgPlayer.hgTeam ?: return@forEach
            val teamMembers = team.hgPlayers.filterNot { it.uuid == uuid }

            // wer dafür verantwortlich ist ...
            teamMembers.forEach brain@ { teamMember ->
                val teamMemberServerPlayer = teamMember.serverPlayer ?: return@brain
                val glowingEffect = MobEffectInstance(MobEffects.GLOWING, 21, 1, false, false)

                serverPlayer.connection.send(ClientboundUpdateMobEffectPacket(teamMemberServerPlayer.id, glowingEffect, true))
                serverPlayer.sendEntityDataUpdate(teamMemberServerPlayer, 6, true)
            }

        }

    }

    fun resetTimer() = timer.set(0)

    val isBuildingForbidden get() = currentPhaseType == PhaseType.LOBBY || currentPhaseType == PhaseType.END

    val isNotInPvpPhase get() = currentPhaseType == PhaseType.LOBBY || currentPhaseType == PhaseType.INVINCIBILITY

    val isIngame get() = currentPhaseType == PhaseType.INGAME || currentPhaseType == PhaseType.INVINCIBILITY
}

fun broadcastComponent(text: Component) {
    GamePhaseManager.server.playerList.broadcastSystemMessage(text, false)// broadcastMessage(text, ChatType.SYSTEM, Util.NIL_UUID)
}

fun broadcast(textString: String) {
    broadcastComponent(literalText(textString))
}
