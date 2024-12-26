package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items

val berserkerKit = kit("Berserker") {
    kitSelectorItem = Items.BLAZE_POWDER.defaultInstance

    description = "kill entities to gain strength"

    val speedAmplifier by property(2, "speed amplifier")
    val strengthAmplifier by property(2, "strength amplifier")

    val entityKillEffectTime by property(2, "entity kill efect time")
    val playerKillEffectTime by property(10, "player kill efect time")

    val berserkSecondsAchievement by achievement("berserk seconds") {
        level(60)
        level(300)
        level(1000)
    }

    kitEvents {
        onKillEntity { hgPlayer, kit, entity ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onKillEntity

            val berserkerTime = if (entity.hgPlayer == null) entityKillEffectTime else playerKillEffectTime

            serverPlayer.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * berserkerTime, speedAmplifier))
            serverPlayer.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * berserkerTime, strengthAmplifier))

            berserkSecondsAchievement.awardLater(serverPlayer, berserkerTime)

            serverPlayer.level().playSound(serverPlayer, serverPlayer.x, serverPlayer.y, serverPlayer.z, SoundEvents.WOLF_HOWL, SoundSource.PLAYERS);
            serverPlayer.playNotifySound(SoundEvents.WOLF_HOWL, SoundSource.PLAYERS, 100f, 1f)
        }
    }
}