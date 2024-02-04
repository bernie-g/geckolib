package software.bernie.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import software.bernie.example.registry.*;
import software.bernie.geckolib.GeckoLibConstants;

public final class GeckoLibMod implements ModInitializer {

	@Override
	public void onInitialize() {
		if (!GeckoLibConstants.shouldRegisterExamples()) {
			return;
		}

		new EntityRegistry();
		registerEntityAttributes();

		new ItemRegistry();
		new BlockEntityRegistry();

		new BlockRegistry();
		new SoundRegistry();
	}

	private void registerEntityAttributes() {
		FabricDefaultAttributeRegistry.register(EntityRegistry.BIKE, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.RACE_CAR, createGenericEntityAttributes());

		FabricDefaultAttributeRegistry.register(EntityRegistry.BAT, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.MUTANT_ZOMBIE, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.GREMLIN, createGenericEntityAttributes());

		FabricDefaultAttributeRegistry.register(EntityRegistry.COOL_KID, createGenericEntityAttributes());
		FabricDefaultAttributeRegistry.register(EntityRegistry.FAKE_GLASS, createGenericEntityAttributes());

		FabricDefaultAttributeRegistry.register(EntityRegistry.PARASITE, createGenericEntityAttributes());
	}

	private static AttributeSupplier.Builder createGenericEntityAttributes() {
		return PathfinderMob.createLivingAttributes().add(Attributes.MOVEMENT_SPEED, 0.80000000298023224D)
				.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 5)
				.add(Attributes.ATTACK_KNOCKBACK, 0.1);
	}

}
