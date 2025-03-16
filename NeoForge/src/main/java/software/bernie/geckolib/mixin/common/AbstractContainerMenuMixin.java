package software.bernie.geckolib.mixin.common;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.animatable.GeoItem;

/**
 * Injection into the base container functionality to handle ItemStack duplication and splitting with GeckoLib stack identifiers
 */
@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    /**
     * Remove the GeckoLib stack ID from a stack when copying it with middle-click
     */
    @Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 1))
    public ItemStack geckolib$removeGeckolibIdOnCopy(ItemStack instance, int count) {
        ItemStack copy = instance.copyWithCount(count);

        if (copy.hasTag() && copy.getTag().contains(GeoItem.ID_NBT_KEY))
            copy.getTag().remove(GeoItem.ID_NBT_KEY);

        return copy;
    }

    /**
     * Force ItemStacks that don't match their GeckoLib stack ID to sync, even though GeckoLib tells the game they're equivalent
     */
    @Redirect(method = "synchronizeSlotToRemote", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean geckolib$forceGeckolibIdSync(ItemStack stack, ItemStack other) {
        return ItemStack.matches(stack, other) && geckolib$xnorGeckolibStackIds(stack.getTag(), other.getTag());
    }

    /**
     * Force ItemStacks that don't match their GeckoLib stack ID to trigger slot listeners, even though GeckoLib tells the game they're equivalent
     */
    @Redirect(method = "triggerSlotListeners", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean geckolib$forceGeckolibSlotChange(ItemStack stack, ItemStack other) {
        return ItemStack.matches(stack, other) && geckolib$xnorGeckolibStackIds(stack.getTag(), other.getTag());
    }

    @Unique
    private static boolean geckolib$xnorGeckolibStackIds(CompoundTag tag1, CompoundTag tag2) {
        return (tag1 == null ? -1 : tag1.getInt(GeoItem.ID_NBT_KEY)) == (tag2 == null ? -1 : tag2.getInt(GeoItem.ID_NBT_KEY));
    }
}
