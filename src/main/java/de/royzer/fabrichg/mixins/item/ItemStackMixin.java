package de.royzer.fabrichg.mixins.item;

import de.royzer.fabrichg.game.GamePhaseManager;
import de.royzer.fabrichg.game.phase.PhaseType;
import de.royzer.fabrichg.kit.KitItemKt;
import de.royzer.fabrichg.mixinskt.KitSelector;
import de.royzer.fabrichg.mixinskt.SoupHealingKt;
import de.royzer.fabrichg.mixinskt.Tracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    public void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        KitItemKt.onClick(user, itemStack, cir);
        if (itemStack.getItem().equals(Items.CHEST)) {
            KitSelector.INSTANCE.onClick(user, itemStack, cir, world, hand);
        }
        if (GamePhaseManager.INSTANCE.getCurrentPhaseType().equals(PhaseType.LOBBY)) {
            cir.setReturnValue(TypedActionResult.pass(itemStack));
            return;
        }
        if (itemStack.getItem().isFood()) {
            SoupHealingKt.INSTANCE.onPotentialSoupUse(user, itemStack.getItem(), cir, world, hand);
        }
        if (itemStack.getItem() == Items.COMPASS) {
            Tracker.INSTANCE.onTrackerUse(user, itemStack, cir, world, hand);
        }
    }

    @Inject(
            method = "useOnBlock",
            at = @At("HEAD")
    )
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir){
        KitItemKt.onUseBlock(context.getPlayer(), context);
    }
}
