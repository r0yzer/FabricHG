package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.PhantomKitKt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract public class PhantomMixin extends Entity {

    protected PhantomMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setSharedFlag(IZ)V"
            )
    )
    public void shouldGlide(LivingEntity instance, int i, boolean b) {
        if (instance instanceof ServerPlayer serverPlayer) {
            boolean s = PhantomKitKt.shouldGlide(serverPlayer);
            this.setSharedFlag(i, s);
        }
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void stopGlide(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayer serverPlayer) {
            PhantomKitKt.stopGlide(serverPlayer);
        }
    }
}
