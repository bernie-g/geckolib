package software.bernie.geckolib3.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.animatable.GeoArmor;
import software.bernie.geckolib3.animatable.GeoItem;
import software.bernie.geckolib3.renderer.GeoArmorRenderer;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Wrapper class for GeckoLib armor items.<br>
 * Not specifically required to be used, but it does do some required work you will need to replicate if you don't use it.
 * @see GeoArmorRenderer
 * @see GeoArmor
 */
public abstract class GeoArmorItem extends ArmorItem implements GeoArmor {
	private final Supplier<RenderProvider> renderer = GeoItem.makeRenderer(this);

	public GeoArmorItem(ArmorMaterial armorMaterial, EquipmentSlot slot, Properties properties) {
		super(armorMaterial, slot, properties);
	}

	/**
	 * Getter for the cached RenderProvider in your class
	 */
	@Override
	public Supplier<RenderProvider> getRenderProvider() {
		return this.renderer;
	}

	/**
	 * Forge hook to get the armor model for rendering.<br>
	 * We can use this as our entrypoint for our {@link software.bernie.geckolib3.renderer.GeoRenderer GeoRenderer} and just refer back to our RenderProvider
	 */
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
					EquipmentSlot slot, HumanoidModel<?> defaultModel) {
				GeoArmorRenderer<?> renderer = GeoArmorItem.this.renderer.get().getArmorRenderer((GeoArmor)stack.getItem());

				renderer.prepForRender(entity, stack, slot, defaultModel);

				return renderer;
			}
		});
	}

	/**
	 * Forge hook to get the armor texture for custom armor models
	 * @param stack  ItemStack for the equipped armor
	 * @param entity The entity wearing the armor
	 * @param slot   The slot the armor is in
	 * @param type   The subtype, can be null or "overlay"
	 * @return The armor texture path
	 */
	@Nullable
	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return GeoArmorItem.this.renderer.get().getArmorTexture(entity, stack, slot, type);
	}
}