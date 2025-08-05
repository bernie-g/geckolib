package anightdazingzoroark.riftlib.ui.uiElement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class RiftLibButton extends GuiButton {
    public final String buttonId;
    public int scrollTop = Integer.MIN_VALUE;
    public int scrollBottom = Integer.MAX_VALUE;

    public RiftLibButton(String buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(0, x, y, widthIn, heightIn, buttonText);
        this.buttonId = buttonId;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            //replace default hover detection with scroll-aware version
            boolean hoveredNormally = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            boolean withinVisibleBounds = mouseY >= Math.max(this.y, scrollTop) && mouseY < Math.min(this.y + this.height, scrollBottom);
            this.hovered = hoveredNormally && withinVisibleBounds;

            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (this.packedFGColour != 0) {
                j = this.packedFGColour;
            }
            else if (!this.enabled) {
                j = 10526880;
            }
            else if (this.hovered) {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}
