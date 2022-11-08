package software.bernie.example;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod.EventBusSubscriber(modid = GeckoLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonListener {
	@SubscribeEvent
	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		if (GeckoLibMod.shouldRegisterExamples()) {
			AttributeSupplier.Builder genericAttribs = PathfinderMob.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16)
					.add(Attributes.MAX_HEALTH, 1);
			AttributeSupplier.Builder genericMovingAttribs = PathfinderMob.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16)
					.add(Attributes.MAX_HEALTH, 1)
					.add(Attributes.MOVEMENT_SPEED, 0.25f);

			event.put(EntityRegistry.BIKE_ENTITY.get(), genericAttribs.build());
			event.put(EntityRegistry.CAR_ENTITY.get(), genericAttribs.build());
			event.put(EntityRegistry.TEST_ENTITY.get(), genericAttribs.build());
			event.put(EntityRegistry.GEO_EXAMPLE_ENTITY.get(), genericAttribs.build());
			//event.put(EntityRegistry.EXTENDED_RENDERER_EXAMPLE.get(), genericAttribs.build());
			event.put(EntityRegistry.GEOLAYERENTITY.get(), genericMovingAttribs.build());
			event.put(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE.get(), genericMovingAttribs.build());
		}
	}
}
