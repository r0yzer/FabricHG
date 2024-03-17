package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.bots.HGBot;
import de.royzer.fabrichg.game.phase.phases.LobbyPhase;
import de.royzer.fabrichg.kit.KitItemKt;
import de.royzer.fabrichg.kit.events.kit.OnMoveKt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

//    @Inject(
//            method = "dropItem(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/entity/ItemEntity;",
//            at = @At("HEAD")
//    )
//    public void onDropItem(ItemConvertible item, CallbackInfoReturnable<ItemEntity> cir) {}

    @Inject(
            method = "interact",
            at = @At("HEAD")
    )
    public void onInteract(Player clickingPlayer, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        KitItemKt.onClickAtEntity(clickingPlayer, hand, (Entity) (Object) this, cir);
    }


    @Inject(
            method = "moveTo(DDDFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onMoveTo(double d, double e, double f, float g, float h, CallbackInfo ci) {
        if (LobbyPhase.INSTANCE.isStarting()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "move",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onMove(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if (LobbyPhase.INSTANCE.isStarting()) {
            Entity entity = ((Entity) (Object) this);
            if (entity instanceof ServerPlayer serverPlayer) {
//                if (serverPlayer.onGround()) {
//                    serverPlayer.connection.send(new ClientboundPlayerPositionPacket(serverPlayer.xOld, serverPlayer.yOld, serverPlayer.zOld, serverPlayer.getYRot(), serverPlayer.getXRot(), RelativeMovement.ALL, 1));
//                }
            }
            ci.cancel();
        }
        if (movementType.equals(MoverType.PLAYER)) {
            boolean isServerPlayer = (Entity) (Object) (this) instanceof ServerPlayer;
            boolean isHgBot = (Entity) (Object) (this) instanceof HGBot;
            if (isServerPlayer || isHgBot) {
                if (!movement.equals(Vec3.ZERO)) {
                    OnMoveKt.onMove((Entity) (Object) (this));
                }
            }
        }
    }
}
