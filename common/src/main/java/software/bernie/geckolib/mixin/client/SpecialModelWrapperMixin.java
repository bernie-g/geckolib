package software.bernie.geckolib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.renderer.base.GeckolibItemSpecialRenderer;

@Mixin(SpecialModelWrapper.class)
public class SpecialModelWrapperMixin {
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;extractArgument(Lnet/minecraft/world/item/ItemStack;)Ljava/lang/Object;"))
    public <T> T geckolib$extractAllArguments(SpecialModelRenderer<T> instance, ItemStack itemStack, Operation<T> original,
                                              ItemStackRenderState renderState, ItemStack itemStack2, ItemModelResolver modelResolver,
                                              ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return instance instanceof GeckolibItemSpecialRenderer geckolibRenderer ?
               (T)geckolibRenderer.extractArgument(itemStack, renderState, displayContext, level, entity) :
               original.call(instance, itemStack);
    }
}
