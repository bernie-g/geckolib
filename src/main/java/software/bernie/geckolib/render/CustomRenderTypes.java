package software.bernie.geckolib.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class CustomRenderTypes extends RenderType
{
	public CustomRenderTypes(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_)
	{
		super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
	}

	public static RenderType createTranslucentWaterRenderType(ResourceLocation p_230168_0_, boolean p_230168_1_) {

		RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(p_230168_0_, false, false)).depthTest(new RenderState.DepthTestState(515)).texturing(ENTITY_GLINT_TEXTURING).target(OUTLINE_TARGET).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).build(p_230168_1_);
		return RenderType.makeType("entity_translucent_waterfix", DefaultVertexFormats.ENTITY, 6, 256, true, true, rendertype$state);
	}
}
