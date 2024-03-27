package de.royzer.fabrichg.bots

import com.mojang.authlib.GameProfile
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.randomKit
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.ConfigManager
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity

class FakeServerPlayer(gameProfile: GameProfile) : ServerPlayer(server, server.overworld(), gameProfile, ClientInformation.createDefault() ) {

    lateinit var hgBot: HGBot

    init {
        this.hgPlayer.fillKits()
        repeat(ConfigManager.gameSettings.kitAmount) {
            this.hgPlayer.setKit(randomKit(), it)
        }
    }
    override fun knockback(strength: Double, x: Double, z: Double) {
        hgBot.knockback(strength,x,z)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if(source.type()?.msgId.equals("player")){
            hgBot.hurt(source, amount)
        }
        return super.hurt(source, amount)

    }

    override fun canCollideWith(entity: Entity): Boolean {
        return false
    }

    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun die(damageSource: DamageSource) {
        super.die(damageSource)
        if(!hgBot.isDeadOrDying){
            hgBot.die(damageSource)
        }
    }

}
