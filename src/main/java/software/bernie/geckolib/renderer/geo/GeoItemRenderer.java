package software.bernie.geckolib.renderer.geo;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.AnimatedGeoModel;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GeoItemRenderer<T extends Item & IAnimatable> implements IGeoRenderer<T>, BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final Map<Class<? extends Item>, GeoItemRenderer> renderers = new ConcurrentHashMap<>();
    // Register a model fetcher for this renderer
    static {
        AnimationController.addModelFetcher((Object object) -> {
            if (object instanceof Item) {
                GeoItemRenderer renderer = renderers.get(object.getClass());
                return renderer == null ? null : renderer.getGeoModelProvider();
            }
            return null;
        });
    }

    protected AnimatedGeoModel<T> modelProvider;
    protected ItemStack currentItemStack;

    public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
        this.modelProvider = modelProvider;
    }

    public void setModel(AnimatedGeoModel<T> model) {
        this.modelProvider = model;
    }

    public static void registerItemRenderer(Class<? extends Item> itemClass, GeoItemRenderer renderer) {
        renderers.put(itemClass, renderer);
    }

    public static GeoItemRenderer getRenderer(Class<? extends Item> item) {
        return renderers.get(item);
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return modelProvider;
    }

    @Override
    public void render(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack);
    }

    public void render(T animatable, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn, ItemStack itemStack) {
        this.currentItemStack = itemStack;
        AnimationEvent<T> itemEvent = new AnimationEvent<>(animatable, 0, 0, MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(itemStack));
        modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
        stack.push();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0.5, 0.5);

        MinecraftClient.getInstance().getTextureManager().bindTexture(modelProvider.getTextureLocation(animatable));
        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
        Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn, modelProvider.getTextureLocation(animatable));
        render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.pop();
    }

    @Override
    public RenderLayer getRenderType(T animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityCutoutNoCull(textureLocation);
    }

    @Override
    public Color getRenderColor(T animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn) {
        return new Color(255, 255, 255, 255);
    }

    @Override
    public Identifier getTextureLocation(T instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    @Override
    public Integer getUniqueID(T animatable) {
        return Objects.hash(currentItemStack.getItem(), currentItemStack.getCount(), currentItemStack.hasTag() ? currentItemStack.getTag().toString() : 1);
    }
}