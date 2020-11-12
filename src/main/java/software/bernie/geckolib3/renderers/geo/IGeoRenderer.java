package software.bernie.geckolib3.renderers.geo;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.util.RenderUtils;

import javax.vecmath.Vector4f;
import java.awt.*;

public interface IGeoRenderer<T>
{
	default void render(GeoModel model, T animatable, float partialTicks, float red, float green, float blue, float alpha)
	{
		renderEarly(animatable, partialTicks, red, green, blue, alpha);

		renderLate(animatable, partialTicks, red, green, blue, alpha);
		//Render all top level bones
		for (GeoBone group : model.topLevelBones)
		{
			renderRecursively(group, red, green, blue, alpha);
		}

		renderAfter(animatable, partialTicks, red, green, blue, alpha);
	}

	default void renderRecursively(GeoBone bone, float red, float green, float blue, float alpha)
	{
		GlStateManager.pushMatrix();
		RenderUtils.translate(bone);
		RenderUtils.moveToPivot(bone);
		RenderUtils.rotate(bone);
		RenderUtils.scale(bone);
		RenderUtils.moveBackFromPivot(bone);

		if (!bone.isHidden)
		{
			for (GeoCube cube : bone.childCubes)
			{
				GlStateManager.pushMatrix();
				renderCube(cube, red, green, blue, alpha);
				GlStateManager.popMatrix();
			}
			for (GeoBone childBone : bone.childBones)
			{
				renderRecursively(childBone, red, green, blue, alpha);
			}
		}


		GlStateManager.popMatrix();
	}

	default void renderCube(GeoCube cube, float red, float green, float blue, float alpha)
	{
		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		RenderUtils.moveToPivot(cube);
		RenderUtils.rotate(cube);
		RenderUtils.moveBackFromPivot(cube);

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

		for (GeoQuad quad : cube.quads)
		{
			Vec3i normal = quad.normal;

			/* ???
			if (normal.getX() < 0)
			{
				normal.mul(-1, 1, 1);
			}
			if (normal.getY() < 0)
			{
				normal.mul(1, -1, 1);
			}
			if (normal.getZ() < 0)
			{
				normal.mul(1, 1, -1);
			}
			*/

			for (GeoVertex vertex : quad.vertices)
			{
				Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);

				builder.pos(vector4f.getX(), vector4f.getY(), vector4f.getZ()).tex(vertex.textureU, vertex.textureV).color(red, green, blue, alpha).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			}
		}

		Tessellator.getInstance().draw();
	}

	GeoModelProvider getGeoModelProvider();

	ResourceLocation getTextureLocation(T instance);

	default void renderEarly(T animatable, float ticks, float red, float green, float blue, float partialTicks)
	{
	}

	default void renderLate(T animatable, float ticks, float red, float green, float blue, float partialTicks)
	{
	}

	default void renderAfter(T animatable, float ticks, float red, float green, float blue, float partialTicks)
	{
	}

	default Color getRenderColor(T animatable, float partialTicks)
	{
		return new Color(255, 255, 255, 255);
	}

	default Integer getUniqueID(T animatable)
	{
		return animatable.hashCode();
	}
}
