package software.bernie.example.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import software.bernie.example.registry.TileRegistry;

public class HabitatBlock extends FacingBlock implements BlockEntityProvider {

	public HabitatBlock() {
		super(AbstractBlock.Settings.of(Material.GLASS).nonOpaque());
	}

	/*
	 * Hides the normal block and only shows the block entity created below
	 */
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	/*
	 * Adds that our block is faceable
	 */
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	/*
	 * Sets the correct facing, needed to flip this block on the 180, should have
	 * done in the model in BB but eh
	 */
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().rotateYClockwise().rotateYClockwise());
	}

	/*
	 * Creates the block entity that we have playing our animations and rendering
	 * the block
	 */
	@Nullable
	@Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return TileRegistry.HABITAT_TILE.instantiate(pos, state);
	}

	/*
	 * Sets the correct shape depending on your facing
	 */
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = (Direction) state.get(FACING);
		switch (direction) {
		case NORTH: {
			return Block.createCuboidShape(0, 0, 0, 32, 16, 16);
		}
		case SOUTH: {
			return Block.createCuboidShape(-16, 0, 0, 16, 16, 16);
		}
		case WEST: {
			return Block.createCuboidShape(0, 0, -16, 16, 16, 16);
		}
		default:
			return Block.createCuboidShape(0, 0, 0, 16, 16, 32);
		}
	}

	/*
	 * Tests for air 1 block out from the facing pos to ensure it's air so the block
	 * doesn't place into another block
	 */
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		for (BlockPos testPos : BlockPos.iterate(pos,
				pos.offset((Direction) state.get(FACING).rotateYClockwise(), 2))) {
			if (!testPos.equals(pos) && !world.getBlockState(testPos).isAir())
				return false;
		}
		return true;
	}
}