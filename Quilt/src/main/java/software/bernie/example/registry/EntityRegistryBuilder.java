//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package software.bernie.example.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.EntityFactory;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityRegistryBuilder<E extends Entity> {
	private static Identifier name;
	private EntityFactory<E> entityFactory;
	private SpawnGroup category;
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

	public static <E extends Entity> EntityRegistryBuilder<E> createBuilder(Identifier nameIn) {
		name = nameIn;
		return new EntityRegistryBuilder<>();
	}

	public EntityRegistryBuilder<E> entity(EntityFactory<E> entityFactory) {
		this.entityFactory = entityFactory;
		return this;
	}

	public EntityRegistryBuilder<E> category(SpawnGroup category) {
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
		EntityType.Builder<E> entityBuilder = EntityType.Builder.create(this.entityFactory, this.category)
				.setDimensions(this.dimensions.width, this.dimensions.height);
		if (fireImmune) {
			entityBuilder.makeFireImmune();
		}
		if (this.alwaysUpdateVelocity && this.updateIntervalTicks != 0 & this.trackingDistance != 0) {
			FabricEntityTypeBuilder.create(this.category, this.entityFactory).dimensions(this.dimensions)
					.trackRangeBlocks(this.trackingDistance).trackedUpdateRate(this.updateIntervalTicks)
					.forceTrackedVelocityUpdates(this.alwaysUpdateVelocity).build();
		}

		EntityType<E> entityType = Registry.register(Registry.ENTITY_TYPE, name, entityBuilder.build(name.getPath()));

		if (this.hasEgg) {
			RegistryUtils.registerItem(
					new SpawnEggItem((EntityType<? extends MobEntity>) entityType, this.primaryColor,
							this.secondaryColor, (new Settings()).group(ItemGroup.MISC)),
					new Identifier(name.getNamespace(), String.format("%s_spawn_egg", name.getPath())));
		}

		return entityType;
	}
}
