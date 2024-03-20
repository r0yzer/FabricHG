package de.royzer.fabrichg.bots.player

import com.mojang.authlib.GameProfile
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.events.PlayerDeath
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.RelativeMovement
import net.silkmc.silk.core.entity.world

class HGBotFakePlayer(private val hgBot: HGBot) : FakePlayer(hgBot.world as ServerLevel?, GameProfile(hgBot.uuid, hgBot.hgName)) {
    override fun teleportTo(x: Double, y: Double, z: Double) {
        hgBot.teleportTo(x, y, z)
        super.teleportTo(x, y, z)
    }

    // tp command
    override fun teleportTo(
        level: ServerLevel,
        x: Double,
        y: Double,
        z: Double,
        relativeMovements: MutableSet<RelativeMovement>,
        yRot: Float,
        xRot: Float
    ): Boolean {
        hgBot.teleportTo(level, x, y, z, relativeMovements, yRot, xRot)
        return super.teleportTo(level, x, y, z, relativeMovements, yRot, xRot)
    }

    override fun kill() {
        hgBot.kill()
        super.kill()
    }

    override fun die(damageSource: DamageSource) {
        hgBot.die(damageSource)
        super.die(damageSource)
    }
}