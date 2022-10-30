package software.bernie.geckolib3q.compat;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PatchouliCompat {
	public static void patchouliLoaded(PoseStack poseStack) {
		if (Minecraft.getInstance().gui.equals(GuiBook.class)) {
			poseStack.pushPose();
			Lighting.setupForFlatItems();
			Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
			RenderSystem.enableDepthTest();
			Lighting.setupFor3DItems();
			poseStack.popPose();
		}
	}
}