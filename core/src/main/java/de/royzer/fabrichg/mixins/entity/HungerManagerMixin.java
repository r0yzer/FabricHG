package de.royzer.fabrichg.mixins.entity;

import de.royzer.fabrichg.game.FabricHGRuntimeKt;
import kotlin.random.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class HungerManagerMixin {
//    @Inject(
//            method = "setFoodLevel",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    public void setHunger(int foodLevel, CallbackInfo ci) {
//        if (GamePhaseManager.INSTANCE.getCurrentPhaseType().equals(PhaseType.LOBBY)) {
//            ci.cancel();
//        }
//    }

    @Shadow private int lastFoodLevel;

    @Shadow private int foodLevel;

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setHunger(Player player, CallbackInfo ci) {
        if (FabricHGRuntimeKt.fabricHGRuntime.isNotStarted()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    public void lessHunger(Player player, CallbackInfo ci) {
        int diff = lastFoodLevel - foodLevel;
        if (diff <= 0) return;

        boolean applicable = diff == 1;
        double hungerRate = 0.15;

        if (Random.Default.nextDouble() > hungerRate && applicable) {
            foodLevel = lastFoodLevel;
        }
    }
}
