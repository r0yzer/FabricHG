package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.gulag.GulagManager
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity
import net.silkmc.silk.core.task.mcCoroutineTask
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object LivingEntityMixinKt {
    fun canDamage(source: DamageSource, entity: LivingEntity): Boolean {
        val level = entity.level()

        if (level == GulagManager.gulagLevel) {
            if (source.type() == DamageTypes.GENERIC_KILL) return true
            if (entity !is ServerPlayer) return true

            val sourceEntity = source.entity

            val sourceEntityFighting = sourceEntity?.let { GulagManager.isFighting(it) } == true
            val fighting = GulagManager.isFighting(entity)

            if (!sourceEntityFighting || !fighting) {
                return false
            }
        }

        if (entity is ServerPlayer || entity is HGBot) {
            if (GamePhaseManager.currentPhaseType != PhaseType.INGAME) return false
        } else if (GamePhaseManager.currentPhaseType == PhaseType.LOBBY || GamePhaseManager.currentPhaseType == PhaseType.END)
            return false

        return true

    }

    fun onDamage(source: DamageSource, amount: Float, entity: LivingEntity, cir: CallbackInfoReturnable<Boolean>) {
        if (!canDamage(source, entity)) {
            cir.returnValue = false
            cir.cancel()
        }
    }
}