package de.royzer.fabrichg.bots

import com.mojang.authlib.GameProfile
import com.mojang.datafixers.util.Pair
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.randomKit
import de.royzer.fabrichg.mixins.server.level.ChunkMapAccessor
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.ConfigManager
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

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


}
