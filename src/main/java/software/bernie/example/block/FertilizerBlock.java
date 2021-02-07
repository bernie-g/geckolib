package software.bernie.example.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.registry.TileRegistry;

import java.util.List;

public class FertilizerBlock extends FacingBlock implements BlockEntityProvider {
	public FertilizerBlock() {
		super(AbstractBlock.Settings.of(Material.STONE).nonOpaque());
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return TileRegistry.FERTILIZER.instantiate();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		tooltip.add(new LiteralText("Turn on rain to see the fertilizer model!"));
		super.appendTooltip(stack, world, tooltip, options);
	}
}
