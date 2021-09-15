package de.royzer.fabrichg.mixinskt

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.broadcast
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
        serverPlayerEntity.hgPlayer.kits.forEach { kit ->
            cir.returnValue = false
            if (stack.item in kit.kitItems.map { it.item }) {
                broadcast("merkel")
                cir.returnValue = true
            } else {
                broadcast("${stack.item.name.string} - ${kit.kitItems.map { it.item.name.string }} -- kit: ${kit.name} -- ${kit.kitItems.first().name.string} --")
            }
        }
    }
    fun onDropItem(stack: ItemStack, cir: CallbackInfoReturnable<ItemEntity>, serverPlayerEntity: ServerPlayerEntity) {
//        broadcast("das andere")
//        serverPlayerEntity.hgPlayer.kits.forEach { kit ->
//            if (stack.item in kit.kitItems.map { it.item }) cir.returnValue = null else broadcast("${stack.item.name.string} - ${kit.kitItems.map { it.item.name.string }} -- kit: ${kit.name}")
//        }
    }
}