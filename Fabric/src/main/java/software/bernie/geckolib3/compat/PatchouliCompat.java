package software.bernie.geckolib3.compat;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PatchouliCompat {
	public static void patchouliLoaded(MatrixStack poseStack) {
		if (MinecraftClient.getInstance().inGameHud.equals(GuiBook.class)) {
			poseStack.push();
			DiffuseLighting.disableGuiDepthLighting();
			MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().draw();
			RenderSystem.enableDepthTest();
			DiffuseLighting.enableGuiDepthLighting();
			poseStack.pop();
		}
	}
}