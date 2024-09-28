package software.bernie.geckolib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.BiConsumer;

/**
 * Internal-only class to store common code mostly for implementation purposes.
 * <p>
 * Shouldn't be used by dependent mods
 */
@ApiStatus.Internal
public class InternalUtil {
    /**
     * Attempt to render a GeckoLib {@link GeoArmorRenderer armor piece} for the given slot
     * <p>
     * This is typically only called by an internal mixin
     *
     * @return true if the armor piece was a GeckoLib armor piece and rendered
     * @param <T> The entity that is rendering its armor
     */
    public static <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> boolean tryRenderGeoArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T entity, ItemStack stack, EquipmentSlot equipmentSlot, M parentModel, A baseModel,
                                                                                                                                  float partialTick, int packedLight, float limbSwing, float limbSwingAmount, float lerpedTickCount, float netHeadYaw, float headPitch,
                                                                                                                                  BiConsumer<A, EquipmentSlot> partVisibilitySetter) {
        final Item item = stack.getItem();

        if (!(item instanceof Equipable equipable) || equipable.getEquipmentSlot() != equipmentSlot)
            return false;

        final HumanoidModel<?> geckolibModel = GeoRenderProvider.of(item).getGeoArmorRenderer(entity, stack, equipmentSlot, baseModel);

        if (geckolibModel == null)
            return false;

        parentModel.copyPropertiesTo(baseModel);
        partVisibilitySetter.accept(baseModel, equipmentSlot);

        if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer)
            geoArmorRenderer.prepForRender(entity, stack, equipmentSlot, baseModel, bufferSource, partialTick, limbSwing, limbSwingAmount, netHeadYaw, headPitch);

        baseModel.copyPropertiesTo((A)geckolibModel);
        geckolibModel.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());

        return true;
    }
}
