package anightdazingzoroark.example.client.renderer.entity;

import anightdazingzoroark.example.client.model.entity.BombProjectileModel;
import anightdazingzoroark.example.entity.BombProjectile;
import anightdazingzoroark.riftlib.renderers.geo.GeoProjectileRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

public class BombProjectileRenderer extends GeoProjectileRenderer<BombProjectile> {
    public BombProjectileRenderer(RenderManager renderManager) {
        super(renderManager, new BombProjectileModel());
    }
}
