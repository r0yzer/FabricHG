package de.royzer.fabrichg.bots

import net.minecraft.core.Vec3i
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.random.Random

class HGBotPathNavigation(val hgBot: HGBot, level: Level) : GroundPathNavigation(hgBot, level) {
    private fun shouldTargetNextNodeInDirection(vec: Vec3): Boolean {
        if (path!!.nextNodeIndex + 1 >= path!!.nodeCount) {
            return false
        } else {
            val vec3 = Vec3.atBottomCenterOf(path!!.nextNodePos)
            if (!vec.closerThan(vec3, 0.3)) {
                return false
            } else if (this.canMoveDirectly(vec, path!!.getNextEntityPos(this.mob))) {
                return true
            } else {
                val vec32 = Vec3.atBottomCenterOf(path!!.getNodePos(path!!.nextNodeIndex + 1))
                val vec33 = vec3.subtract(vec)
                val vec34 = vec32.subtract(vec)
                val d = vec33.lengthSqr()
                val e = vec34.lengthSqr()
                val bl = e < d
                val bl2 = d < 0.5
                if (!bl && !bl2) {
                    return false
                } else {
                    val vec35 = vec33.normalize()
                    val vec36 = vec34.normalize()
                    return vec36.dot(vec35) < 0.0
                }
            }
        }
    }
    override fun followThePath() {
        val vec3 = this.tempMobPos
        this.maxDistanceToWaypoint = if (mob.bbWidth > 0.75f) mob.bbWidth / 2.0f else 0.75f - mob.bbWidth / 2.0f
        val vec3i: Vec3i = path!!.nextNodePos
        val d = abs(mob.x - (vec3i.x.toDouble() + 0.5))
        val e = abs(mob.y - vec3i.y.toDouble())
        val f = abs(mob.z - (vec3i.z.toDouble() + 0.5))
        val bl =
            d < maxDistanceToWaypoint.toDouble() && f < maxDistanceToWaypoint.toDouble() && e < 1.0
        if (bl || this.canCutCorner(path!!.nextNode.type) && this.shouldTargetNextNodeInDirection(vec3)) {
            path!!.advance()
            if (Random.nextInt(10) == 5) hgBot.jump()
        }

        this.doStuckDetection(vec3)
    }}