package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.kit.kits.MagmaKit;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MagmaMixin {
    @Inject(
            method = "attack",
            at = @At("HEAD")
    )
    public void onAttackPlayer(Entity target, CallbackInfo ci) {
        MagmaKit.INSTANCE.onAttackEntity(target, (ServerPlayerEntity) (Object) this);
    }
}
