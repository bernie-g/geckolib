package software.bernie.example.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import software.bernie.example.registry.TileRegistry;

public class HabitatBlock extends DirectionalBlock {

	public HabitatBlock() {
		super(Properties.of(Material.STONE).noOcclusion());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	/*
	 * Creates the block entity that we have playing our animations and rendering
	 * the block
	 */
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileRegistry.HABITAT_TILE.get().create();
	}

	/*
	 * Hides the normal block and only shows the block entity created below
	 */
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	/*
	 * Adds that our block is faceable
	 */
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	/*
	 * Sets the correct facing, needed to flip this block on the 180, should have
	 * done in the model in BB but eh
	 */
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING,
				context.getHorizontalDirection().getClockWise().getClockWise());
	}

	/*
	 * Sets the correct shape depending on your facing
	 */
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Direction direction = (Direction) state.getValue(FACING);
		switch (direction) {
		case NORTH: {
			return Block.box(0, 0, 0, 32, 16, 16);
		}
		case SOUTH: {
			return Block.box(-16, 0, 0, 16, 16, 16);
		}
		case WEST: {
			return Block.box(0, 0, -16, 16, 16, 16);
		}
		default:
			return Block.box(0, 0, 0, 16, 16, 32);
		}
	}

	/*
	 * Tests for air 1 block out from the facing pos to ensure it's air so the block
	 * doesn't place into another block
	 */
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		for (BlockPos testPos : BlockPos.betweenClosed(pos,
				pos.relative((Direction) state.getValue(FACING).getClockWise(), 2))) {
			if (!testPos.equals(pos) && !world.getBlockState(testPos).isAir())
				return false;
		}
		return true;
	}
}
