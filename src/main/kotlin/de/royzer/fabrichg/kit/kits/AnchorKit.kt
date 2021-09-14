package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import net.axay.fabrik.core.math.vector.modifyVelocity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object AnchorKit : Kit() {
    override val name = "Anchor"
    override val kitItem: ItemStack? = null
    override val kitSelectorItem = Items.ANVIL

    fun onAttackEntity(target: Entity, serverPlayerEntity: ServerPlayerEntity) {
        if (serverPlayerEntity.hgPlayer.hasKit(this)) {
            target.setVelocity(0.0,0.0,0.0)
            target.modifyVelocity(0,-0.5,0, false)
        }
    }

    fun onKnockback(strength: Double, x: Double, z: Double, ci: CallbackInfo, livingEntity: LivingEntity) {
        val serverPlayerEntity = livingEntity as? ServerPlayerEntity ?: return
        if (serverPlayerEntity.hgPlayer.hasKit(this)) {
            ci.cancel()
            serverPlayerEntity.modifyVelocity(0,-0.5,0, false)
        }
    }
}