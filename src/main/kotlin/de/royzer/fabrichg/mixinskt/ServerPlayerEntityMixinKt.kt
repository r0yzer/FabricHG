package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.mixins.DamageTrackerAccessor
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object ServerPlayerEntityMixinKt {
    fun onDamage(damageSource: DamageSource, amount: Float, cir: CallbackInfoReturnable<Boolean>, serverPlayerEntity: ServerPlayerEntity) {
    }
}