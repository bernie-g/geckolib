package software.bernie.geckolib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import software.bernie.geckolib.example.client.renderer.entity.*;
import software.bernie.geckolib.example.entity.StingrayTestEntity;
import software.bernie.geckolib.example.registry.Entities;

@Environment(EnvType.CLIENT)
public class ClientProxy implements ClientModInitializer
{

	@Override
	public void onInitializeClient()
	{
		EntityRendererRegistry.INSTANCE.register(Entities.STINGRAY, (dispatcher, context) -> new StingrayRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(Entities.ASCENDED_LEG_FISH, (dispatcher, context) -> new AscendedLegfishRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(Entities.LIGHTCRYSTAL, (dispatcher, context) -> new LightCrystalRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(Entities.BROWN, (dispatcher, context) -> new BrownRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(Entities.EASING_DEMO, (dispatcher, context) -> new EasingDemoRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(Entities.ROBOT, (dispatcher, context) -> new RobotRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(Entities.TIGRIS, (dispatcher, context) -> new TigrisRenderer(dispatcher));
	}
}
