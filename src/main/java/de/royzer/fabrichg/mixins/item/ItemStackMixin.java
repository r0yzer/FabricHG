package de.royzer.fabrichg.mixins.item;

import de.royzer.fabrichg.game.GamePhaseManager;
import de.royzer.fabrichg.game.phase.PhaseType;
import de.royzer.fabrichg.kit.KitItemKt;
import de.royzer.fabrichg.mixinskt.KitSelector;
import de.royzer.fabrichg.mixinskt.SoupHealingKt;
import de.royzer.fabrichg.mixinskt.Tracker;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        KitItemKt.onClick(user, itemStack, cir);
        if (itemStack.getItem().equals(Items.CHEST)) {
            KitSelector.INSTANCE.onClick(user, itemStack, cir, world, hand);
        }
        if (GamePhaseManager.INSTANCE.isBuildingForbidden()) {
            cir.setReturnValue(InteractionResultHolder.pass(itemStack));
            return;
        }
        if (itemStack.getItem().isEdible()) {
            SoupHealingKt.INSTANCE.onPotentialSoupUse(user, itemStack.getItem(), cir, world, hand);
        }
        if (itemStack.getItem() == Items.COMPASS) {
            Tracker.INSTANCE.onTrackerUse(user, itemStack, cir, world, hand);
        }
    }

    @Inject(
            method = "useOn",
            at = @At("HEAD")
    )
    public void useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        KitItemKt.onUseBlock(context.getPlayer(), context);
    }
}
