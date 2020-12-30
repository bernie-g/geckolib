package software.bernie.example.block.baked;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeoBakedModel implements IDynamicBakedModel
{
	public ResourceLocation model;
	public ResourceLocation texture;

	public GeoBakedModel(ResourceLocation model, ResourceLocation texture)
	{
		this.model = model;
		this.texture = texture;
	}

	private TextureAtlasSprite getTexture()
	{
		return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(this.texture);
	}

	private void putVertex(BakedQuadBuilder builder, Vector3f normal, Vector4f vertex, float u, float v, TextureAtlasSprite sprite, float r, float g, float b)
	{
		int i = 0;

		for (VertexFormatElement element : builder.getVertexFormat().getElements())
		{
			VertexFormatElement.Usage usage = element.getUsage();

			if (usage == VertexFormatElement.Usage.POSITION)
			{
				builder.put(i, vertex.getX(), vertex.getY(), vertex.getZ(), 1);
			}
			else if (usage == VertexFormatElement.Usage.COLOR)
			{
				builder.put(i, r, g, b, 1);
			}
			else if (usage == VertexFormatElement.Usage.UV)
			{
				int index = element.getIndex();

				if (index == 0)
				{
					float iu = sprite.getInterpolatedU(u * 16);
					float iv = sprite.getInterpolatedV(v * 16);

					builder.put(i, iu, iv);
				}
				else if (index == 2)
				{
					builder.put(i, 0, 0);
				}
				else
				{
					builder.put(i);
				}
			}
			else if (usage == VertexFormatElement.Usage.NORMAL)
			{
				builder.put(i, normal.getX(), normal.getY(), normal.getZ());
			}
			else
			{
				builder.put(i);
			}

			i++;
		}
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
	{
		if (side != null)
		{
			return Collections.emptyList();
		}

		TextureAtlasSprite texture = this.getTexture();
		GeoModel model = GeckoLibCache.getInstance().getGeoModels().get(this.model);
		List<BakedQuad> quads = new ArrayList<BakedQuad>();

		if (model != null)
		{
			MatrixStack stack = new MatrixStack();

			stack.translate(0.5F, 0, 0.5F);

			for (GeoBone bone : model.topLevelBones)
			{
				this.buildRecursively(bone, stack, quads, texture);
			}
		}

		return quads;
	}

	public void buildRecursively(GeoBone bone, MatrixStack stack, List<BakedQuad> quads, TextureAtlasSprite texture)
	{
		BoneSnapshot snapshot = bone.getInitialSnapshot();
		float x = bone.getPositionX();
		float y = bone.getPositionY();
		float z = bone.getPositionZ();
		float rx = bone.getRotationX();
		float ry = bone.getRotationY();
		float rz = bone.getRotationZ();
		float sx = bone.getScaleX();
		float sy = bone.getScaleY();
		float sz = bone.getScaleZ();

		if (snapshot != null)
		{
			bone.setPositionX(snapshot.positionOffsetX);
			bone.setPositionY(snapshot.positionOffsetY);
			bone.setPositionZ(snapshot.positionOffsetZ);
			bone.setRotationX(snapshot.rotationValueX);
			bone.setRotationY(snapshot.rotationValueY);
			bone.setRotationZ(snapshot.rotationValueZ);
			bone.setScaleX(snapshot.scaleValueX);
			bone.setScaleY(snapshot.scaleValueY);
			bone.setScaleZ(snapshot.scaleValueZ);
		}

		stack.push();
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		RenderUtils.scale(bone, stack);
		RenderUtils.moveBackFromPivot(bone, stack);

		if (!bone.isHidden)
		{
			for (GeoCube cube : bone.childCubes)
			{
				stack.push();
				renderCube(cube, stack, quads, texture);
				stack.pop();
			}
			for (GeoBone childBone : bone.childBones)
			{
				buildRecursively(childBone, stack, quads, texture);
			}
		}

		stack.pop();

		if (snapshot != null)
		{
			bone.setPositionX(x);
			bone.setPositionY(y);
			bone.setPositionZ(z);
			bone.setRotationX(rx);
			bone.setRotationY(ry);
			bone.setRotationZ(rz);
			bone.setScaleX(sx);
			bone.setScaleY(sy);
			bone.setScaleZ(sz);
		}
	}

	public void renderCube(GeoCube cube, MatrixStack stack, List<BakedQuad> quads, TextureAtlasSprite texture)
	{
		RenderUtils.moveToPivot(cube, stack);
		RenderUtils.rotate(cube, stack);
		RenderUtils.moveBackFromPivot(cube, stack);
		Matrix3f matrix3f = stack.getLast().getNormal();
		Matrix4f matrix4f = stack.getLast().getMatrix();

		for (GeoQuad quad : cube.quads)
		{
			if(quad == null)
			{
				continue;
			}
			Vector3f normal = quad.normal.copy();
			normal.transform(matrix3f);

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

			BakedQuadBuilder builder = new BakedQuadBuilder(texture);

			builder.setQuadOrientation(Direction.getFacingFromVector(normal.getX(), normal.getY(), normal.getZ()));

			for (GeoVertex vertex : quad.vertices)
			{
				Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);
				vector4f.transform(matrix4f);

				putVertex(builder, normal, vector4f, vertex.textureU, vertex.textureV, texture, 1, 1, 1);
			}

			quads.add(builder.build());
		}
	}

	@Override
	public boolean func_230044_c_()
	{
		return true;
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return false;
	}

	@Override
	public boolean isGui3d()
	{
		return false;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return this.getTexture();
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return ItemOverrideList.EMPTY;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return ItemCameraTransforms.DEFAULT;
	}
}