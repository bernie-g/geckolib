package anightdazingzoroark.riftlib.hitboxLogic;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityHitboxRenderer extends Render<EntityHitbox> {
    protected EntityHitboxRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHitbox entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityHitbox> {
        @Override
        public Render<? super EntityHitbox> createRenderFor(RenderManager manager) {
            return new EntityHitboxRenderer(manager);
        }
    }
}
