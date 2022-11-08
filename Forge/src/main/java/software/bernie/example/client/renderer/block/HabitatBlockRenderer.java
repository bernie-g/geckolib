package software.bernie.example.client.renderer.block;

import software.bernie.example.block.entity.HabitatBlockEntity;
import software.bernie.example.client.model.block.HabitatModel;
import software.bernie.geckolib3.renderer.GeoBlockRenderer;

/**
 * Example {@link net.minecraft.world.level.block.entity.BlockEntity} renderer for {@link HabitatBlockEntity}
 * @see HabitatModel
 * @see HabitatBlockEntity
 */
public class HabitatBlockRenderer extends GeoBlockRenderer<HabitatBlockEntity> {
	public HabitatBlockRenderer() {
		super(new HabitatModel());
	}
}
