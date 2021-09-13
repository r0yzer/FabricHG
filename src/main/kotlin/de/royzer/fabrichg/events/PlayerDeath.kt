package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.server.network.ServerPlayerEntity

object PlayerDeath {
    init {
        ServerPlayerEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return@register true
            serverPlayerEntity.removeHGPlayer()
            val killer: ServerPlayerEntity? = (serverPlayerEntity as LivingEntityAccessor).attackingPlayer as? ServerPlayerEntity
            PlayerList.announcePlayerDeath(serverPlayerEntity, killer)
            val hgPlayer = killer?.hgPlayer ?: return@register true
            hgPlayer.kills += 1

            return@register true
        }
    }
}