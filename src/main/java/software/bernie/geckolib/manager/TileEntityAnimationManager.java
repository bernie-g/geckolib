/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.manager;

/**
 * Each tile entity should have exactly ye<b>ONE</b> TileEntityAnimationManager and can add as many animation controllers to the collection as desired.
 */
public class TileEntityAnimationManager extends EntityAnimationManager
{
	private long tileEntityCreationTime = 0;

	/**
	 * Instantiates a new Animation controller collection.
	 */
	public TileEntityAnimationManager()
	{
		super();
	}

	public long getTileEntityCreationTime()
	{
		return tileEntityCreationTime;
	}

	public void setTileEntityCreationTime(long time)
	{
		this.tileEntityCreationTime = time;
	}
}
