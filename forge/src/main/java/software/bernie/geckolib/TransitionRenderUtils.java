package software.bernie.geckolib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

public class TransitionRenderUtils {


    /**
     * Gets a {@link GeoModel} instance from a given {@link EntityType}.<br>
     * This only works if you're calling this method for an EntityType known to be using a {@link GeoRenderer GeckoLib Renderer}.<br>
     * Generally speaking you probably shouldn't be calling this method at all.
     * @param entityType The {@code EntityType} to retrieve the GeoModel for
     * @return The GeoModel, or null if one isn't found
     */
	@Nullable
	public static GeoModel<?> getGeoModelForEntityType(EntityType<?> entityType) {
		EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);

		return renderer instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a GeoAnimatable instance that has been registered as the replacement renderer for a given {@link EntityType}
	 * @param entityType The {@code EntityType} to retrieve the replaced {@link GeoAnimatable} for
	 * @return The {@code GeoAnimatable} instance, or null if one isn't found
	 */
	@Nullable
	public static GeoAnimatable getReplacedAnimatable(EntityType<?> entityType) {
		EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);

		return renderer instanceof GeoReplacedEntityRenderer<?, ?> replacedEntityRenderer ? replacedEntityRenderer.getAnimatable() : null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link Entity}.<br>
	 * This only works if you're calling this method for an Entity known to be using a {@link GeoRenderer GeckoLib Renderer}.<br>
	 * Generally speaking you probably shouldn't be calling this method at all.
	 * @param entity The {@code Entity} to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForEntity(Entity entity) {
		EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

		return renderer instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link Item}.<br>
	 * This only works if you're calling this method for an Item known to be using a {@link GeoRenderer GeckoLib Renderer}.<br>
	 * Generally speaking you probably shouldn't be calling this method at all.
	 * @param item The {@code Item} to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForItem(Item item) {
		if(IClientItemExtensions.of(item).getCustomRenderer() instanceof GeoRenderer<?> geoRenderer)
			return geoRenderer.getGeoModel();

		return null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link BlockEntity}.<br>
	 * This only works if you're calling this method for a BlockEntity known to be using a {@link GeoRenderer GeckoLib Renderer}.<br>
	 * Generally speaking you probably shouldn't be calling this method at all.
	 * @param blockEntity The {@code BlockEntity} to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForBlock(BlockEntity blockEntity) {
		BlockEntityRenderer<?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);

		return renderer instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link Item}.<br>
	 * This only works if you're calling this method for an Item known to be using a {@link GeoArmorRenderer GeoArmorRenderer}.<br>
	 * Generally speaking you probably shouldn't be calling this method at all.
	 * @param stack The ItemStack to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForArmor(ItemStack stack) {
		if (IClientItemExtensions.of(stack).getHumanoidArmorModel(null, stack, null, null) instanceof GeoArmorRenderer<?> armorRenderer)
			return armorRenderer.getGeoModel();

		return null;
	}
}
