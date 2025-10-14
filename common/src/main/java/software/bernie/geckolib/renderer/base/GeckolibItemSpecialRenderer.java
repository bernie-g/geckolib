package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.entity.ItemOwner;
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
    public void submit(@Nullable GeckolibItemSpecialRenderer.RenderData<T> renderData, ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector renderTasks,
                       int packedLight, int packedOverlay, boolean hasGlint, int outlineColor) {
        if (renderData == null)
            return;

        renderData.renderState.addGeckolibData(DataTickets.HAS_GLINT, hasGlint);
        renderData.renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, packedOverlay);
        renderData.renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

        renderData.renderer.submit(renderData.renderState, poseStack, renderTasks, outlineColor);
    }

    @Override
    public void getExtents(Set<Vector3f> set) {
        set.add(new Vector3f(0, 0, 0));
    }

    /**
     * Wrap the {@link #extractArgument(ItemStack)} call to provide all the context available, rather than just what Mojang provides
     */
    public GeckolibItemSpecialRenderer.RenderData<T> extractArgument(ItemStack itemStack, ItemStackRenderState renderState, ItemDisplayContext context,
                                                                     @Nullable ClientLevel level, @Nullable ItemOwner itemOwner) {
        var item = makeCovariantItem(itemStack.getItem());
        GeoItemRenderer<T> renderer = (GeoItemRenderer)GeoRenderProvider.of(item).getGeoItemRenderer();

        if (renderer == null)
            return null;

        return new RenderData<>(item, buildRenderState(item, itemStack, renderer, renderState, context, level, itemOwner), renderer);
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
                                            @Nullable ClientLevel level, @Nullable ItemOwner itemOwner) {
        final GeoItemRenderer.RenderData renderData = new GeoItemRenderer.RenderData(itemStack, renderState, context, level, itemOwner);

        return renderer.fillRenderState(animatable, renderData, renderer.createRenderState(animatable, renderData),
                                        Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<GeckolibItemSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Nullable
        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new GeckolibItemSpecialRenderer<>();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }

    public record RenderData<T extends Item & GeoAnimatable>(T item, GeoRenderState renderState, GeoItemRenderer<T> renderer) {}
}