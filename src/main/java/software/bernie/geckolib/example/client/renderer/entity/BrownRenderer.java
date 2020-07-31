/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.client.renderer.model.BrownModel;
import software.bernie.geckolib.example.entity.BrownEntity;

import javax.annotation.Nullable;

public class BrownRenderer extends RenderLiving<BrownEntity>
{
	public BrownRenderer(RenderManager rendererManager)
	{
		super(rendererManager, new BrownModel(), 0.5F);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(BrownEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/brown.png");
	}
}