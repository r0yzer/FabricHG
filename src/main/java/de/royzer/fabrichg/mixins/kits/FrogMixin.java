package de.royzer.fabrichg.mixins.kits;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingHook.class)
public abstract class FrogMixin {
//    @Inject(
//            method = "tick",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;getPlayerOwner()Lnet/minecraft/world/entity/player/Player;"),
//            cancellable = true)
//    public void alhambra(CallbackInfo ci) {
//        if (((FishingHook) (Object) this).getTags().contains("frogBobber")) {
//            ci.cancel();
//        }
//    }

    @Shadow protected abstract boolean shouldStopFishing(Player player);

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;shouldStopFishing(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    public boolean hansentertainment(FishingHook instance, Player player) {
        if (((FishingHook) (Object) this).getTags().contains("frogBobber")) {
            return false;
        }
        return shouldStopFishing(player);
    }
}
