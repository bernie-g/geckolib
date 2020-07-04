package software.bernie.geckolib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import software.bernie.geckolib.example.client.renderer.entity.AscendedLegfishRenderer;
import software.bernie.geckolib.example.client.renderer.entity.StingrayRenderer;
import software.bernie.geckolib.example.client.renderer.entity.TigrisRenderer;
import software.bernie.geckolib.example.entity.StingrayTestEntity;
import software.bernie.geckolib.example.registry.Entities;

@Environment(EnvType.CLIENT)
public class ClientProxy implements ClientModInitializer
{

	@Override
	public void onInitializeClient()
	{
		EntityRendererRegistry.INSTANCE.register(Entities.STINGRAY, (dispatcher, context) -> {
			return new StingrayRenderer(dispatcher);
		});
		EntityRendererRegistry.INSTANCE.register(Entities.TIGRIS, (dispatcher, context) -> {
			return new TigrisRenderer(dispatcher);
		});
		EntityRendererRegistry.INSTANCE.register(Entities.ASCENDED_LEG_FISH, (dispatcher, context) -> {
			return new AscendedLegfishRenderer(dispatcher);
		});
	}
}
