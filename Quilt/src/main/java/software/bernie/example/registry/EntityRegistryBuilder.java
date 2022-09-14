//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package software.bernie.example.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.SpawnEggItem;

public class EntityRegistryBuilder<E extends Entity> {
	private static ResourceLocation name;
	private EntityFactory<E> entityFactory;
	private MobCategory category;
	private int trackingDistance;
	private int updateIntervalTicks;
	private boolean alwaysUpdateVelocity;
	private int primaryColor;
	private int secondaryColor;
	private boolean hasEgg;
	private boolean fireImmune;
	private EntityDimensions dimensions;

	public EntityRegistryBuilder() {
	}

	public static <E extends Entity> EntityRegistryBuilder<E> createBuilder(ResourceLocation nameIn) {
		name = nameIn;
		return new EntityRegistryBuilder<>();
	}

	public EntityRegistryBuilder<E> entity(EntityFactory<E> entityFactory) {
		this.entityFactory = entityFactory;
		return this;
	}

	public EntityRegistryBuilder<E> category(MobCategory category) {
		this.category = category;
		return this;
	}

	public EntityRegistryBuilder<E> tracker(int trackingDistance, int updateIntervalTicks,
			boolean alwaysUpdateVelocity) {
		this.trackingDistance = trackingDistance;
		this.updateIntervalTicks = updateIntervalTicks;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		return this;
	}

	public EntityRegistryBuilder<E> egg(int primaryColor, int secondaryColor) {
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		return this;
	}

	public EntityRegistryBuilder<E> hasEgg(boolean hasEgg) {
		this.hasEgg = hasEgg;
		return this;
	}

	public EntityRegistryBuilder<E> makeFireImmune() {
		this.fireImmune = true;
		return this;
	}

	public EntityRegistryBuilder<E> dimensions(EntityDimensions size) {
		this.dimensions = size;
		return this;
	}

	public EntityType<E> build() {
		EntityType.Builder<E> entityBuilder = EntityType.Builder.of(this.entityFactory, this.category)
				.sized(this.dimensions.width, this.dimensions.height);
		if (fireImmune) {
			entityBuilder.fireImmune();
		}
		if (this.alwaysUpdateVelocity && this.updateIntervalTicks != 0 & this.trackingDistance != 0) {
			FabricEntityTypeBuilder.create(this.category, this.entityFactory).dimensions(this.dimensions)
					.trackRangeBlocks(this.trackingDistance).trackedUpdateRate(this.updateIntervalTicks)
					.forceTrackedVelocityUpdates(this.alwaysUpdateVelocity).build();
		}

		EntityType<E> entityType = Registry.register(Registry.ENTITY_TYPE, name, entityBuilder.build(name.getPath()));

		if (this.hasEgg) {
			RegistryUtils.registerItem(
					new SpawnEggItem((EntityType<? extends Mob>) entityType, this.primaryColor, this.secondaryColor,
							(new Properties()).tab(CreativeModeTab.TAB_MISC)),
					new ResourceLocation(name.getNamespace(), String.format("%s_spawn_egg", name.getPath())));
		}

		return entityType;
	}
}
