package software.bernie.geckolib3.compat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import vazkii.patchouli.client.book.gui.GuiBook;

public class PatchouliCompat {

	public static void patchouliLoaded(MatrixStack matrixStackIn) {
		Class<GuiBook> patchouli = GuiBook.class;
		boolean screen = Minecraft.getInstance().gui.equals(patchouli);
		if (screen) {
			matrixStackIn.pushPose();
			IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers()
					.bufferSource();
			RenderHelper.setupForFlatItems();
			irendertypebuffer$impl.endBatch();
			RenderSystem.enableDepthTest();
			RenderHelper.setupFor3DItems();
			matrixStackIn.popPose();
		}
	}
}