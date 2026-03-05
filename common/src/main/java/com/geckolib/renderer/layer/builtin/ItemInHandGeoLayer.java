package com.geckolib.renderer.layer.builtin;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Built-in GeoLayer for rendering the item in a [LivingEntity]'s hands.
///
/// Handles all the boilerplate for basic handheld item rendering.
///
/// Assumes the [GeoModel] has bones for both hands called `RightHandItem` and `LeftHandItem`.
/// If you have different names, use the [ItemInHandGeoLayer#ItemInHandGeoLayer(EntityRendererProvider.Context, GeoRenderer, String, String)] constructor.
///
/// @param <T> Entity animatable class type. Inherited from the renderer this layer is attached to
/// @param <O> Associated object class type, or [Void] if none. Inherited from the renderer this layer is attached to
/// @param <R> RenderState class type. Inherited from the renderer this layer is attached to
public class ItemInHandGeoLayer<T extends LivingEntity & GeoAnimatable, O, R extends GeoRenderState> extends BlockAndItemGeoLayer<T, O, R> {
    public static final DataTicket<Boolean> MAINHAND_SHIELD = DataTicket.create("iteminhandgeolayer_mainhand_shield", Boolean.class);
    public static final DataTicket<Boolean> OFFHAND_SHIELD = DataTicket.create("iteminhandgeolayer_offhand_shield", Boolean.class);
    protected final @Nullable String rightHandBone;
    protected final @Nullable String leftHandBone;

    public ItemInHandGeoLayer(EntityRendererProvider.Context context, GeoRenderer<T, O, R> renderer) {
        this(context, renderer, "RightHandItem", "LeftHandItem");
    }

    public ItemInHandGeoLayer(EntityRendererProvider.Context context, GeoRenderer<T, O, R> renderer, @Nullable String rightHandBoneName, @Nullable String leftHandBoneName) {
        super(context, renderer);

        this.rightHandBone = rightHandBoneName;
        this.leftHandBone = leftHandBoneName;
    }

    //<editor-fold defaultstate="collapsed" desc="<Internal Methods>">
    /// Return a list of [RenderData] instances to render
    @Override
    protected List<RenderData> getRelevantBones(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        final List<RenderData> relevantBones = new ObjectArrayList<>(2);
        final RenderData rightHand = this.rightHandBone == null ? null : renderDataForHand(this.rightHandBone, HumanoidArm.RIGHT, animatable, renderState);
        final RenderData leftHand = this.leftHandBone == null ? null : renderDataForHand(this.leftHandBone, HumanoidArm.LEFT, animatable, renderState);

        if (rightHand != null)
            relevantBones.add(rightHand);

        if (leftHand != null)
            relevantBones.add(leftHand);

        return relevantBones;
    }

    /// Helper method for creating [RenderData] for a given hand
    protected RenderData renderDataForHand(String boneName, HumanoidArm arm, T animatable, R renderState) {
        final HumanoidArm mainHandArm = animatable.getMainArm() == HumanoidArm.LEFT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        final EquipmentSlot slot = arm == mainHandArm ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        final ItemDisplayContext context = switch (slot) {
            case MAINHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            case OFFHAND -> mainHandArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
            default -> ItemDisplayContext.NONE;
        };
        final ItemStack stack = animatable.getItemBySlot(slot);

        if (stack.getItem() instanceof ShieldItem)
            renderState.addGeckolibData((slot == EquipmentSlot.MAINHAND ? MAINHAND_SHIELD : OFFHAND_SHIELD), true);

        return RenderData.item(boneName, context, RenderUtil.createRenderStateForItem(stack, this.itemModelResolver, context, animatable));
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
    @Override
    public void addRenderData(T animatable, @Nullable O relatedObject, R renderState, float partialTick) {
        final List<RenderData> contents = getRelevantBones(animatable, relatedObject, renderState, partialTick);

        if (!contents.isEmpty()) {
            renderState.addGeckolibData(BlockAndItemGeoLayer.CONTENTS, contents);
            renderState.addGeckolibData(DataTickets.IS_LEFT_HANDED, animatable.getMainArm() == HumanoidArm.LEFT);
        }
    }

    /// Render the given [ItemStack] for the provided [GeoBone].
    @Override
    protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks, int packedLight) {
        poseStack.pushPose();

        if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));
            poseStack.translate(0, 0.125f, -0.0625f);

            if (renderState.getOrDefaultGeckolibData(renderState.getOrDefaultGeckolibData(DataTickets.IS_LEFT_HANDED, false) ? OFFHAND_SHIELD : MAINHAND_SHIELD, false))
                poseStack.translate(0, 0.125, -0.25);
        }
        else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));
            poseStack.translate(0, 0.125f, -0.0625f);

            if (renderState.getOrDefaultGeckolibData(renderState.getOrDefaultGeckolibData(DataTickets.IS_LEFT_HANDED, false) ? MAINHAND_SHIELD : OFFHAND_SHIELD, false)) {
                poseStack.translate(0, 0.125, 0.25);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
        }

        super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
        poseStack.popPose();
    }
    //</editor-fold>
}
