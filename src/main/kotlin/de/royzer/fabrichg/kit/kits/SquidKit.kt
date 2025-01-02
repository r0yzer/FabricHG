package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items

val squidKit = kit("Squid") {
    kitSelectorItem = Items.SQUID_SPAWN_EGG.defaultInstance

    cooldown = 13.0

    description = "Blind your enemies"

    val range by property(6.0, "Blinding range")
    val blindnessDuration by property(60, "Blinding duration (ticks)")

    kitEvents {
        onSneak { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onSneak

            val blindedPlayers = serverPlayer.level().getEntitiesOfClass(ServerPlayer::class.java, serverPlayer.boundingBox.inflate(range)) {
                it != serverPlayer
            }.filter { !it.hgPlayer.isNeo }.filter { it.hgPlayer.isAlive }


            if (blindedPlayers.isNotEmpty()) {
                blindedPlayers.forEach {
                    it.addEffect(MobEffectInstance(MobEffects.BLINDNESS, blindnessDuration, 0))
                }
                serverPlayer.playNotifySound(SoundEvents.SQUID_SQUIRT, SoundSource.PLAYERS, 1f, 1f)
                hgPlayer.activateCooldown(kit)
            }

        }

    }
}