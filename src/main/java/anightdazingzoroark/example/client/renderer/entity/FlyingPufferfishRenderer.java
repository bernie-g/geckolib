package anightdazingzoroark.example.client.renderer.entity;

import anightdazingzoroark.example.client.model.entity.FlyingPufferfishModel;
import anightdazingzoroark.example.entity.FlyingPufferfishEntity;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

public class FlyingPufferfishRenderer extends GeoEntityRenderer<FlyingPufferfishEntity> {
    public FlyingPufferfishRenderer(RenderManager renderManager) {
        super(renderManager, new FlyingPufferfishModel());
    }
}
