package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Set;

public class GeckolibItemSpecialRenderer<T extends Item & GeoAnimatable> implements SpecialModelRenderer<GeckolibItemSpecialRenderer.RenderData<T>> {
    @Override
    public void render(@Nullable GeckolibItemSpecialRenderer.RenderData<T> renderData, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay, boolean hasGlint) {
        if (renderData == null)
            return;

        renderData.renderState.addGeckolibData(DataTickets.HAS_GLINT, hasGlint);
        renderData.renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, packedOverlay);
        renderData.renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

        renderData.renderer.render(renderData.renderState, poseStack, bufferSource);
    }

    @Override
    public void getExtents(Set<Vector3f> set) {}

    /**
     * Wrap the {@link #extractArgument(ItemStack)} call to provide all the context available, rather than just what Mojang provides
     */
    public GeckolibItemSpecialRenderer.RenderData<T> extractArgument(ItemStack itemStack, ItemStackRenderState renderState, ItemDisplayContext context,
                                                                     @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        var item = makeCovariantItem(itemStack.getItem());
        GeoItemRenderer<T> renderer = (GeoItemRenderer)GeoRenderProvider.of(item).getGeoItemRenderer(itemStack);

        if (renderer == null)
            return null;

        return new RenderData<>(item, buildRenderState(item, itemStack, renderer, renderState, context, level, entity), renderer);
    }

    /**
     * Should not be used
     */
    @Deprecated
    @Nullable
    @Override
    public GeckolibItemSpecialRenderer.RenderData<T> extractArgument(ItemStack itemStack) {
        return extractArgument(itemStack, new ItemStackRenderState(), ItemDisplayContext.FIXED, null, null);
    }

    private T makeCovariantItem(Item item) {
        return item instanceof GeoAnimatable ? (T)item : null;
    }

    private GeoRenderState buildRenderState(T animatable, ItemStack itemStack, GeoItemRenderer<T> renderer, ItemStackRenderState renderState, ItemDisplayContext context,
                                            @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return renderer.fillRenderState(animatable, new GeoItemRenderer.RenderData(itemStack, renderState, context, level, entity),
                                        new GeoRenderState.Impl(), Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<GeckolibItemSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Nullable
        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
            return new GeckolibItemSpecialRenderer<>();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }

    public record RenderData<T extends Item & GeoAnimatable>(T item, GeoRenderState renderState, GeoItemRenderer<T> renderer) {}
}