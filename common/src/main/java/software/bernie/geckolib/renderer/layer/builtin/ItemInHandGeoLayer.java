package software.bernie.geckolib.renderer.layer.builtin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

/// Built-in GeoLayer for rendering the item in a [LivingEntity]'s hands.
///
/// Handles all the boilerplate for basic handheld item rendering.
///
/// Assumes the [GeoModel] has bones for both hands called `RightHandItem` and `LeftHandItem`.
/// If you have different names, use the [ItemInHandGeoLayer#ItemInHandGeoLayer(GeoRenderer, String, String)] constructor.
///
/// @param <T> Entity animatable class type. Inherited from the renderer this layer is attached to
/// @param <O> Associated object class type, or [Void] if none. Inherited from the renderer this layer is attached to
/// @param <R> RenderState class type. Inherited from the renderer this layer is attached to
public class ItemInHandGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends GeoRenderState> extends BlockAndItemGeoLayer<T, O, R> {
    protected final String rightHandBone;
    protected final String leftHandBone;

    public ItemInHandGeoLayer(GeoRenderer<T, O, R> renderer) {
        this(renderer, "RightHandItem", "LeftHandItem");
    }

    public ItemInHandGeoLayer(GeoRenderer<T, O, R> renderer, String rightHandBoneName, String leftHandBoneName) {
        super(renderer);

        this.rightHandBone = rightHandBoneName;
        this.leftHandBone = leftHandBoneName;
    }

    /// Return a list of the bone names that this layer will render for.
    ///
    /// Ideally, you would cache this list in a class-field if you don't need any data from the input renderState or model
    @Override
    protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
        boolean isLeftHanded = renderState.getOrDefaultGeckolibData(DataTickets.IS_LEFT_HANDED, false);

        return List.of(
                renderDataForHand(this.rightHandBone, HumanoidArm.RIGHT, isLeftHanded, renderState),
                renderDataForHand(this.leftHandBone, HumanoidArm.LEFT, isLeftHanded, renderState));
    }

    /// Helper method for creating [RenderData] for a given hand
    protected static <R extends GeoRenderState> RenderData<R> renderDataForHand(String boneName, R renderState) {
        return renderDataForHand(boneName, HumanoidArm.RIGHT, false, renderState);
    }

    /// Helper method for creating [RenderData] for a given hand
    protected static <R extends GeoRenderState> RenderData<R> renderDataForHand(String boneName, HumanoidArm arm, boolean isLeftHanded, R renderState) {
        HumanoidArm mainHandArm = isLeftHanded ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        EquipmentSlot slot = arm == mainHandArm ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

        ItemDisplayContext context = switch (slot) {
            case MAINHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            case OFFHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            default -> ItemDisplayContext.NONE;
        };

        return new RenderData<>(boneName, context, (bone, renderState2) -> Either.left((ItemStack)renderState2.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).get(slot)));
    }

    /// Override to add any custom [DataTicket]s you need to capture for rendering.
    ///
    /// The animatable is discarded from the rendering context after this, so any data needed
    /// for rendering should be captured in the renderState provided
    ///
    /// @param animatable The animatable instance being rendered
    /// @param relatedObject An object related to the render pass or null if not applicable.
    ///                         (E.G., ItemStack for GeoItemRenderer, entity instance for GeoReplacedEntityRenderer).
    /// @param renderState The GeckoLib RenderState to add data to, will be passed through the rest of rendering
    /// @param partialTick The fraction of a tick that has elapsed as of the current render pass
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        EnumMap<EquipmentSlot, ItemStack> equipment = renderState.getOrDefaultGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, (Supplier<EnumMap>)() -> new EnumMap<>(EquipmentSlot.class));

        //noinspection DataFlowIssue
        equipment.put(EquipmentSlot.MAINHAND, animatable.getMainHandItem());
        equipment.put(EquipmentSlot.OFFHAND, animatable.getOffhandItem());

        renderState.addGeckolibData(DataTickets.EQUIPMENT_BY_SLOT, equipment);
        renderState.addGeckolibData(DataTickets.IS_LEFT_HANDED, animatable.getMainArm() == HumanoidArm.LEFT);
    }

    /// Render the given [ItemStack] for the provided [GeoBone].
    @Override
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks,
                                         CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
        poseStack.pushPose();

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

        super.submitItemStackRender(poseStack, bone, stack, displayContext, renderState, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
        poseStack.popPose();
    }
}
