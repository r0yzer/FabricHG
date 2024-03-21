package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.kit.events.kit.OnSneakKt;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public class PlayerMixin {

    @Redirect(
            method = "updatePlayerPose",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;setPose(Lnet/minecraft/world/entity/Pose;)V")
    )
    void abc(Player instance, Pose pose) {
        if (instance.getPose() != Pose.CROUCHING && pose == Pose.CROUCHING) {
            OnSneakKt.onSneak(instance, pose);
        }
        instance.setPose(pose);
    }
}
