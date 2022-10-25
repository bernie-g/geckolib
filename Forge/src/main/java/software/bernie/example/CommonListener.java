package software.bernie.example;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonListener {
	@SubscribeEvent
	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		if (GeckoLibMod.shouldRegisterExamples()) {
			event.put(EntityRegistry.BIKE_ENTITY.get(), CreatureEntity.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 1.0D).build());
			event.put(EntityRegistry.CAR_ENTITY.get(), CreatureEntity.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 1.0D).build());
			event.put(EntityRegistry.GEO_EXAMPLE_ENTITY.get(), CreatureEntity.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 1.0D).build());

			event.put(EntityRegistry.EXTENDED_RENDERER_EXAMPLE.get(), CreatureEntity.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 1.0D).build());

			event.put(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE.get(), CreatureEntity.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 1.0D).build());

			event.put(EntityRegistry.GEOLAYERENTITY.get(),
					CreatureEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D)
							.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MOVEMENT_SPEED, 0.25f).build());

		}
	}
}
