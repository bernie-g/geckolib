package anightdazingzoroark.example.client.renderer.entity;

import anightdazingzoroark.example.client.model.entity.DragonModel;
import anightdazingzoroark.example.entity.DragonEntity;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {
    public DragonRenderer(RenderManager renderManager) {
        super(renderManager, new DragonModel());
    }
}
