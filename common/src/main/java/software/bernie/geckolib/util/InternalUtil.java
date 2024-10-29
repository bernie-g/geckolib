package software.bernie.geckolib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.object.Color;
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
     */
    public static <E extends LivingEntity, S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> boolean tryRenderGeoArmorPiece(
            PoseStack poseStack, MultiBufferSource bufferSource, E entity, ItemStack stack, EquipmentSlot equipmentSlot,
            M parentModel, A baseModel, float partialTick, int packedLight, float netHeadYaw, float headPitch, BiConsumer<A, EquipmentSlot> partVisibilitySetter) {
        final Item item = stack.getItem();
        final Equippable equippable = stack.get(DataComponents.EQUIPPABLE);

        if (equippable == null || equippable.slot() != equipmentSlot)
            return false;

        final HumanoidModel<?> geckolibModel = GeoRenderProvider.of(item).getGeoArmorRenderer(entity, stack, equipmentSlot,
                equipmentSlot == EquipmentSlot.LEGS ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID, baseModel);

        if (geckolibModel == null)
            return false;

        parentModel.copyPropertiesTo(baseModel);
        partVisibilitySetter.accept(baseModel, equipmentSlot);

        if (geckolibModel instanceof GeoArmorRenderer<?> geoArmorRenderer)
            geoArmorRenderer.prepForRender(entity, stack, equipmentSlot, baseModel, bufferSource, partialTick, netHeadYaw, headPitch);

        baseModel.copyPropertiesTo((A)geckolibModel);
        geckolibModel.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());

        return true;
    }
}
