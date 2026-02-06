package software.bernie.geckolib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.*;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.HashedStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.GeckoLibConstants;

import java.util.Optional;

/// Injection into the HashedStack handling to allow for bypassing GeckoLib ItemStack ID parity
@Mixin(HashedStack.ActualItem.class)
public class HashedStackMixin {
    /// In `ItemStackMixin#geckolib$skipGeckolibIdOnCompare`, we tell Minecraft to ignore the contents of GeckoLib
    /// stack ids for the purposes of ItemStack parity.
    ///
    /// We temporarily reinstate it here so that the game syncs changes to this specific component
    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/HashedPatchMap;matches(Lnet/minecraft/core/component/DataComponentPatch;Lnet/minecraft/network/HashedPatchMap$HashGenerator;)Z"))
    public boolean geckolib$allowLazyStackIdParity(HashedPatchMap patchMap, DataComponentPatch componentPatch, HashedPatchMap.HashGenerator hasher, Operation<Boolean> original) {
        if (!original.call(patchMap, componentPatch, hasher))
            return false;

        DataComponentType<Long> componentType = GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get();
        int remoteStackHashedId = patchMap.addedComponents().getOrDefault(componentType, Integer.MIN_VALUE);
        int localStackHashedId = Optional.ofNullable(componentPatch.get(DataComponentMap.EMPTY, componentType))
                .map(value -> hasher
                        .apply(new TypedDataComponent<>(componentType, value)))
                .orElse(Integer.MIN_VALUE);

        return remoteStackHashedId == localStackHashedId;
    }
}
