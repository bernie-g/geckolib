package software.bernie.example.client.renderer.block;

import software.bernie.example.block.entity.BotariumBlockEntity;
import software.bernie.example.client.model.block.FertilizerModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Example {@link net.minecraft.world.level.block.entity.BlockEntity} renderer for {@link BotariumBlockEntity}
 * @see FertilizerModel
 * @see BotariumBlockEntity
 */
public class FertilizerBlockRenderer extends GeoBlockRenderer<BotariumBlockEntity> {
	public FertilizerBlockRenderer() {
		super(new FertilizerModel());
	}
}
