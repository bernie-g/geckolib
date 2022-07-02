package software.bernie.geckolib3.renderers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GeoRenderType extends RenderType {
	public GeoRenderType(String p_173178_, VertexFormat p_173179_, Mode p_173180_, int p_173181_, boolean p_173182_,
			boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}

	public static RenderType emissive(ResourceLocation texture) {
		return RenderType.create("geckolib_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
				CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setCullState(NO_CULL)
						.setTextureState(new TextureStateShard(texture, false, false))
						.setTransparencyState(LIGHTNING_TRANSPARENCY).setOverlayState(OVERLAY)
						.createCompositeState(true));
	}
}
