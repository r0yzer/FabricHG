package de.royzer.fabrichg.bots

import com.mojang.authlib.GameProfile
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.server
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageSources
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import org.dizitart.kno2.filters.eq

class FakeServerPlayer(gameProfile: GameProfile) : ServerPlayer(server, server.overworld(), gameProfile, ClientInformation.createDefault() ) {

    lateinit var hgBot: HGBot

    init {
        this.hgPlayer.addKit(kits.random())
    }
    override fun knockback(strength: Double, x: Double, z: Double) {
        hgBot.knockback(strength,x,z)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if(source.type()!!.msgId.equals("player")){
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
