package de.royzer.fabrichg.kit

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Cooldown(
    val hgPlayer: HGPlayer,
    val kit: Kit,
)

private val cooldownMap = HashMap<Cooldown, Double>()
private val cooldownCoroutineScope = CoroutineScope(Dispatchers.IO)

fun HGPlayer.startCooldown(kit: Kit) {
    val cooldown = Cooldown(this, kit)
    cooldownMap[cooldown] = kit.cooldown ?: return
    cooldownCoroutineScope.launch scope@{
        while (hasCooldown(kit)) {
            delay(100)
            cooldownMap[cooldown] = cooldownMap[cooldown]!!.minus(0.1)
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