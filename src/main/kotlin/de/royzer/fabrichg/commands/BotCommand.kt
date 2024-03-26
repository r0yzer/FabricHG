package de.royzer.fabrichg.commands

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.royzer.fabrichg.bots.FakeServerPlayer
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.bots.skin.SkinManager
import de.royzer.fabrichg.server
import net.minecraft.network.Connection
import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.world.effect.MobEffects
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import java.util.*


val hgbotCommand = command("hgbot") {
    requiresPermissionLevel(1)
    argument("name") { name ->
        runs {
            val botname = name()
            if (botname.toString().length <= 16) {
                val world = source.player?.world
                val info = net.minecraft.server.level.ClientInformation.createDefault()
                val uuid = SkinManager.getUuidByName(botname)
                val skin = SkinManager.getSkinByUuid(uuid)!!
                val newUUID = buildString {
                    append(uuid.substring(0, 8))
                    append("-")
                    append(uuid.substring(8, 12))
                    append("-")
                    append(uuid.substring(12, 16))
                    append("-")
                    append(uuid.substring(16, 20))
                    append("-")
                    append(uuid.substring(20))
                }

                println(newUUID)

                val profile = GameProfile(UUID.fromString(newUUID), botname)
                val value = skin.properties.first().value
                val signature = skin.properties.first().signature
//        skinTag.putString("value", skin.properties.first().value)
//        skinTag.putString("signature", skin.properties.first().signature)
                val map = profile.properties
                try {

                    val oldSkin = map?.get("textures")?.iterator()?.next()
                    map?.remove("textures", oldSkin)
                } catch (_: NoSuchElementException) {
                }
                profile.properties.put("textures", Property("textures", value, signature))
                val serverPlayer = FakeServerPlayer(profile)
                server.playerList.placeNewPlayer(
                    FakeClientConnection(),
                    serverPlayer,
                    CommonListenerCookie.createInitial(profile)
                )
                serverPlayer.setPos(source!!.player!!.pos)

                val hgBot = HGBot(world!!, botname, source.player!!, serverPlayer = serverPlayer)
                source.player?.world?.addFreshEntity(hgBot.apply {
                    setPos(source!!.player!!.pos)
                    addEffect(
                        net.minecraft.world.effect.MobEffectInstance(
                            MobEffects.INVISIBILITY,
                            Int.MAX_VALUE,
                            1,
                            false,
                            false,
                            false
                        )
                    )
                })
                hgBot.isInvisible = true

                //SkinManager.sendProfileUpdates(serverPlayer)

                // PlayerList.players[hgBot.uuid] = HGPlayer(hgBot.uuid, botname)
                // PlayerList.players[hgBot.uuid]?.kits?.add(randomKit())
            }

        }
    }

    runs {


    }

//    runs {
//        val world = source.player?.world
//        val hgBot = HGBot(world!!, "HGBot", source.player!!, serverPlayer = serverPlayer)
//        source.player?.world?.addFreshEntity(hgBot.apply {
//            setPos(source!!.player!!.pos)
//        })
//        PlayerList.players[hgBot.uuid] = HGPlayer(hgBot.uuid, "HGBot")
//        PlayerList.players[hgBot.uuid]?.kits?.add(beerKit)
//    }
}

class FakeClientConnection constructor() : Connection(PacketFlow.CLIENTBOUND) {
    override fun setListener(packetListener: PacketListener) {
    }
}

