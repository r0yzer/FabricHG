package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.util.toVec3
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.blockPos
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.math.geometry.produceCirclePositions
import net.silkmc.silk.core.math.geometry.produceFilledCirclePositions
import net.silkmc.silk.core.task.mcCoroutineTask


private val fightKey = "isInGladiatorFight"

val gladiatorKit = kit("Gladiator") {
    kitSelectorItem = Items.IRON_BARS.defaultInstance
    usableInInvincibility = false
    cooldown = 45.0

    val radius by property(15, "Gladi box radius")
    val height by property(10, "Gladi box height")

    description = "Do a 1v1"

    kitItem {
        itemStack = kitSelectorItem

        onClickAtPlayer { hgPlayer, kit, clickedPlayer, hand ->
            val player1 = hgPlayer.serverPlayer ?: return@onClickAtPlayer
            if (hgPlayer.getPlayerData<Boolean>(fightKey) == true) {
                return@onClickAtPlayer
            }
            if (clickedPlayer.hgPlayer.getPlayerData<Boolean>(fightKey) == true) {
                return@onClickAtPlayer
            }
            if (clickedPlayer.hgPlayer.isNeo) {
                return@onClickAtPlayer
            }
            val fight = GladiatorFight(player1, clickedPlayer, radius, height)
            fight.start()
            val task = mcCoroutineTask(sync = false, howOften = Long.MAX_VALUE, period = 20.ticks) {
                fight.tick()
            }
            fight.job = task // das ist niemals best practice so
        }
    }
}

// gucken ob geung platz für die box
// das mit dem rot gelb grün glas
class GladiatorFight(val player1: ServerPlayer, val player2: ServerPlayer, val radius: Int, val height: Int) {

    val startPos1 = player1.pos
    val startPos2 = player2.pos

    val pos = player1.world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, player1.blockPos)
    val fightCenterPos = pos.subtract(BlockPos(0, -40, 0)) // sollte passen

    var timer = 0

    lateinit var job: Job


    fun start() {

        // hoffe das geht so wegen damit kein combatlog
        player1.combatTracker.recordDamage(player1.damageSources().playerAttack(player2), 0.0f)
        player2.combatTracker.recordDamage(player2.damageSources().playerAttack(player1), 0.0f)

        player1.hgPlayer.playerData[fightKey] = true
        player2.hgPlayer.playerData[fightKey] = true

        fightCenterPos.produceFilledCirclePositions(radius) {// boden
            player1.server.overworld().setBlockAndUpdate(BlockPos(it.x, it.y, it.z), Blocks.GLASS.defaultBlockState())
        }
        repeat(height) { i ->
            fightCenterPos.produceCirclePositions(radius) {// wand
                player1.server.overworld()
                    .setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.GLASS.defaultBlockState())
            }
        }
        fightCenterPos.produceFilledCirclePositions(radius) {// dach
            player1.server.overworld()
                .setBlockAndUpdate(BlockPos(it.x, it.y + height, it.z), Blocks.GLASS.defaultBlockState())
        }
        fightCenterPos.produceFilledCirclePositions(3) {// loch
            player1.server.overworld()
                .setBlockAndUpdate(BlockPos(it.x, it.y + 10, it.z), Blocks.AIR.defaultBlockState())
        }


        val p = fightCenterPos.toVec3()
        val distanceFromMiddle = radius / 2 - 1
        player1.teleportTo(player1.serverLevel(), p.x, p.y + 1, p.z + distanceFromMiddle, -180f, 0f)
        player2.teleportTo(player1.serverLevel(), p.x, p.y + 1, p.z - distanceFromMiddle, 0f, 0f)
    }

    fun end() {
        player1.hgPlayer.playerData[fightKey] = false
        player2.hgPlayer.playerData[fightKey] = false

        if (player1.hgPlayer.isAlive) {
            if (!player2.hgPlayer.isAlive) {
                player1.hgPlayer.activateCooldown(gladiatorKit)
            }
            player1.teleportTo(startPos1.x, startPos1.y, startPos1.z)
            player1.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 3, 5, false, false, false))
        }
        if (player2.hgPlayer.isAlive) {
            player2.teleportTo(startPos2.x, startPos2.y, startPos2.z)
            player2.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 3, 5, false, false, false))
        }


        repeat(height + 1) { i ->
            fightCenterPos.produceFilledCirclePositions(radius + 1) {
                player1.server.overworld()
                    .setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.AIR.defaultBlockState())
            }
        }

        job.cancel("AAAAAAAAAHHHHHHHHH BLUEFIREOLY")
    }

    fun tick() {
        timer++

        if (timer >= 150) {
            player1.addEffect(MobEffectInstance(MobEffects.WITHER, 20, 1, false, false, false))
            player2.addEffect(MobEffectInstance(MobEffects.WITHER, 20, 1, false, false, false))
        }

        // y checken
        if (player1.y < fightCenterPos.y || player2.y < fightCenterPos.y || player1.y > fightCenterPos.y + height + 2 || player2.y > fightCenterPos.y + height + 2) {
            end()
            return
        }

        // xz distanz gucken ist ja n kreis
        val player1XZPos = Vec3(player1.pos.x, fightCenterPos.y.toDouble(), player1.pos.z)
        if (player1XZPos.distanceTo(fightCenterPos.toVec3()) > radius + 1) {
            end()
            return
        }
        val player2XZPos = Vec3(player2.pos.x, fightCenterPos.y.toDouble(), player2.pos.z)

        if (player2XZPos.distanceTo(fightCenterPos.toVec3()) > radius + 1) {
            end()
            return
        }
        if (!PlayerList.alivePlayers.contains(player1.hgPlayer) || !PlayerList.alivePlayers.contains(player2.hgPlayer)) {
            end()
            return
        }

        // noch nicht getestet aber sollte passen
        PlayerList.alivePlayers.forEach { hgPlayer ->
            val player = hgPlayer.serverPlayer ?: return@forEach
            if (player == player1 || player == player2) {
                return@forEach
            }
            if (player.y > fightCenterPos.y && player.y < fightCenterPos.y + height) {
                val playerXZPos = player.pos.also { it.subtract(0.0, (player.pos.y - fightCenterPos.y), 0.0) }
                if (playerXZPos.distanceTo(fightCenterPos.toVec3()) < radius) {
                    player.addEffect(MobEffectInstance(MobEffects.WITHER, 22, 1, false, false, false))
                }
            }
        }
    }

}
