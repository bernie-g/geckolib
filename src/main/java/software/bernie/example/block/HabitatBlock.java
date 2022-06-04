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
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
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

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return (BlockState) state.with(FACING, rotation.rotate((Direction) state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation((Direction) state.get(FACING)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return TileRegistry.HABITAT_TILE.instantiate(pos, state);
	}

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

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction direction = (Direction) state.get(FACING);
		switch (direction) {
		case NORTH: {
			return world.getBlockState(pos.east()).isAir();
		}
		case SOUTH: {
			return world.getBlockState(pos.west()).isAir();
		}
		case WEST: {
			return world.getBlockState(pos.north()).isAir();
		}
		default:
			return world.getBlockState(pos.south()).isAir();
		}
	}
}