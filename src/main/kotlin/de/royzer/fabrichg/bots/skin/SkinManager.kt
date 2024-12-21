package de.royzer.fabrichg.bots.skin

import de.royzer.fabrichg.mixins.server.level.ChunkMapAccessor
import de.royzer.fabrichg.server
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.core.server.players
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection


object SkinManager {

    val MOJANG_NAME2UUID = "https://api.mojang.com/users/profiles/minecraft/"
    val MOJANG_UUID2SKIN = "https://sessionserver.mojang.com/session/minecraft/profile/UUID?unsigned=false"


    fun getUuidByName(name: String): String {
        val url = URL(MOJANG_NAME2UUID + name)
        val result = urlRequest(url) ?: return ""
        return Json.decodeFromString<UuidRequestData>(result).id
    }

    fun getSkinByUuid(uuid: String): SkinRequestData? {
        val url = URL(MOJANG_UUID2SKIN.replace("UUID", uuid))
        val result = urlRequest(url) ?: return null
        return Json.decodeFromString<SkinRequestData>(result)
    }

    fun sendProfileUpdates(player: ServerPlayer) {
        if (server.overworld().isClientSide()) return
        server.playerList.broadcastAll(ClientboundPlayerInfoRemovePacket(listOf(player.uuid)))
        server.playerList.broadcastAll(
            ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                player
            )
        )
        val storage = server.overworld().chunkSource.chunkMap

        val entry = (storage as ChunkMapAccessor).entityMap.get(player.id)
        PlayerLookup.tracking(player).forEach { tracking ->
            entry.serverEntity.addPairing(tracking)
        }
    }


    @Throws(IOException::class)
    fun urlRequest(url: URL): String? {
        val connection = url.openConnection() as HttpsURLConnection

        var reply: String? = null

        connection.useCaches = false
        connection.doOutput = true
        connection.doInput = true
        connection.requestMethod = "GET"
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.use { `is` ->
                InputStreamReader(`is`).use { isr ->
                    Scanner(isr).use { scanner ->
                        val replyBuilder = StringBuilder()
                        while (scanner.hasNextLine()) {
                            val line: String = scanner.next()
                            if (line.trim { it <= ' ' }.isEmpty()) continue
                            replyBuilder.append(line)
                        }
                        reply = replyBuilder.toString()
                    }
                }
            }
        }
        connection.disconnect()

        return reply
    }

}