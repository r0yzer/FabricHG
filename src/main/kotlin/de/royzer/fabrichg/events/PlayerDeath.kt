package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import de.royzer.fabrichg.sendPlayerStatus
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.server.network.ServerPlayerEntity

object PlayerDeath {
    init {
        ServerPlayerEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            if ((serverPlayerEntity as? LivingEntityAccessor)?.invokeTryUseTotem(damageSource) == true) {
                serverPlayerEntity.sendPlayerStatus()
                return@register false
            }
            if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return@register true
            val killer: ServerPlayerEntity? = (serverPlayerEntity as LivingEntityAccessor).attackingPlayer as? ServerPlayerEntity
            serverPlayerEntity.hgPlayer.kits.forEach {
                it.onDisable?.invoke(serverPlayerEntity.hgPlayer, it)
            }
             PlayerList.announcePlayerDeath(serverPlayerEntity, killer)
            serverPlayerEntity.removeHGPlayer()
            val hgPlayer = killer?.hgPlayer ?: return@register true
            hgPlayer.kills += 1

            return@register true
        }
    }
}