package de.royzer.fabrichg.mixins.kits;

import de.royzer.fabrichg.data.hgplayer.HGPlayer;
import de.royzer.fabrichg.game.PlayerList;
import de.royzer.fabrichg.kit.kits.MagmaKit;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ServerPlayerEntity.class)
public class MagmaMixin {
    @Inject(
            method = "attack",
            at = @At("HEAD")
    )
    public void onAttackPlayer(Entity target, CallbackInfo ci) {
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;
        HGPlayer hgPlayer = PlayerList.INSTANCE.getPlayer(serverPlayerEntity.getUuid());
        if (hgPlayer.hasKit(MagmaKit.INSTANCE)) {
            if (ThreadLocalRandom.current().nextInt(1,4) == 3) {
                target.setFireTicks(40);
            }
        }
    }
}
