package software.bernie.geckolib.mixins.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.animatable.GeoItem;

/**
 * Injection into the equipment change handling to allow for bypassing GeckoLib ItemStack ID parity
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    /**
     * In {@code ItemStackMixin#geckolib$skipGeckolibIdOnCompare}, we tell Minecraft to ignore the contents of GeckoLib
     * stack ids for the purposes of ItemStack parity.
     * <p>
     * We temporarily reinstate it here so that the game syncs changes to this specific component
     */
    @WrapOperation(method = "equipmentHasChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean geckolib$allowLazyStackIdParity(ItemStack remoteStack, ItemStack localStack, Operation<Boolean> original) {
        return original.call(remoteStack, localStack) && geckolib$xnorGeckolibStackIds(remoteStack.getTag(), localStack.getTag());
    }

    @Unique
    private static boolean geckolib$xnorGeckolibStackIds(@Nullable CompoundTag tag1, @Nullable CompoundTag tag2) {
        return (tag1 == null ? -1 : tag1.getInt(GeoItem.ID_NBT_KEY)) == (tag2 == null ? -1 : tag2.getInt(GeoItem.ID_NBT_KEY));
    }
}
