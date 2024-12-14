package software.bernie.geckolib.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public class GeckolibSpecialRenderer implements SpecialModelRenderer<GeckolibSpecialRenderer.RenderData> {
    @Override
    public void render(@Nullable GeckolibSpecialRenderer.RenderData renderData, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasGlint) {
        GeoRenderProvider.of(renderData.item).getGeoItemRenderer().render(renderData, itemDisplayContext, poseStack, bufferSource, packedLight, packedOverlay, hasGlint);
    }

    @Nullable
    @Override
    public GeckolibSpecialRenderer.RenderData extractArgument(ItemStack itemStack) {
        return new RenderData(itemStack.getItem(), itemStack);
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<GeckolibSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Nullable
        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
            return new GeckolibSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }

    public record RenderData(Item item, ItemStack itemstack) {}
}