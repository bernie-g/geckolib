package software.bernie.example.client.renderer.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import software.bernie.example.client.renderer.model.entity.BatModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib.renderer.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {
    public ExampleGeoRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new BatModel());
    }
}
