package software.bernie.geckolib3.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

/**
 * Stores the default (builtin) {@link AnimationEvent.DataTicket DataTickets} used in Geckolib
 */
public final class DataTickets {
	public static final AnimationEvent.DataTicket<ItemStack> ITEMSTACK = new AnimationEvent.DataTicket<>("itemstack", ItemStack.class);
	public static final AnimationEvent.DataTicket<LivingEntity> LIVING_ENTITY = new AnimationEvent.DataTicket<>("living_entity", LivingEntity.class);
	public static final AnimationEvent.DataTicket<EquipmentSlot> EQUIPMENT_SLOT = new AnimationEvent.DataTicket<>("equipment_slot", EquipmentSlot.class);
	public static final AnimationEvent.DataTicket<EntityModelData> ENTITY_MODEL_DATA = new AnimationEvent.DataTicket<>("entity_model_data", EntityModelData.class);
}
