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
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.silkmc.silk.core.logging.logInfo

object PlayerDeath {
    init {
        ServerLivingEntityEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            val playerDeath = hgPlayerDeath(serverPlayerEntity, damageSource, amount)
            return@register playerDeath
        }
    }

    private fun hgPlayerDeath(deadEntity: LivingEntity, damageSource: DamageSource, amount: Float): Boolean {
        if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return true
        val deadHGPlayer = deadEntity.hgPlayer ?: return true
        val serverPlayerEntity = deadHGPlayer.serverPlayer ?: return true

        val killer: Entity? = (serverPlayerEntity as LivingEntityAccessor).attackingMob


        if ((serverPlayerEntity as? LivingEntityAccessor)?.invokeTryUseTotem(damageSource) == true) {
            logInfo("${serverPlayerEntity.name.string} hat Totem genutzt")
            serverPlayerEntity.sendPlayerStatus()
            return false
        }


        if (killer is HGBot) {
            killer.kill(serverPlayerEntity.hgPlayer)
        }
//        (serverPlayerEntity as LivingEntityAccessor).invokeDropAllDeathLoot(serverPlayerEntity.serverLevel(), damageSource)
        val hgPlayer = killer?.hgPlayer ?: return true

        if (deadHGPlayer.status != HGPlayerStatus.GULAG) {
            hgPlayer.kills += 1
            hgPlayer.updateStats(1)
            serverPlayerEntity.hgPlayer.updateStats(deaths = 1)
        }

        serverPlayerEntity.dropInventoryItemsWithoutKitItems()

        serverPlayerEntity.removeHGPlayer()
        PlayerList.announcePlayerDeath(serverPlayerEntity.hgPlayer, damageSource, killer)
//        if(serverPlayerEntity is FakeServerPlayer){
//            serverPlayerEntity.connection.onDisconnect(Component.literal("Dead"))
//        }
        hgPlayer.kits.forEach {
            if (hgPlayer.canUseKit(it, true)) {
                it.events.killPlayerAction?.invoke(hgPlayer, it, serverPlayerEntity)
            }
        }


//        serverPlayerEntity.hgPlayer.kits.clear()

        return true
    }
}