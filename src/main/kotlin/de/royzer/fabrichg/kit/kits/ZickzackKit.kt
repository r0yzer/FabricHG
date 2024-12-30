package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.Items
import java.util.*

private const val ZICKZACK_COMBO_KEY = "zickzackCombo"


val zickzackKit = kit("Zickzack") {
    kitSelectorItem = Items.DIAMOND_BLOCK.defaultInstance

    description = "BastiGHG"

    val minCombo by property(3, "Min combo")
    val chanceMultiplier by property(8, "Dodge probability (combo * this)")

    onEnable { hgPlayer, kit, serverPlayer ->
        hgPlayer.playerData[ZICKZACK_COMBO_KEY] = hashMapOf<UUID, Int>()
    }

    onDisable { hgPlayer, kit ->
        hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.clear()
        hgPlayer.serverPlayer?.playNotifySound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundSource.PLAYERS, 1f, 1f)
    }

    kitEvents {
        onHitPlayer { hgPlayer, kit, hittedPlayer ->
            val combo = hgPlayer.combo(hittedPlayer.uuid)
            hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)
                ?.set(hittedPlayer.uuid, combo + 1) // muss != null sein
        }

        // return ob gecancelt werden soll
        onAttackedByPlayer { hgPlayer, kit, attacker ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onAttackedByPlayer false
            val combo = hgPlayer.combo(attacker.uuid)

            if (combo > minCombo) {
                // bei 3er combo 24% bei 10er 80% und dann pro dodge 1 runter
                // und wenn ein hit durch geht reset
                if (kotlin.random.Random.nextInt(100) < combo * chanceMultiplier) {
                    hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.set(attacker.uuid, combo - 1)
                    serverPlayer.playNotifySound(
                        SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE,
                        SoundSource.PLAYERS,
                        1f,
                        1f
                    )
                    attacker.playNotifySound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundSource.PLAYERS, 1f, 1f)
                    return@onAttackedByPlayer true
                } else {
                    hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.set(attacker.uuid, 0)
                }
            }

            return@onAttackedByPlayer false

        }
    }
}

private fun HGPlayer.combo(uuid: UUID): Int = getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.get(uuid) ?: 0
