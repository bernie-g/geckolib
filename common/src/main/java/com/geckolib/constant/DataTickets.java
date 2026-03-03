package com.geckolib.constant;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.constant.dataticket.OverridingDataTicket;
import com.geckolib.object.VanillaModelModifier;
import com.google.common.reflect.TypeToken;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/// Stores the default (builtin) [DataTickets][DataTicket] used in GeckoLib
///
/// This class should only be used on the client side
public final class DataTickets {
	public static final DataTicket<Class<? extends GeoAnimatable>> ANIMATABLE_CLASS = DataTicket.create("animatable_class", new TypeToken<>() {});
	public static final DataTicket<Float> PARTIAL_TICK = DataTicket.create("partial_tick", new TypeToken<>() {});
	public static final DataTicket<Integer> RENDER_COLOR = DataTicket.create("render_color", new TypeToken<>() {});
	public static final DataTicket<Long> ANIMATABLE_INSTANCE_ID = DataTicket.create("animatable_instance_id", new TypeToken<>() {});
	public static final DataTicket<Integer> PACKED_OVERLAY = DataTicket.create("packed_overlay", new TypeToken<>() {});
	public static final DataTicket<Integer> PACKED_LIGHT = DataTicket.create("packed_light", new TypeToken<>() {});
	public static final DataTicket<Integer> GLOW_COLOUR = DataTicket.create("glow_colour", new TypeToken<>() {});
	public static final DataTicket<Vec3> VELOCITY = DataTicket.create("velocity", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_MOVING = DataTicket.create("is_moving", new TypeToken<>() {});
	public static final DataTicket<BlockState> BLOCKSTATE = DataTicket.create("blockstate", new TypeToken<>() {});
	public static final DataTicket<Vec3> POSITION = DataTicket.create("position", new TypeToken<>() {});
	public static final DataTicket<BlockPos> BLOCKPOS = DataTicket.create("blockpos", new TypeToken<>() {});
	public static final DataTicket<Direction> BLOCK_FACING = DataTicket.create("block_facing", new TypeToken<>() {});
	public static final DataTicket<ItemDisplayContext> ITEM_RENDER_PERSPECTIVE = DataTicket.create("item_render_perspective", new TypeToken<>() {});
	public static final DataTicket<Boolean> HAS_GLINT = DataTicket.create("has_glint", new TypeToken<>() {});
	public static final DataTicket<Item> ITEM = DataTicket.create("item", new TypeToken<>() {});
	public static final DataTicket<AnimatableManager<? extends GeoAnimatable>> ANIMATABLE_MANAGER = DataTicket.create("animatable_manager", new TypeToken<>() {});
	public static final DataTicket<Boolean> SWINGING_ARM = DataTicket.create("swinging_arm", new TypeToken<>() {});
	public static final DataTicket<Boolean> SPRINTING = DataTicket.create("sprinting", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_DEAD_OR_DYING = DataTicket.create("is_dead_or_dying", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_ENCHANTED = DataTicket.create("is_enchanted", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_STACKABLE = DataTicket.create("is_stackable", new TypeToken<>() {});
	public static final DataTicket<Integer> MAX_USE_DURATION = DataTicket.create("max_use_duration", new TypeToken<>() {});
	public static final DataTicket<Integer> MAX_DURABILITY = DataTicket.create("max_durability", new TypeToken<>() {});
	public static final DataTicket<Integer> REMAINING_DURABILITY = DataTicket.create("remaining_durability", new TypeToken<>() {});
	public static final DataTicket<EquipmentSlot> EQUIPMENT_SLOT = DataTicket.create("equipment_slot", new TypeToken<>() {});
	public static final DataTicket<HumanoidModel<? extends HumanoidRenderState>> HUMANOID_MODEL = DataTicket.create("humanoid_model", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_GECKOLIB_WEARER = DataTicket.create("is_geckolib_wearer", new TypeToken<>() {});
	public static final DataTicket<EnumMap<EquipmentSlot, ItemStack>> EQUIPMENT_BY_SLOT = DataTicket.create("equipment_by_slot", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_LEFT_HANDED = DataTicket.create("is_left_handed", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_CROUCHING = DataTicket.create("is_crouching", new TypeToken<>() {});
	public static final DataTicket<Vec3> ELYTRA_ROTATION = DataTicket.create("elytra_rotation", new TypeToken<>() {});
	public static final DataTicket<Map<? extends Model<?>, List<VanillaModelModifier<?, ?>>>> VANILLA_MODEL_MODIFIERS = DataTicket.create("vanilla_model_modifiers", new TypeToken<>() {});

	public static final OverridingDataTicket<Double, EntityRenderState> TICK = OverridingDataTicket.create("tick", new TypeToken<>() {}, EntityRenderState.class, state -> (double)state.ageInTicks);
	public static final OverridingDataTicket<Boolean, LivingEntityRenderState> INVISIBLE_TO_PLAYER = OverridingDataTicket.create("invisible_to_player", new TypeToken<>() {}, LivingEntityRenderState.class, state -> state.isInvisibleToPlayer);
	public static final OverridingDataTicket<Boolean, LivingEntityRenderState> IS_SHAKING = OverridingDataTicket.create("is_shaking", new TypeToken<>() {}, LivingEntityRenderState.class, state -> state.isFullyFrozen);
	public static final OverridingDataTicket<Pose, LivingEntityRenderState> ENTITY_POSE = OverridingDataTicket.create("entity_pose", new TypeToken<>() {}, LivingEntityRenderState.class, state -> state.pose);
	public static final OverridingDataTicket<Float, LivingEntityRenderState> ENTITY_PITCH = OverridingDataTicket.create("entity_pitch", new TypeToken<>() {}, LivingEntityRenderState.class, state -> state.xRot);
	public static final OverridingDataTicket<Float, LivingEntityRenderState> ENTITY_YAW = OverridingDataTicket.create("entity_yaw", new TypeToken<>() {}, LivingEntityRenderState.class, state -> state.yRot);
	public static final OverridingDataTicket<Float, LivingEntityRenderState> ENTITY_BODY_YAW = OverridingDataTicket.create("entity_body_yaw", new TypeToken<>() {}, LivingEntityRenderState.class, state -> state.bodyRot);

	@ApiStatus.Internal
	public static final DataTicket<EnumMap<EquipmentSlot, ? extends HumanoidRenderState>> PER_SLOT_RENDER_DATA = DataTicket.create("per_slot_render_data", new TypeToken<>() {});
	@ApiStatus.Internal
	public static final DataTicket<ControllerState[]> ANIMATION_CONTROLLER_STATES = DataTicket.create("animation_controller_states", new TypeToken<>() {});
}
