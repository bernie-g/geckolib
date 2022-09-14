package software.bernie.geckolib3q.compat;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PatchouliCompat {

	public static void patchouliLoaded(PoseStack matrixStackIn) {
		Class<GuiBook> patchouli = GuiBook.class;
		boolean screen = Minecraft.getInstance().gui.equals(patchouli);
		if (screen) {
			matrixStackIn.pushPose();
			MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers()
					.bufferSource();
			Lighting.setupForFlatItems();
			irendertypebuffer$impl.endBatch();
			RenderSystem.enableDepthTest();
			Lighting.setupFor3DItems();
			matrixStackIn.popPose();
		}
	}
}