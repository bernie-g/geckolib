package software.bernie.geckolib.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.*;

public class FabricGeckoLibEvents implements GeckoLibEvents {

    @Override
    public void blockCompileLayers(GeoBlockRenderer<?> renderer){
        GeoRenderEvent.Block.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Block.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preBlockRender(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return GeoRenderEvent.Block.Pre.EVENT.invoker().handle(new GeoRenderEvent.Block.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void postBlockRender(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        GeoRenderEvent.Block.Post.EVENT.invoker().handle(new GeoRenderEvent.Block.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void armorCompileLayers(GeoArmorRenderer<?> renderer){
        GeoRenderEvent.Armor.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Armor.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preArmorRender(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return GeoRenderEvent.Armor.Pre.EVENT.invoker().handle(new GeoRenderEvent.Armor.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void postArmorRender(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        GeoRenderEvent.Armor.Post.EVENT.invoker().handle(new GeoRenderEvent.Armor.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void entityCompileLayers(GeoEntityRenderer<?> renderer){
        GeoRenderEvent.Entity.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Entity.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preEntityRender(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return GeoRenderEvent.Entity.Pre.EVENT.invoker().handle(new GeoRenderEvent.Entity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void postEntityRender(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        GeoRenderEvent.Entity.Post.EVENT.invoker().handle(new GeoRenderEvent.Entity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void replacedEntityCompileLayers(GeoReplacedEntityRenderer<?, ?> renderer){
        GeoRenderEvent.ReplacedEntity.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preReplacedEntityRender(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return GeoRenderEvent.ReplacedEntity.Pre.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void postReplacedEntityRender(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        GeoRenderEvent.ReplacedEntity.Post.EVENT.invoker().handle(new GeoRenderEvent.ReplacedEntity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void itemCompileLayers(GeoItemRenderer<?> renderer){
        GeoRenderEvent.Item.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Item.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preItemRender(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return GeoRenderEvent.Item.Pre.EVENT.invoker().handle(new GeoRenderEvent.Item.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void postItemRender(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        GeoRenderEvent.Item.Post.EVENT.invoker().handle(new GeoRenderEvent.Item.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void objectCompileLayers(GeoObjectRenderer<?> renderer){
        GeoRenderEvent.Object.CompileRenderLayers.EVENT.invoker().handle(new GeoRenderEvent.Object.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preObjectRender(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return GeoRenderEvent.Object.Pre.EVENT.invoker().handle(new GeoRenderEvent.Object.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void postObjectRender(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
       GeoRenderEvent.Object.Post.EVENT.invoker().handle(new GeoRenderEvent.Object.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }
}
