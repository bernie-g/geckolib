package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

import java.util.EnumMap;
import java.util.List;

/**
 * Built-in GeoLayer for rendering the item in a {@link LivingEntity}'s hands.
 * <p>
 * Handles all the boilerplate for basic handheld item rendering.
 * <p>
 * Assumes the {@link GeoModel} has bones for both hands called <code>MainHandItem</code> and <code>OffHandItem</code>.
 * If you have different names, you must override {@link #getRelevantBones(GeoRenderState, BakedGeoModel)}
 */
public class ItemInHandGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends GeoRenderState> extends BlockAndItemGeoLayer<T, O, R> {
    protected final String mainHandBone;
    protected final String offHandBone;

    public ItemInHandGeoLayer(GeoRenderer<T, O, R> renderer) {
        this(renderer, "MainHandItem", "OffHandItem");
    }

    public ItemInHandGeoLayer(GeoRenderer<T, O, R> renderer, String mainHandBoneName, String offHandBoneName) {
        super(renderer);

        this.mainHandBone = mainHandBoneName;
        this.offHandBone = offHandBoneName;
    }

    /**
     * Return a list of the bone names that this layer will render for.
     * <p>
     * Ideally, you would cache this list in a class-field if you don't need any data from the input renderState or model
     */
    @Override
    protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
        boolean isLeftHanded = renderState.getGeckolibData(DataTickets.IS_LEFT_HANDED).booleanValue();

        return List.of(
                renderDataForHand(this.mainHandBone, EquipmentSlot.MAINHAND, isLeftHanded, renderState),
                renderDataForHand(this.offHandBone, EquipmentSlot.OFFHAND, isLeftHanded, renderState));
    }

    /**
     * Helper method for creating {@link RenderData} for a given hand
     */
    protected static <R extends GeoRenderState> RenderData<R> renderDataForHand(String boneName, EquipmentSlot slot, R renderState) {
        return renderDataForHand(boneName, slot, false, renderState);
    }

    /**
     * Helper method for creating {@link RenderData} for a given hand
     */
    protected static <R extends GeoRenderState> RenderData<R> renderDataForHand(String boneName, EquipmentSlot slot, boolean isLeftHanded, R renderState) {
        HumanoidArm mainHandArm = isLeftHanded ? HumanoidArm.LEFT : HumanoidArm.RIGHT;

        ItemDisplayContext context = switch (slot) {
            case MAINHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            case OFFHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            default -> ItemDisplayContext.NONE;
        };

        return new RenderData<>(boneName, context, (bone, renderState2) -> Either.left((ItemStack)renderState2.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).get(slot)));
    }

    /**
     * Override to add any custom {@link DataTicket}s you need to capture for rendering.
     * <p>
     * The animatable is discarded from the rendering context after this, so any data needed
     * for rendering should be captured in the renderState provided
     *
     * @param animatable The animatable instance being rendered
     * @param relatedObject An object related to the render pass or null if not applicable.
     *                         (E.G. ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
     * @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
     */
    @Override
    public void addRenderData(T animatable, O relatedObject, R renderState) {
        EnumMap<EquipmentSlot, ItemStack> equipment = new EnumMap<>(EquipmentSlot.class);

        equipment.put(EquipmentSlot.MAINHAND, animatable.getMainHandItem());
        equipment.put(EquipmentSlot.OFFHAND, animatable.getOffhandItem());

        renderState.addGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, equipment);
        renderState.addGeckolibData(DataTickets.IS_LEFT_HANDED, animatable.getMainArm() == HumanoidArm.LEFT);
    }


    /**
     * Render the given {@link ItemStack} for the provided {@link GeoBone}.
     */
    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext displayContext, R renderState, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));
            poseStack.translate(0, 0.125f, -0.0625f);

            if (stack.getItem() instanceof ShieldItem)
                poseStack.translate(0, 0.125, -0.25);
        }
        else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
            poseStack.translate(0, 0.125f, -0.0625f);

            if (stack.getItem() instanceof ShieldItem) {
                poseStack.translate(0, 0.125, 0.25);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
        }

        super.renderStackForBone(poseStack, bone, stack, displayContext, renderState, bufferSource, packedLight, packedOverlay);
    }
}
