/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.entity.*;

public class Entities
{
	public static final EntityType<StingrayTestEntity> STINGRAY = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(GeckoLib.ModID, "stingray"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, StingrayTestEntity::new).dimensions(
					EntityDimensions.fixed(2.845F, 0.3125F)).trackable(160, 2).build());

	public static final EntityType<AscendedLegfishEntity> ASCENDED_LEG_FISH = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(GeckoLib.ModID, "ascended_leg_fish"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, AscendedLegfishEntity::new).dimensions(
					EntityDimensions.fixed(2.4F, 4.2F)).trackable(160, 2).build());

	public static final EntityType<LightCrystalEntity> LIGHTCRYSTAL = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(GeckoLib.ModID, "light_crystal"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, LightCrystalEntity::new).dimensions(
							EntityDimensions.fixed(2, 2)).trackable(160, 2).build());

	public static final EntityType<BrownEntity> BROWN =
			Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(GeckoLib.ModID, "brown"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BrownEntity::new).dimensions(
				EntityDimensions.fixed(2, 2)).trackable(160, 2).build());

	public static final EntityType<EasingDemoEntity> EASING_DEMO = Registry.register(
					Registry.ENTITY_TYPE,
					new Identifier(GeckoLib.ModID, "easing_demo"),
					FabricEntityTypeBuilder.create(SpawnGroup.MISC, EasingDemoEntity::new).dimensions(
							EntityDimensions.fixed(0.1f, 0.1f)).trackable(160, 2).build());

	public static final EntityType<RobotEntity> ROBOT = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(GeckoLib.ModID, "robot"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RobotEntity::new).dimensions(
					EntityDimensions.fixed(0.7f, 1.3f)).trackable(160, 2).build());

	public static final EntityType<TigrisEntity> TIGRIS = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(GeckoLib.ModID, "tigris"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TigrisEntity::new).dimensions(
					EntityDimensions.fixed(5F, 3F)).trackable(160, 2).build());

	public static void registerDefaultAttributes() {
		/*
		 * Register our Cube Entity's default attributes.
		 * Attributes are properties or stats of the mobs, including things like attack damage and health.
		 * The game will crash if the entity doesn't have the proper attributes registered in time.
		 *
		 * In 1.15, this was done by a method override inside the entity class.
		 * Most vanilla entities have a static method (eg. ZombieEntity#createZombieAttributes) for initializing their attributes.
		 */
        FabricDefaultAttributeRegistry.register(STINGRAY, StingrayTestEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(ASCENDED_LEG_FISH, AscendedLegfishEntity.createAscendedLegfishAttributes());
		FabricDefaultAttributeRegistry.register(LIGHTCRYSTAL, LightCrystalEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(BROWN, BrownEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(EASING_DEMO, EasingDemoEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(ROBOT, RobotEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(TIGRIS, TigrisEntity.createMobAttributes());
	}

}
