package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.LlamaSpit
import net.minecraft.world.item.Items
import net.minecraft.world.phys.EntityHitResult
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import kotlin.random.Random

val spitKit = kit("Spit") {
    kitSelectorItem = Items.GHAST_TEAR.defaultInstance

    cooldown = 8.0 / 10

    kitItem {
        itemStack = kitSelectorItem

        onClick { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick
            val spit = LlamaSpit(EntityType.LLAMA_SPIT, serverPlayer.world)
            spit.deltaMovement = serverPlayer.lookAngle
            spit.setPos(serverPlayer.eyePosition)
            spits[spit] = serverPlayer
            serverPlayer.world.addFreshEntity(spit)
            serverPlayer.playNotifySound(SoundEvents.LLAMA_SPIT, SoundSource.MASTER, 1.0f, 1.0f)

            hgPlayer.activateCooldown(kit)
        }
    }
}

val spits = hashMapOf<LlamaSpit, ServerPlayer>()

fun onSpitHit(result: EntityHitResult, spitEntity: LlamaSpit) {
    val spitter = spits[spitEntity] ?: return
    spits.remove(spitEntity)
    result.entity.hurt(spitter.damageSources().playerAttack(spitter), 1f)
    val hitted = result.entity as? ServerPlayer ?: return

    if (!hitted.inventory.hasAnyOf(setOf(Items.MUSHROOM_STEW))) {
        return
    }

    val indices = mutableListOf<Int>()

    repeat(hitted.inventory.items.size) {
        if (hitted.inventory.getItem(it).item == Items.MUSHROOM_STEW) {
            indices.add(it)
        }
    }

    if (indices.isEmpty()) return

    hitted.inventory.setItem(indices.random(), itemStack(Items.SUSPICIOUS_STEW) {})

}