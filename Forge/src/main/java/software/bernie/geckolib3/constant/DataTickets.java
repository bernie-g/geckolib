package software.bernie.geckolib3.constant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.object.DataTicket;
import software.bernie.geckolib3.model.data.EntityModelData;

/**
 * Stores the default (builtin) {@link DataTicket DataTickets} used in Geckolib
 */
public final class DataTickets {
	public static final DataTicket<ItemStack> ITEMSTACK = new DataTicket<>("itemstack", ItemStack.class);
	public static final DataTicket<LivingEntity> LIVING_ENTITY = new DataTicket<>("living_entity", LivingEntity.class);
	public static final DataTicket<EquipmentSlot> EQUIPMENT_SLOT = new DataTicket<>("equipment_slot", EquipmentSlot.class);
	public static final DataTicket<EntityModelData> ENTITY_MODEL_DATA = new DataTicket<>("entity_model_data", EntityModelData.class);
}
