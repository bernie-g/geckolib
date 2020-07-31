/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.client.renderer.model.RobotModel;
import software.bernie.geckolib.example.entity.RobotEntity;

import javax.annotation.Nullable;

public class RobotRenderer extends RenderLiving<RobotEntity>
{
	public RobotRenderer(RenderManager rendererManager)
	{
		super(rendererManager, new RobotModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(RobotEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/robot.png");
	}
}