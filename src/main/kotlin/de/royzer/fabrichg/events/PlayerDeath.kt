package de.royzer.fabrichg.events

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.HGPlayerStatus
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import de.royzer.fabrichg.sendPlayerStatus
import de.royzer.fabrichg.util.dropInventoryItemsWithoutKitItems
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Wolf
import net.silkmc.silk.core.logging.logInfo

object PlayerDeath {
    init {
        ServerLivingEntityEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            val playerDeath = hgPlayerDeath(serverPlayerEntity, damageSource, amount)
            return@register playerDeath
        }
    }

    private fun hgPlayerDeath(deadEntity: LivingEntity, damageSource: DamageSource, amount: Float): Boolean {
        if ((deadEntity as? LivingEntityAccessor)?.invokeTryUseTotem(damageSource) == true) {
            logInfo("${deadEntity.name.string} hat Totem genutzt")

            if (deadEntity is ServerPlayer)
                deadEntity.sendPlayerStatus()
            return false
        }

        var killer: Entity? = (deadEntity as LivingEntityAccessor).attackingMob

        if (killer is Wolf) {
            if (killer.owner != null) {
                killer = killer.owner
            }
        }

        val hgPlayer = killer?.hgPlayer
        hgPlayer?.allKits?.forEach {
            if (hgPlayer.canUseKit(it, true)) {
                it.events.killEntityAction?.invoke(hgPlayer, it, deadEntity)
            }
        }

        if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return true

        val deadHGPlayer = deadEntity.hgPlayer ?: return true
        val serverPlayerEntity = deadHGPlayer.serverPlayer ?: return true

        if (deadHGPlayer.status == HGPlayerStatus.SPECTATOR) {
            return true
        }

        if (killer is HGBot) {
            killer.kill(serverPlayerEntity.hgPlayer)
        }

        if (deadHGPlayer.status != HGPlayerStatus.GULAG) {
            hgPlayer?.kills = hgPlayer?.kills?.plus(1) ?: 1
            hgPlayer?.updateStats(1)
            serverPlayerEntity.hgPlayer.updateStats(deaths = 1)
            serverPlayerEntity.dropInventoryItemsWithoutKitItems()
        }

        serverPlayerEntity.removeHGPlayer()
        PlayerList.announcePlayerDeath(deadHGPlayer, damageSource, killer)
        hgPlayer?.allKits?.forEach {
            if (hgPlayer.canUseKit(it, true)) {
                it.events.killPlayerAction?.invoke(hgPlayer, it, serverPlayerEntity)
            }
        }

        return true
    }
}