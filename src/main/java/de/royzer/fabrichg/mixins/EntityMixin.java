package de.royzer.fabrichg.mixins;

import de.royzer.fabrichg.game.phase.phases.LobbyPhase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
//    @Inject(
//            method = "move",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    public void onMove(MovementType movementType, Vec3d movement, CallbackInfo ci) {
//        if (LobbyPhase.INSTANCE.isStarting() && ((Object) this) instanceof ServerPlayerEntity ) {
//            ((ServerPlayerEntity) (Object) this).setMovementSpeed(0F);
//            ci.cancel();
//        }
//    }
}
