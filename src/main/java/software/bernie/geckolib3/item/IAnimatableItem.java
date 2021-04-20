package software.bernie.geckolib3.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

import software.bernie.geckolib3.core.IAnimatable;

public interface IAnimatableItem extends IAnimatable {
    String GECKO_LIB_ID_NBT = "GeckoLibID";

    @Nullable
    default Integer getId(ItemStack stack) {
        if (stack.hasTag()) {
            final CompoundNBT tag = stack.getTag();
            if (tag.contains(GECKO_LIB_ID_NBT, 99)) {
                return tag.getInt(GECKO_LIB_ID_NBT);
            }
        }
        return null;
    }
}
