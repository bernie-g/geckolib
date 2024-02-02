package software.bernie.geckolib.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.*;

public class NeoForgeGeckoLibEvents implements GeckoLibEvents {
    @Override
    public void blockCompileLayers(GeoBlockRenderer<?> renderer){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preBlockRender(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    @Override
    public void postBlockRender(GeoBlockRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Block.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void armorCompileLayers(GeoArmorRenderer<?> renderer){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preArmorRender(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    @Override
    public void postArmorRender(GeoArmorRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Armor.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void entityCompileLayers(GeoEntityRenderer<?> renderer){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preEntityRender(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    @Override
    public void postEntityRender(GeoEntityRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Entity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void replacedEntityCompileLayers(GeoReplacedEntityRenderer<?, ?> renderer){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preReplacedEntityRender(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    @Override
    public void postReplacedEntityRender(GeoReplacedEntityRenderer<?, ?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.ReplacedEntity.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void itemCompileLayers(GeoItemRenderer<?> renderer){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preItemRender(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    @Override
    public void postItemRender(GeoItemRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Item.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }

    @Override
    public void objectCompileLayers(GeoObjectRenderer<?> renderer){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.CompileRenderLayers(renderer));
    }

    @Override
    public boolean preObjectRender(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        return !NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.Pre(renderer, poseStack, model, bufferSource, partialTick, packedLight)).isCanceled();
    }

    @Override
    public void postObjectRender(GeoObjectRenderer<?> renderer, PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight){
        NeoForge.EVENT_BUS.post(new GeoRenderEvent.Object.Post(renderer, poseStack, model, bufferSource, partialTick, packedLight));
    }
}
