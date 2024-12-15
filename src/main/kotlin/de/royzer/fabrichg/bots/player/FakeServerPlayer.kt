package de.royzer.fabrichg.bots.player

import com.mojang.authlib.GameProfile
import com.mojang.datafixers.util.Pair
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.gulag.GulagManager
import de.royzer.fabrichg.kit.randomKit
import de.royzer.fabrichg.mixins.entity.EntityAcessor
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.ConfigManager
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.RelativeMovement
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.portal.DimensionTransition

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
        println("fake player hurt: $amount")

        if ((health - amount) <= 0) {
            val cancelDeath = GulagManager.onDeath(source.entity, this)

            if (cancelDeath) return false
        }

        if(health > 0){
            hgBot.hurt(source, amount)
        }

        return super.hurt(source, amount)
    }

    override fun unsetRemoved() {
        println("unset remove")
        (hgBot as EntityAcessor).removeRemoval()
        super.unsetRemoved()
    }

    override fun remove(reason: RemovalReason) {
        println("fake player remove: $reason, hgbot health: ${hgBot.health} ($hgBot)")

        hgBot.remove(reason)
        super.remove(reason)
    }

    fun hurtFromHGBot(source: DamageSource, amount: Float){
        super.hurt(source,amount)
    }

    override fun canCollideWith(entity: Entity): Boolean {
        return false
    }

    override fun canBeCollidedWith(): Boolean {
        return false
    }

    fun justDie(damageSource: DamageSource) {
        println("fake player just die")
        // ausrastungs busting
        super.health = 0f
        super.kill()
        super.die(damageSource)
        super.remove(RemovalReason.KILLED)
    }

    override fun die(damageSource: DamageSource) {
        println("fake player die")
        hgBot.die(damageSource)
        super.die(damageSource)
    }

    override fun setItemSlot(slot: EquipmentSlot, stack: ItemStack) {
        super.setItemSlot(slot, stack)
        sendUpdateEquipmentPackets()
    }

    private fun sendUpdateEquipmentPackets(){
        val list: java.util.ArrayList<Pair<EquipmentSlot, ItemStack>> =
            ArrayList()
        EquipmentSlot.entries.forEach {
            list.add(Pair(it, getItemBySlot(it) ?: Items.AIR.defaultInstance!!))
        }
        server.playerList.broadcastAll(ClientboundSetEquipmentPacket(id, list))
    }

    override fun allowsListing(): Boolean {
        return true
    }

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

    override fun teleportTo(newLevel: ServerLevel, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        // minecraft busting

        /*hgBot.changeDimension(
            DimensionTransition(newLevel, hgBot) {
                hgBot.teleportTo(x, y, z)
            }
        )*/

        hgBot.level().add

        newLevel.addFreshEntity(hgBot)
        hgBot.teleportTo(x, y, z)

        super.teleportTo(newLevel, x, y, z, yaw, pitch)
    }

    override fun kill() {
        println("fake player kill")
        //hgBot.kill()
        super.kill()
    }

}
