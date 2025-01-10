package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.bots.HGBot;
import de.royzer.fabrichg.data.hgplayer.HGPlayer;
import de.royzer.fabrichg.data.hgplayer.HGPlayerKt;
import de.royzer.fabrichg.kit.events.kit.invoker.OnMoveKt;
import de.royzer.fabrichg.kit.events.kititem.invoker.OnClickAtEntityWithKitItemKt;
import de.royzer.fabrichg.kit.kits.DemomanKitKt;
import de.royzer.fabrichg.kit.kits.GamingGolem;
import de.royzer.fabrichg.kit.kits.GamingGolemWolf;
import de.royzer.fabrichg.kit.kits.TankKitKt;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
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
        OnClickAtEntityWithKitItemKt.onClickAtEntity(clickingPlayer, hand, (Entity) (Object) this, cir);
    }

    @Inject(
            method = "move",
            at = @At("HEAD")
    )
    public void onMove(MoverType movementType, Vec3 movement, CallbackInfo ci) {
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

    @Inject(
            method = "removePassenger",
            at = @At("TAIL")
    )
    public void onDismount(Entity passenger, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetPassengersPacket(serverPlayer));
        }
    }

    @Inject(
            method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"),
            cancellable = true)
    public void dripstoneSpawn(ItemStack stack, float offsetY, CallbackInfoReturnable<ItemEntity> cir) {
        if (stack.getItem().equals(Items.POINTED_DRIPSTONE)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
            method = "ignoreExplosion",
            at = @At("HEAD"),
            cancellable = true
    )
    public void kitIgnoreExplosions(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        HGPlayer hgPlayer = HGPlayerKt.getHgPlayer(entity);

        if (hgPlayer == null) return;

        if (hgPlayer.canUseKit(TankKitKt.getTankKit())) cir.setReturnValue(true);
        if (hgPlayer.canUseKit(DemomanKitKt.getDemomanKit())) cir.setReturnValue(true);
    }


    // GAMBLER GAMING GOLEM
    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    public void useGolemCollision(Vec3 vec, CallbackInfoReturnable<Vec3> cir) {
        if (((Object) this) instanceof GamingGolemWolf) {
            GamingGolem golem = ((GamingGolemWolf) (Object) this).getGolem();
            cir.setReturnValue(((EntityAcessor) (Object) golem).collisionVector(vec));
        }
    }
}
