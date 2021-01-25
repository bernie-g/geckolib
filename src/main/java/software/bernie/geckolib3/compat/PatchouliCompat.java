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
		boolean screen = Minecraft.getInstance().ingameGUI.equals(patchouli);
		if (screen) {
			matrixStackIn.push();
			IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers()
					.getBufferSource();
			RenderHelper.setupGuiFlatDiffuseLighting();
			irendertypebuffer$impl.finish();
			RenderSystem.enableDepthTest();
			RenderHelper.setupGui3DDiffuseLighting();
			matrixStackIn.pop();
		}
	}
}