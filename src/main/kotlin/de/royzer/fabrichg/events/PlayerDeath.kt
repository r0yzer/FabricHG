package de.royzer.fabrichg.events

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import net.axay.fabrik.core.text.literalText
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode

object PlayerDeath {
    init {
        ServerPlayerEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return@register true
            serverPlayerEntity.removeHGPlayer()
            PlayerList.announcePlayerDeath(serverPlayerEntity.name.string, damageSource)
            return@register true
        }
    }
}