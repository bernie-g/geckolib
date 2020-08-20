package software.bernie.geckolib.example.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import software.bernie.geckolib.example.registry.TileRegistry;

import javax.annotation.Nullable;

public class JackInTheBoxBlock extends Block
{
	public JackInTheBoxBlock()
	{
		super(Properties.create(Material.ROCK).notSolid());
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return TileRegistry.JACK_IN_THE_BOX_TILE.get().create();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
