package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.phase.PhaseType
import net.minecraft.entity.damage.DamageSource
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object ServerPlayerEntityMixinKt {
    fun onDamage(source: DamageSource, amount: Float, cir: CallbackInfoReturnable<Boolean>) {
        if (GamePhaseManager.currentPhaseType != PhaseType.INGAME) cir.returnValue = false
    }
}