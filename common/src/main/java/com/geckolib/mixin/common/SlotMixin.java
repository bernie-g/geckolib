package com.geckolib.mixin.common;

import com.geckolib.GeckoLibConstants;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/// Injection into container slots to handle ItemStack cloning in relation to GeckoLib ItemStack IDs
@Mixin(Slot.class)
public class SlotMixin {
	/// Remove the GeckoLib stack ID from a stack when copying it with middle-click<br/>
	/// This prevents split-stacks from having duplicate IDs, and allows for stacks to have their own IDs when created anew from an existing one
	@WrapOperation(method = "safeClone", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack geckolib$removeGeckolibIdOnCopy(ItemStack instance, int count, Operation<ItemStack> original) {
		final ItemStack copy = original.call(instance, count);
		
		if (copy.has(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get()))
			copy.remove(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get());
		
		return copy;
	}
}
