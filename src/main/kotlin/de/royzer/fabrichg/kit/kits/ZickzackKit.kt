package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.Items
import java.util.*

private const val ZICKZACK_COMBO_KEY = "zickzackCombo"

// rr kein plan wieso das geht hab das aus der alten kitapi abgeschrieben

val zickzackKit = kit("Zickzack") {
    kitSelectorItem = Items.DIAMOND_BLOCK.defaultInstance

    description = "BastiGHG"

    val minCombo by property(3, "Min combo")
    val likelihood by property(20, "likelihood")

    onEnable { hgPlayer, kit, serverPlayer ->
        hgPlayer.playerData[ZICKZACK_COMBO_KEY] = hashMapOf<UUID, Int>()
    }

    onDisable { hgPlayer, kit ->
        hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.clear()
        hgPlayer.serverPlayer?.playNotifySound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundSource.PLAYERS, 1f, 1f)
    }

    kitEvents {
        onHitPlayer { hgPlayer, kit, hittedPlayer ->
            val combo = hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.get(hittedPlayer.uuid) ?: 0

            if (combo < likelihood) {  // ist wenn kein element drin ist == 0 also auch true
                hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.set(hittedPlayer.uuid, combo + 1) // muss != null sein
            }

        }

        // return ob gecancelt werden soll
        onAttackedByPlayer { hgPlayer, kit, attacker ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onAttackedByPlayer false
            val combo = hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.get(attacker.uuid) ?: 0

            if (combo > minCombo) {
                val chance = Random().nextInt(likelihood) + 1
                if (chance > likelihood - combo) {
                    hgPlayer.getPlayerData<HashMap<UUID, Int>>(ZICKZACK_COMBO_KEY)?.set(attacker.uuid, combo - 1)
                    serverPlayer.playNotifySound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundSource.PLAYERS, 1f, 1f)
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