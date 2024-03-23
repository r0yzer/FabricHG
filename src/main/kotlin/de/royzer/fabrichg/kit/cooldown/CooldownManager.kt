package de.royzer.fabrichg.kit.cooldown

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.silkmc.silk.core.text.literalText
import net.minecraft.server.level.ServerPlayer

val cooldownMap = HashMap<Cooldown, Double>()
private val cooldownCoroutineScope = CoroutineScope(Dispatchers.IO)

fun HGPlayer.activateCooldown(kit: Kit) {
    val cooldown = Cooldown(this, kit)
    cooldownMap[cooldown] = kit.cooldown ?: return
    cooldownCoroutineScope.launch scope@{
        while (hasCooldown(kit)) {
            delay(100)
            val time = cooldownMap[cooldown]?.minus(0.1) ?: break
            cooldownMap[cooldown] = time
        }
        cooldownMap.remove(cooldown)
    }
}
fun HGPlayer.hasCooldown(kit: Kit): Boolean {
    if (kit.cooldown == null) return false
    cooldownMap[Cooldown(this, kit)].let {
        return if (it == null) false
        else it > 0.0
    }
}

fun HGPlayer.cooldown(kit: Kit): Double {
    return if (!hasCooldown(kit)) 0.0
    else cooldownMap[Cooldown(this, kit)] ?: 0.0
}

fun ServerPlayer.sendCooldown(kit: Kit) {
    if (!hgPlayer.hasCooldown(kit)) return
    val sec = "%.1f".format(hgPlayer.cooldown(kit))
    sendSystemMessage(literalText {
        text("Du hast noch ")
        text(sec) {
            color = TEXT_BLUE
        }
        text(" Sekunden Cooldown")
        color = TEXT_GRAY
    })
}

fun HGPlayer.sendCooldown(kit: Kit) {
    if (!hasCooldown(kit)) return
    val sec = "%.1f".format(cooldown(kit))
    serverPlayer?.sendSystemMessage(literalText {
        text("Du hast noch ")
        text(sec) {
            color = TEXT_BLUE
        }
        text(" Sekunden Cooldown")
        color = TEXT_GRAY
    })
}