package de.royzer.fabrichg.events

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.removeHGPlayer
import de.royzer.fabrichg.mixins.DamageTrackerAccessor
import net.axay.fabrik.core.text.literalText
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode

object PlayerDeath {
    init {
        ServerPlayerEvents.ALLOW_DEATH.register { serverPlayerEntity, damageSource, amount ->
            if (GamePhaseManager.currentPhase.phaseType != PhaseType.INGAME) return@register true
            serverPlayerEntity.removeHGPlayer()
//            broadcast((serverPlayerEntity.damageTracker as DamageTrackerAccessor).recentDamage.size.toString())
            (serverPlayerEntity.damageTracker as DamageTrackerAccessor).recentDamage.reversed().forEach {
//                broadcast(it.attacker?.name?.string.toString())
                if (it.damageSource.attacker is ServerPlayerEntity) {
                    val hgPlayer = PlayerList.getPlayer((it.damageSource.attacker as ServerPlayerEntity).uuid) ?: return@forEach
                    hgPlayer.kills += 1
                    return@forEach
                }
            }
            serverPlayerEntity.damageTracker.update()
//            broadcast((serverPlayerEntity.damageTracker as DamageTrackerAccessor).recentDamage.size.toString())
//            (serverPlayerEntity.damageTracker as DamageTrackerAccessor).recentDamage.reversed().forEach {
//                broadcast("hi")
//                broadcast(it.damageSource.attacker?.type?.name?.string.toString())
//                if (it.damageSource.attacker is ServerPlayerEntity) {
//                    val hgPlayer = PlayerList.getPlayer((it.damageSource.attacker as ServerPlayerEntity).uuid) ?: return@forEach
//                    hgPlayer.kills += 1
//                    return@forEach
//                }
//            }
            PlayerList.announcePlayerDeath(serverPlayerEntity.name.string, damageSource)
            return@register true
        }
    }
}