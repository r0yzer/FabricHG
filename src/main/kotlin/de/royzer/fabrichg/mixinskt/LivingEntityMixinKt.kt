package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object LivingEntityMixinKt {
    fun onDamage(source: DamageSource, amount: Float, entity: LivingEntity, cir: CallbackInfoReturnable<Boolean>) {
        if (entity is ServerPlayer) {
            if (GamePhaseManager.currentPhaseType != PhaseType.INGAME) cir.returnValue = false
        }
        else if (GamePhaseManager.currentPhaseType == PhaseType.LOBBY) cir.returnValue = false
    }
}