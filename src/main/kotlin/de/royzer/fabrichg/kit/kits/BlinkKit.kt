package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.silkmc.silk.core.entity.pos
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.entity.blockPos
import kotlin.math.roundToInt

private const val BLINK_LAST_HIT_BY_KEY = "blinklasthitby"

val blinkKit = kit("Blink") {
    maxUses = 5

    kitSelectorItem = Items.NETHER_STAR.defaultInstance
    cooldown = 15.0
    description = "Teleport in the direction you are looking"

    val blinkDistance by property(4.0, "blink distance")
    val maxLookDifference by property(50, "look difference")
    val valMaxTimeDiff by property(3.0, "time diff for debuff to go away (s)")

    val blinkTimesAchievement by achievement("blink times") {
        level(100)
        level(700)
        level(1400)
    }
    val blinkDistanceAchievement by achievement("blink distance") {
        level(500)
        level(3000)
        level(10000)
    }


    kitItem {
        itemStack = kitSelectorItem

        onClick { hgPlayer, kit ->
            val hitInfo =  hgPlayer.getPlayerData<HitInfo>(BLINK_LAST_HIT_BY_KEY)?.let {
                if (isAgo(it, (valMaxTimeDiff * 1000).toLong())) {
                    hgPlayer.playerData.remove(BLINK_LAST_HIT_BY_KEY)
                    null
                }

                it
            }

            if (hitInfo != null) {
                if (hgPlayer.isLookingWrong(hitInfo, maxLookDifference)) {
                    hgPlayer.serverPlayer?.forceAddEffect(MobEffectInstance(MobEffects.WITHER, 5 * 20, 2), null)
                    hgPlayer.serverPlayer?.forceAddEffect(MobEffectInstance(MobEffects.BLINDNESS, 5 * 20, 2), null)
                }

                return@onClick
            }

            val player = hgPlayer.serverPlayer ?: return@onClick

            blinkTimesAchievement.awardLater(player)

            hgPlayer.checkUsesForCooldown(kit, maxUses!!)
            val p = player.lookDirection.normalize().scale(blinkDistance.toDouble())
            val newPos = player.pos.add(p.x, p.y, p.z)
            player.teleportTo(
                newPos.x, newPos.y, newPos.z
            )
            player.level().setBlockAndUpdate(BlockPos(player.blockPos.subtract(Vec3i(0,1,0))), Blocks.OAK_LEAVES.defaultBlockState())
            player.playNotifySound(SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.MASTER, 100F, 100F)
            blinkDistanceAchievement.awardLater(player, blinkDistance.roundToInt())
        }
    }

    kitEvents {
        onTakeDamage { player, kit, source, amount ->
            val hitByPlayer = when (source.entity) {
                is ServerPlayer -> source.entity as ServerPlayer
                is HGBot -> (source.entity as HGBot).serverPlayer
                else -> null
            }

            if (hitByPlayer == null) return@onTakeDamage amount

            player.playerData[BLINK_LAST_HIT_BY_KEY] = HitInfo(System.currentTimeMillis(), hitByPlayer)

            return@onTakeDamage amount
        }
    }
}