package software.bernie.geckolib.service;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Supplier;

/**
 * Loader-agnostic service interface for clientside functionalities
 */
public interface GeckoLibClient {
    Supplier<HumanoidModel<HumanoidRenderState>> GENERIC_INNER_ARMOR_MODEL = Suppliers.memoize(() -> new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)));
    Supplier<HumanoidModel<HumanoidRenderState>> GENERIC_OUTER_ARMOR_MODEL = Suppliers.memoize(() -> new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)));

    /**
     * Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
     * <p>
     * If no custom model applies to this item, the {@code defaultModel} is returned
     */
    @NotNull
    <E extends LivingEntity & GeoAnimatable, S extends HumanoidRenderState> Model getArmorModelForItem(E animatable, S entityRenderState, ItemStack stack, EquipmentSlot slot, EquipmentModel.LayerType type, HumanoidModel<S> defaultModel);

    /**
     * Helper method for retrieving an (ideally) cached instance of the GeoModel for the given item
     *
     * @return The GeoModel for the item, or null if not applicable
     */
    @Nullable
    GeoModel<?> getGeoModelForItem(ItemStack item);

    /**
     * Helper method for retrieving an (ideally) cached instance of the GeoModel for the given item's armor renderer
     *
     * @return The GeoModel for the item, or null if not applicable
     */
    @Nullable
    GeoModel<?> getGeoModelForArmor(ItemStack armour, EquipmentSlot slot, EquipmentModel.LayerType type);
}
