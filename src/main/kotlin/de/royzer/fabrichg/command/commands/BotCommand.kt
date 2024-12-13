package de.royzer.fabrichg.command.commands

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.brigadier.context.CommandContext
import de.royzer.fabrichg.bots.player.FakeServerPlayer
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.bots.skin.SkinManager
import de.royzer.fabrichg.command.sharedCommand
import de.royzer.fabrichg.server
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.Connection
import net.minecraft.network.PacketListener
import net.minecraft.network.PacketSendListener
import net.minecraft.network.ProtocolInfo
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.text.literal
import java.util.*


val hgbotCommand = sharedCommand("hgbot") {
    requiresPermissionLevel(1)
    argument<String>("name") { name ->
        runs {
            val botname = name().toString()
            if (botname.toString().length <= 16) {
                createBot(botname)
            }

        }
    }

}

private val playersWithSkin = listOf(
    "BastiGHG",
    "merkel",
    "noriskk",
    "notch",
    "asbach",
    "Asbach_URALT",
    "olaf_scholz",
    "r0yzer",
    "ASB4CH_URALT",
    "Hotkeyyy",
    "V8_BITURBO",
)

private var i = 0;

fun CommandContext<CommandSourceStack>.createBot(botname: String, skinName: String = botname) {
    val executor = source.player ?: return
    val world = executor.world
    val info = ClientInformation.createDefault()
    val uuid = SkinManager.getUuidByName(skinName)
    val skin = SkinManager.getSkinByUuid(uuid)
    if (skin == null) {
        // bei 2 mal gleichen geht nicht wiel gleiche uuid
        // wenn einer von denen online ist geht auch alles kaputt
        val newName = playersWithSkin.random()
        executor.sendSystemMessage("$skinName existiert nicht, skin von $newName wird genommen".literal)
        createBot(botname, newName)
        return
    }
    val newUUID = UUID.randomUUID()

    val profile = GameProfile(newUUID, botname)
    val value = skin.properties.first().value
    val signature = skin.properties.first().signature
    val map = profile.properties
    try {
        val oldSkin = map?.get("textures")?.iterator()?.next()
        map?.remove("textures", oldSkin)
    } catch (_: NoSuchElementException) {
    }
    profile.properties.put("textures", Property("textures", value, signature))
    val serverPlayer = FakeServerPlayer(profile)
    server.playerList.placeNewPlayer(
        FakeClientConnection(), serverPlayer, CommonListenerCookie.createInitial(profile, false)
    )
    serverPlayer.setPos(executor.pos)

    val hgBot = HGBot(world, botname, executor, serverPlayer = serverPlayer)
    executor.world.addFreshEntity(hgBot.apply {
        setPos(executor.pos)
        addEffect(
            MobEffectInstance(
                MobEffects.INVISIBILITY, Int.MAX_VALUE, 1, false, false, false
            )
        )
    })
    hgBot.isInvisible = true

}


class FakeClientConnection() : Connection(PacketFlow.CLIENTBOUND) {
    override fun setupOutboundProtocol(protocolInfo: ProtocolInfo<*>) {
    }

    override fun <T : PacketListener?> setupInboundProtocol(protocolInfo: ProtocolInfo<T>, packetInfo: T & Any) {
    }

    override fun send(packet: Packet<*>, listener: PacketSendListener?, flush: Boolean) {
    }

    override fun setListenerForServerboundHandshake(packetListener: PacketListener) {
    }
}

