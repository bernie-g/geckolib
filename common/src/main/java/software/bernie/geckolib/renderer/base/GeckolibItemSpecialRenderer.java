package software.bernie.geckolib.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GeckolibItemSpecialRenderer<T extends Item & GeoAnimatable> implements SpecialModelRenderer<GeckolibItemSpecialRenderer.RenderData<T>> {
    @Override
    public void render(@Nullable GeckolibItemSpecialRenderer.RenderData<T> renderData, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasGlint) {
        if (renderData == null)
            return;

        renderData.renderState.addGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, itemDisplayContext);
        renderData.renderState.addGeckolibData(DataTickets.HAS_GLINT, hasGlint);
        renderData.renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, packedOverlay);
        renderData.renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

        renderData.renderer.render(renderData.renderState, poseStack, bufferSource);
    }

    @Nullable
    @Override
    public GeckolibItemSpecialRenderer.RenderData<T> extractArgument(ItemStack itemStack) {
        var item = makeCovariantItem(itemStack.getItem());
        GeoItemRenderer<T> renderer = (GeoItemRenderer)GeoRenderProvider.of(item).getGeoItemRenderer();

        if (renderer == null)
            return null;

        return new RenderData<>(item, buildRenderState(item, itemStack, renderer), renderer);
    }

    private T makeCovariantItem(Item item) {
        return item instanceof GeoAnimatable ? (T)item : null;
    }

    private GeoRenderState buildRenderState(T animatable, ItemStack itemStack, GeoItemRenderer<T> renderer) {
        return renderer.fillRenderState(animatable, itemStack, new GeoRenderState.Impl(), Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
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