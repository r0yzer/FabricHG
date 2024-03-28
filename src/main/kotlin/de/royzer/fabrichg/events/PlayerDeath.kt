package de.royzer.fabrichg.events

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.entity.LivingEntityAccessor
import de.royzer.fabrichg.sendPlayerStatus
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.silkmc.silk.core.logging.logInfo

object PlayerDeath {
    init {
        ServerLivingEntityEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            val playerDeath = hgPlayerDeath(serverPlayerEntity, damageSource, amount)
            return@register (playerDeath)
        }
    }

    private fun hgPlayerDeath(serverPlayerEntity: LivingEntity, damageSource: DamageSource, amount: Float): Boolean {
        if (serverPlayerEntity !is ServerPlayer) return false
        if ((serverPlayerEntity as? LivingEntityAccessor)?.invokeTryUseTotem(damageSource) == true) {
            logInfo("${serverPlayerEntity.name.string} hat Totem genutzt")
            serverPlayerEntity.sendPlayerStatus()
            return false
        }
        if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return true
        val killer: Entity? = (serverPlayerEntity as LivingEntityAccessor).attackingMob
        if (killer is HGBot) {
            killer.kill(serverPlayerEntity.hgPlayer)
        }
        serverPlayerEntity.hgPlayer.kits.forEach {
            it.onDisable?.invoke(serverPlayerEntity.hgPlayer, it)
        }
        serverPlayerEntity.removeHGPlayer()
        PlayerList.announcePlayerDeath(serverPlayerEntity.hgPlayer, damageSource, killer)
        val hgPlayer = killer?.hgPlayer ?: return true
        hgPlayer.kits.forEach {
            if (hgPlayer.canUseKit(it, true)) {
                it.events.killPlayerAction?.invoke(hgPlayer, it, serverPlayerEntity)
            }
        }
        hgPlayer.kills += 1
        hgPlayer.updateStats(1)
        serverPlayerEntity.hgPlayer.updateStats(deaths = 1)
//        serverPlayerEntity.hgPlayer.kits.clear()

        return true
    }
}