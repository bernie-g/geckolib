package software.bernie.example.client.renderer.block;

import software.bernie.example.block.entity.FertilizerBlockEntity;
import software.bernie.example.client.model.block.FertilizerModel;
import software.bernie.geckolib3.renderer.GeoBlockRenderer;

/**
 * Example {@link net.minecraft.world.level.block.entity.BlockEntity} renderer for {@link FertilizerBlockEntity}
 * @see FertilizerModel
 * @see FertilizerBlockEntity
 */
public class FertilizerBlockRenderer extends GeoBlockRenderer<FertilizerBlockEntity> {
	public FertilizerBlockRenderer() {
		super(new FertilizerModel());
	}
}
