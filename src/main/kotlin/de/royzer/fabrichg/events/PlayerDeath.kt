package de.royzer.fabrichg.events

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.DamageTrackerAccessor
import de.royzer.fabrichg.mixins.LivingEntityAccessor
import net.axay.fabrik.core.text.literalText
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode

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