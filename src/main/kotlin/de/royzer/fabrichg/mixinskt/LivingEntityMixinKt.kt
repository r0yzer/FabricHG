package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object LivingEntityMixinKt {
    fun onDamage(source: DamageSource, amount: Float, entity: LivingEntity, cir: CallbackInfoReturnable<Boolean>) {
        if (entity is ServerPlayerEntity) {
            if (GamePhaseManager.currentPhaseType != PhaseType.INGAME) cir.returnValue = false
        }
        else if (GamePhaseManager.currentPhaseType == PhaseType.LOBBY) cir.returnValue = false
    }
}