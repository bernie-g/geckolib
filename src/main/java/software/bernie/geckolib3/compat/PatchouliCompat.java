package software.bernie.geckolib3.compat;

import net.minecraft.client.util.math.MatrixStack;

public class PatchouliCompat {

	public static void patchouliLoaded(MatrixStack matrixStackIn) {
//	    Patchouli doesnt exist on fabric 1.17 snapshots :p
/*		Class<GuiBook> patchouli = GuiBook.class;
		boolean screen = MinecraftClient.getInstance().inGameHud.equals(patchouli);
		if (screen) {
			matrixStackIn.push();
			VertexConsumerProvider.Immediate irendertypebuffer$impl = MinecraftClient.getInstance().getBufferBuilders()
					.getEntityVertexConsumers();
			DiffuseLighting.disableGuiDepthLighting();
			irendertypebuffer$impl.draw();
			RenderSystem.enableDepthTest();
			DiffuseLighting.enableGuiDepthLighting();
			matrixStackIn.pop();
		}*/
	}
}