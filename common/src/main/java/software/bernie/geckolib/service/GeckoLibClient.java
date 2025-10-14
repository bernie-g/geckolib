package software.bernie.geckolib.service;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.function.Supplier;

/**
 * Loader-agnostic service interface for clientside functionalities
 */
public interface GeckoLibClient {
    Supplier<ArmorModelSet<PlayerModel>> PLAYER_ARMOR = Suppliers.memoize(() -> ArmorModelSet.bake(ModelLayers.PLAYER_ARMOR, Minecraft.getInstance().getEntityModels(), root -> new PlayerModel(root, false)));
    Supplier<ArmorModelSet<PlayerModel>> PLAYER_SLIM_ARMOR = Suppliers.memoize(() -> ArmorModelSet.bake(ModelLayers.PLAYER_SLIM_ARMOR, Minecraft.getInstance().getEntityModels(), root -> new PlayerModel(root, true)));
    Supplier<ElytraModel> GENERIC_ELYTRA_MODEL = Suppliers.memoize(() -> new ElytraModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELYTRA)));

    /**
     * Helper method for retrieving an (ideally) cached instance of the armor model for a given Item
     * <p>
     * If no custom model applies to this item, the {@code defaultModel} is returned
     */
    @NotNull
    <S extends HumanoidRenderState & GeoRenderState> Model<?> getArmorModelForItem(S entityRenderState, ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType type, HumanoidModel<S> defaultModel);

    /**
     * Helper method for retrieving an (ideally) cached instance of the GeoModel for the given item
     *
     * @return The GeoModel for the item, or null if not applicable
     */
    @Nullable
    GeoModel<?> getGeoModelForItem(ItemStack item);

    /**
     * Return the dye value for a given ItemStack, or the defaul value if not present.
     * <p>
     * This is split off to allow for handling of loader-specific handling for dyed items
     */
    int getDyedItemColor(ItemStack itemStack, int defaultColor);
}
