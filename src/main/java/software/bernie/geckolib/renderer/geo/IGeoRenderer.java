package software.bernie.geckolib.renderer.geo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import software.bernie.geckolib.geo.render.built.*;
import software.bernie.geckolib.model.provider.GeoModelProvider;
import software.bernie.geckolib.util.RenderUtils;

import java.awt.*;

public interface IGeoRenderer<T> {
    default void render(GeoModel model, T animatable, float partialTicks, RenderLayer type, MatrixStack matrixStackIn,  VertexConsumerProvider renderTypeBuffer,  VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        renderEarly(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        if (renderTypeBuffer != null) {
            vertexBuilder = renderTypeBuffer.getBuffer(type);
        }
        renderLate(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        //Render all top level bones
        for (GeoBone group : model.topLevelBones) {
            renderRecursively(group, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    default void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        stack.push();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

        if (!bone.isHidden) {
            for (GeoCube cube : bone.childCubes) {
                renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
            for (GeoBone childBone : bone.childBones) {
                renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }


        stack.pop();
    }

    default void renderCube(GeoCube cube, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        RenderUtils.moveToPivot(cube, stack);
        RenderUtils.rotate(cube, stack);
        RenderUtils.moveBackFromPivot(cube, stack);
        Matrix3f matrix3f = stack.peek().getNormal();
        Matrix4f matrix4f = stack.peek().getModel();

        for (GeoQuad quad : cube.quads) {
            Vector3f normal = quad.normal.copy();
            normal.transform(matrix3f);

            if (normal.getX() < 0) {
                normal.multiplyComponentwise(-1, 1, 1);
            }
            if (normal.getY() < 0) {
                normal.multiplyComponentwise(1, -1, 1);
            }
            if (normal.getZ() < 0) {
                normal.multiplyComponentwise(1, 1, -1);
            }

            for (GeoVertex vertex : quad.vertices) {
                Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);
                vector4f.transform(matrix4f);
                bufferIn.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.textureU, vertex.textureV, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
            }
        }
    }

    GeoModelProvider getGeoModelProvider();

    Identifier getTextureLocation(T instance);

    default void renderEarly(T animatable, MatrixStack stackIn, float ticks,  VertexConsumerProvider renderTypeBuffer,  VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
    }

    default void renderLate(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
    }

    default RenderLayer getRenderType(T animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityCutout(textureLocation);
    }

    default Color getRenderColor(T animatable, float partialTicks, MatrixStack stack,  VertexConsumerProvider renderTypeBuffer,  VertexConsumer vertexBuilder, int packedLightIn) {
        return new Color(255, 255, 255, 255);
    }

    default Integer getUniqueID(T animatable) {
        return animatable.hashCode();
    }
}