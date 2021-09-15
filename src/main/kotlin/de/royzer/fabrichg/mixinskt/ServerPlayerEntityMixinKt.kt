package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.phase.PhaseType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object ServerPlayerEntityMixinKt {
    fun onDamage(damageSource: DamageSource, amount: Float, cir: CallbackInfoReturnable<Boolean>, serverPlayerEntity: ServerPlayerEntity) {
    }

    fun onDropSelectedItem(entireStack: Boolean, cir: CallbackInfoReturnable<Boolean>, serverPlayerEntity: ServerPlayerEntity) {
        val stack = serverPlayerEntity.mainHandStack
        if (GamePhaseManager.currentPhaseType == PhaseType.LOBBY) {
            cir.returnValue = true
            return
        }
        serverPlayerEntity.hgPlayer.kits.forEach { kit ->
            if (stack.item in kit.kitItems.filterNot { it.droppable }.map { it.itemStack.item }) {
                cir.returnValue = true
            }
        }
    }
}