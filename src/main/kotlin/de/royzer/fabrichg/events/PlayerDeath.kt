package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import de.royzer.fabrichg.sendPlayerStatus
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.server.level.ServerPlayer

object PlayerDeath {
    init {
        ServerLivingEntityEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            if (serverPlayerEntity !is ServerPlayer) return@register false
            if ((serverPlayerEntity as? LivingEntityAccessor)?.invokeTryUseTotem(damageSource) == true) {
                serverPlayerEntity.sendPlayerStatus()
                return@register false
            }
            if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return@register true
            val killer: ServerPlayer? = (serverPlayerEntity as LivingEntityAccessor).attackingPlayer as? ServerPlayer
            serverPlayerEntity.hgPlayer.kits.forEach {
                it.onDisable?.invoke(serverPlayerEntity.hgPlayer, it)
            }
             PlayerList.announcePlayerDeath(serverPlayerEntity, damageSource, killer)
            serverPlayerEntity.removeHGPlayer()
            val hgPlayer = killer?.hgPlayer ?: return@register true
            hgPlayer.kills += 1

            return@register true
        }
    }
}