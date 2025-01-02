package de.royzer.fabrichg.mixins.entitylookup;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import net.silkmc.silk.core.logging.LoggingKt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(EntityLookup.class)
public class EntityLookupMixin<T extends EntityAccess> {
    @Shadow @Final private Map<UUID, T> byUuid;

    @Shadow @Final private Int2ObjectMap<T> byId;

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void fixRemove(T entity, CallbackInfo ci) {
        try {
            this.byUuid.remove(entity.getUUID());

            if (this.byId.containsKey(entity.getId())) {
                this.byId.remove(entity.getId());
            } else {
                LoggingKt.logWarning("tried removing entity by id that does not exist");
                LoggingKt.logWarning("entity: " + entity);
                LoggingKt.logWarning("entity id: " + entity.getId());
                LoggingKt.logWarning("by id map: " + this.byId);
            }
        } catch (Exception e) {
            LoggingKt.logWarning("Error while removing entity: " + e);
            e.printStackTrace();
        }

        ci.cancel();
    }
}
