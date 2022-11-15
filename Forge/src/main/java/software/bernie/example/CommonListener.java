package software.bernie.example;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
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
			AttributeSupplier.Builder genericMonsterAttribs = Monster.createMobAttributes()
					.add(Attributes.FOLLOW_RANGE, 16)
					.add(Attributes.MAX_HEALTH, 1)
					.add(Attributes.MOVEMENT_SPEED, 0.25f)
					.add(Attributes.ATTACK_DAMAGE, 5);

			event.put(EntityRegistry.BIKE.get(), genericAttribs.build());
			event.put(EntityRegistry.RACE_CAR.get(), genericAttribs.build());
			event.put(EntityRegistry.BAT.get(), genericAttribs.build());
			//event.put(EntityRegistry.EXTENDED_RENDERER_EXAMPLE.get(), genericAttribs.build());
			event.put(EntityRegistry.COOL_KID.get(), genericMovingAttribs.build());
			event.put(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE.get(), genericMovingAttribs.build());
			event.put(EntityRegistry.PARASITE.get(), genericMonsterAttribs.build());
		}
	}
}
