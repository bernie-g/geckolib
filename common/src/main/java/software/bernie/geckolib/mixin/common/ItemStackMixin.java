package software.bernie.geckolib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Injection into ItemStack functionality to handle duplication and splitting with GeckoLib stack identifiers
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {
    /**
     * Remove the GeckoLib stack ID when splitting up a stack into two
     */
    @WrapOperation(method = "split", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack geckolib$removeGeckolibIdOnCopy(ItemStack instance, int count, Operation<ItemStack> original) {
        ItemStack copy = original.call(instance, count);

        if (count < instance.getCount() && copy.has(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get()))
            copy.remove(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get());

        return copy;
    }

    /**
     * Consider ItemStacks equal if the only difference is their GeckoLib stack ID
     * <p>
     * We do this so that the game doesn't prevent combining stacks due solely to GeckoLib sync IDs.
     */
    @WrapOperation(method = "isSameItemSameComponents", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private static boolean geckolib$skipGeckolibIdOnCompare(Object a, Object b, Operation<Boolean> original) {
        if (original.call(a, b))
            return true;

        if (!(a instanceof PatchedDataComponentMap components) || !(b instanceof PatchedDataComponentMap components2))
            return false;

        return GeckoLibUtil.areComponentsMatchingIgnoringGeckoLibId(components, components2);
    }
}
