package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.combatlog.combatloggedPlayers
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.teams.HGTeam
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.MapRenderer
import de.royzer.fabrichg.util.cloudnet.CloudNetManager
import de.royzer.fabrichg.util.toHighestPos
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.entity.changePos
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.logging.logError
import net.silkmc.silk.core.logging.logInfo
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class EndPhase(private val winnable: HGWinnable?) : GamePhase() {

    val endTime by lazy { GamePhaseManager.timer.get() }

    override fun init() {
        GamePhaseManager.server.motd = "${GamePhaseManager.MOTD_STRING}\nCURRENT GAME PHASE: \u00A74END"
        combatloggedPlayers.forEach { (_, u) -> u.cancel() }
        endTime
        GamePhaseManager.resetTimer()
        placeKuchen()

        winnable?.winners?.forEach { winner ->
            with(winner.serverPlayer ?: return) {
                connection.send(ClientboundPlayerAbilitiesPacket(abilities.also {
                    it.flying = true
                    it.mayfly = true
                }))
                addEffect(MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false))
            }
            winner.updateStats(wins = 1)


            try {
                val imageUrl = "https://i.imgur.com/o2alDIv.png" // kp von wo sonst discord webp geht nciht

                winner.serverPlayer?.let {
                    it.setItemInHand(
                        InteractionHand.OFF_HAND, MapRenderer.render(
                            ImageIO.read(URL(imageUrl)),
                            it
                        )
                    )
                }
            } catch (e: Exception) {
                logError("failed to get image")
                e.printStackTrace()
            }
        }
    }

    fun placeKuchen() {
        val size = 3

        val starExplosion = FireworkExplosion(
            FireworkExplosion.Shape.STAR,
            IntArrayList(listOf(0xFF0000)),
            IntArrayList(listOf(0xFF0000)),
            true,
            true
        )
        val creeperExplosion = FireworkExplosion(
            FireworkExplosion.Shape.CREEPER,
            IntArrayList(listOf(0x00FF00)),
            IntArrayList(listOf(0x00FF00)),
            true,
            true
        )
        val burstExplosion = FireworkExplosion(
            FireworkExplosion.Shape.BURST,
            IntArrayList(listOf(0x0000FF)),
            IntArrayList(listOf(0x0000FF)),
            true,
            true
        )

        val explosions = listOf(starExplosion, creeperExplosion, burstExplosion)

        val player = winnable?.winners?.map { it.serverPlayer }?.filterNotNull()?.firstOrNull() ?: return

        val y = min(server.overworld().height - 10, BlockPos(0, 0, 0).toHighestPos().y + 50)

        server.playerList.players.forEach { player ->
            player.changePos(0, y + 1, 0, server.overworld())
        }

        mcCoroutineTask(howOften = 5, period = 1.seconds) { task ->
            for (x in -size..size) {
                for (z in -size..size) {
                    server.overworld().setBlockAndUpdate(BlockPos(x, y, z), Blocks.CAKE.defaultBlockState())

                    val fireworkStack = itemStack(Items.FIREWORK_ROCKET) {
                        set(
                            DataComponents.FIREWORKS,
                            Fireworks(Random.nextInt(1, 3), listOf(explosions.random()))
                        )
                    }

                    val fireworkRocketEntity = FireworkRocketEntity(
                        server.overworld(),
                        player,
                        x.toDouble(),
                        y.toDouble(),
                        z.toDouble(),
                        fireworkStack.copy()
                    );
                    server.overworld().addFreshEntity(fireworkRocketEntity);
                }
            }
        }
    }

    override fun tick(timer: Int) {
        broadcastComponent(winnable?.text ?: "Kein sieger?".literal)
//        if (timer == maxPhaseTime - 1) {
//            GamePhaseManager.server.playerList.players.forEach {
//                it.connection.disconnect(literalText("Der Server startet neu") { color = 0xFF0000 })
//            }
//        }
        if (timer == maxPhaseTime) {
            logInfo("Spiel endet")
            logInfo("Sieger: ${winnable?.winners?.map { it.name }}, Kills: ${winnable?.winners?.sumOf { it.kills }}")

            runCatching {
                CloudNetManager.stopCloudNetService()
            }.onFailure {
                GamePhaseManager.server.halt(false)
            }
            return
        }
    }

    override val phaseType = PhaseType.END
    override val maxPhaseTime = 20
    override val nextPhase: GamePhase? = null
}

fun playerInfoText(player: HGPlayer): Component {
    return literalText {
        text("Kills: ${player.kills}\n") {
            color = 0x00FF51
        }
        text("Kit(s): ") {
            color = 0x42FF51
            text(player.kits.joinToString { it.name })
        }
    }
}

fun teamInfoText(team: HGTeam): Component {
    return literalText {
        text("Kills: ${team.hgPlayers.sumOf { it.kills }}\n") {
            color = 0x00FF51
        }
        text("Player(s): ") {
            color = 0x42FF51
            text(team.hgPlayers.joinToString { it.name })
        }
    }
}