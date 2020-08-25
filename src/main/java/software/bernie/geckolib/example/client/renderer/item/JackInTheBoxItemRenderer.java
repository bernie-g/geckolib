package software.bernie.geckolib.example.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.client.renderer.model.tile.JackInTheBoxModel;
import software.bernie.geckolib.example.item.JackInTheBoxItem;
import software.bernie.geckolib.item.AnimatedItemRenderer;

public class JackInTheBoxItemRenderer extends AnimatedItemRenderer<JackInTheBoxItem, JackInTheBoxModel>
{
	@Override
	public ResourceLocation getBlockTexture(JackInTheBoxItem entity)
	{
		return new ResourceLocation("geckolib" + ":textures/model/entity/jackinthebox.png");
	}

	@Override
	public void renderCustom(JackInTheBoxItem entity, MatrixStack stack)
	{
		stack.rotate(Vector3f.YP.rotationDegrees(180));
	}
}
