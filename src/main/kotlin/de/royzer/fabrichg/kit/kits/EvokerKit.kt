package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.projectile.EvokerFangs
import net.minecraft.world.item.Items
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

val evokerEffect get() = MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 5, 1)

// das hab ich SELBER programmiert und ausgedacht
val evokerKit = kit("Evoker") {
    kitSelectorItem = Items.SHEARS.defaultInstance

    cooldown = 11.5

    description = "Slow your enemies with magic"

    val fangCount by property(12, "fang count")

    kitItem {
        itemStack = kitSelectorItem


        onClick { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick
            val playerPos = serverPlayer.pos
            val playerPosAbove = serverPlayer.pos.add(0.0,2.0,0.0)

            val dir = serverPlayer.lookAngle

            repeat(fangCount) {
                val pos = playerPos.add(dir.scale(it.toDouble()))
                val posAbove = playerPosAbove.add(dir.scale(it.toDouble() + 1.0))
                val fang = EvokerFangs(serverPlayer.world, pos.x, pos.y, pos.z, 0F, 0, serverPlayer)
                val fangAbove = EvokerFangs(serverPlayer.world, posAbove.x, posAbove.y, posAbove.z, 0F, 0, serverPlayer)
                serverPlayer.world.addFreshEntity(fang)
                serverPlayer.world.addFreshEntity(fangAbove)
            }

            hgPlayer.activateCooldown(kit)
        }
    }
}

fun evokerOnDamage(damageSource: DamageSource, amount: Float, cir: CallbackInfoReturnable<Boolean>, damagedPlayer: ServerPlayer) {
    if (damageSource.entity is ServerPlayer && damageSource.type().msgId == "indirectMagic") {
        if (damagedPlayer.hgPlayer.isNeo) {
            cir.returnValue = false
            return
        }
        val attacker = (damageSource.entity as ServerPlayer).hgPlayer
        if (attacker.hasKit(evokerKit)) {
            damagedPlayer.addEffect(evokerEffect)
        }
    }
}