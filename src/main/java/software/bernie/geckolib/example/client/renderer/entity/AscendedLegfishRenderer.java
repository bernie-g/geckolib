/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib.example.client.renderer.model.AscendedLegfishModel;
import software.bernie.geckolib.example.entity.AscendedLegfishEntity;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class AscendedLegfishRenderer extends RenderLiving<AscendedLegfishEntity>
{
	public AscendedLegfishRenderer(RenderManager rendererManager)
	{
		super(rendererManager, new AscendedLegfishModel(), 0.5f);
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(AscendedLegfishEntity entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/ascended_leg_fish.png");
	}

}