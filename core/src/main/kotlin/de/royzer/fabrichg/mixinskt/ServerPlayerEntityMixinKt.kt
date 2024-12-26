package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.kit.kits.evokerOnDamage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object ServerPlayerEntityMixinKt {
    fun onDamage(damageSource: DamageSource, amount: Float, cir: CallbackInfoReturnable<Boolean>, damagedPlayer: ServerPlayer) {
        evokerOnDamage(damageSource, amount, cir, damagedPlayer)
    }
}