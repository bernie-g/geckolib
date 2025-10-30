package software.bernie.geckolib.constant;

import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.renderer.internal.PerBoneRenderTasks;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the default (builtin) {@link DataTicket DataTickets} used in GeckoLib
 * <p>
 * Additionally handles registration of {@link SerializableDataTicket SerializableDataTickets}
 */
public final class DataTickets {
	private static final Map<String, SerializableDataTicket<?>> SERIALIZABLE_TICKETS = new ConcurrentHashMap<>();
	
	// Builtin tickets
	// These tickets are used by GeckoLib by default, usually added in by the GeoRenderer for use in animations
	public static final DataTicket<Double> TICK = DataTicket.create("tick", Double.class);
	public static final DataTicket<Double> ANIMATION_TICKS = DataTicket.create("animation_ticks", Double.class);
	public static final DataTicket<Class> ANIMATABLE_CLASS = DataTicket.create("animatable_class", Class.class);
	public static final DataTicket<Float> PARTIAL_TICK = DataTicket.create("partial_tick", Float.class);
	public static final DataTicket<Integer> RENDER_COLOR = DataTicket.create("render_color", Integer.class);
	public static final DataTicket<Long> ANIMATABLE_INSTANCE_ID = DataTicket.create("animatable_instance_id", Long.class);
	public static final DataTicket<Boolean> INVISIBLE_TO_PLAYER = DataTicket.create("invisible_to_player", Boolean.class);
	public static final DataTicket<Integer> PACKED_OVERLAY = DataTicket.create("packed_overlay", Integer.class);
	public static final DataTicket<Integer> PACKED_LIGHT = DataTicket.create("packed_light", Integer.class);
	public static final DataTicket<Integer> GLOW_COLOUR = DataTicket.create("glow_colour", Integer.class);
	public static final DataTicket<Boolean> IS_SHAKING = DataTicket.create("is_shaking", Boolean.class);
	public static final DataTicket<Pose> ENTITY_POSE = DataTicket.create("entity_pose", Pose.class);
	public static final DataTicket<Float> ENTITY_PITCH = DataTicket.create("entity_pitch", Float.class);
	public static final DataTicket<Float> ENTITY_YAW = DataTicket.create("entity_yaw", Float.class);
	public static final DataTicket<Float> ENTITY_BODY_YAW = DataTicket.create("entity_body_yaw", Float.class);
	public static final DataTicket<Vec3> VELOCITY = DataTicket.create("velocity", Vec3.class);
	public static final DataTicket<Boolean> IS_MOVING = DataTicket.create("is_moving", Boolean.class);
	public static final DataTicket<BlockState> BLOCKSTATE = DataTicket.create("blockstate", BlockState.class);
	public static final DataTicket<Vec3> POSITION = DataTicket.create("position", Vec3.class);
	public static final DataTicket<BlockPos> BLOCKPOS = DataTicket.create("blockpos", BlockPos.class);
	public static final DataTicket<Direction> BLOCK_FACING = DataTicket.create("block_facing", Direction.class);
	public static final DataTicket<ItemDisplayContext> ITEM_RENDER_PERSPECTIVE = DataTicket.create("item_render_perspective", ItemDisplayContext.class);
	public static final DataTicket<Boolean> HAS_GLINT = DataTicket.create("has_glint", Boolean.class);
	public static final DataTicket<Item> ITEM = DataTicket.create("item", Item.class);
	public static final DataTicket<AnimatableManager> ANIMATABLE_MANAGER = DataTicket.create("animatable_manager", AnimatableManager.class);
	public static final DataTicket<Double> BONE_RESET_TIME = DataTicket.create("bone_reset_time", Double.class);
	public static final DataTicket<Boolean> SWINGING_ARM = DataTicket.create("swinging_arm", Boolean.class);
	public static final DataTicket<Boolean> SPRINTING = DataTicket.create("sprinting", Boolean.class);
	public static final DataTicket<Boolean> IS_DEAD_OR_DYING = DataTicket.create("is_dead_or_dying", Boolean.class);
	public static final DataTicket<Boolean> IS_ENCHANTED = DataTicket.create("is_enchanted", Boolean.class);
	public static final DataTicket<Boolean> IS_STACKABLE = DataTicket.create("is_stackable", Boolean.class);
	public static final DataTicket<Integer> MAX_USE_DURATION = DataTicket.create("max_use_duration", Integer.class);
	public static final DataTicket<Integer> MAX_DURABILITY = DataTicket.create("max_durability", Integer.class);
	public static final DataTicket<Integer> REMAINING_DURABILITY = DataTicket.create("remaining_durability", Integer.class);
	public static final DataTicket<EquipmentSlot> EQUIPMENT_SLOT = DataTicket.create("equipment_slot", EquipmentSlot.class);
	public static final DataTicket<HumanoidModel> HUMANOID_MODEL = DataTicket.create("humanoid_model", HumanoidModel.class);
	public static final DataTicket<Boolean> IS_GECKOLIB_WEARER = DataTicket.create("is_geckolib_wearer", Boolean.class);
	public static final DataTicket<EnumMap> EQUIPMENT_BY_SLOT = DataTicket.create("equipment_by_slot", EnumMap.class);
	public static final DataTicket<Boolean> IS_LEFT_HANDED = DataTicket.create("is_left_handed", Boolean.class);
	public static final DataTicket<Boolean> IS_CROUCHING = DataTicket.create("is_crouching", Boolean.class);
	public static final DataTicket<Vec3> ELYTRA_ROTATION = DataTicket.create("elytra_rotation", Vec3.class);

	@ApiStatus.Internal
	public static final DataTicket<EnumMap> PER_SLOT_RENDER_DATA = DataTicket.create("per_slot_render_data", EnumMap.class);
	@ApiStatus.Internal
	public static final DataTicket<Reference2DoubleMap> QUERY_VALUES = DataTicket.create("query_values", Reference2DoubleMap.class);
	@ApiStatus.Internal
	public static final DataTicket<PerBoneRenderTasks> PER_BONE_TASKS = DataTicket.create("per_bone_render_tasks", PerBoneRenderTasks.class);
    @ApiStatus.Internal
	public static final DataTicket<Matrix4f> OBJECT_RENDER_POSE = DataTicket.create("object_render_pose", Matrix4f.class);
    @ApiStatus.Internal
	public static final DataTicket<Matrix4f> MODEL_RENDER_POSE = DataTicket.create("object_render_pose", Matrix4f.class);

	// Builtin serializable tickets
	// These are not used anywhere by default, but are provided as examples and for ease of use
	public static final SerializableDataTicket<Integer> ANIM_STATE = SerializableDataTicket.ofInt(GeckoLibConstants.id("anim_state"));
	public static final SerializableDataTicket<String> ANIM = SerializableDataTicket.ofString(GeckoLibConstants.id("anim"));
	public static final SerializableDataTicket<Integer> USE_TICKS = SerializableDataTicket.ofInt(GeckoLibConstants.id("use_ticks"));
	public static final SerializableDataTicket<Boolean> ACTIVE = SerializableDataTicket.ofBoolean(GeckoLibConstants.id("active"));
	public static final SerializableDataTicket<Boolean> OPEN = SerializableDataTicket.ofBoolean(GeckoLibConstants.id("open"));
	public static final SerializableDataTicket<Boolean> CLOSED = SerializableDataTicket.ofBoolean(GeckoLibConstants.id("closed"));
	public static final SerializableDataTicket<Direction> DIRECTION = SerializableDataTicket.ofEnum(GeckoLibConstants.id("direction"), Direction.class);

	@Nullable
	public static SerializableDataTicket<?> byName(String id) {
		return SERIALIZABLE_TICKETS.getOrDefault(id, null);
	}

	/**
	 * Internal only. You should NOT be using this
	 */
	@ApiStatus.Internal
	public static <D> SerializableDataTicket<D> registerSerializable(SerializableDataTicket<D> ticket) {
		SerializableDataTicket<?> existingTicket = SERIALIZABLE_TICKETS.putIfAbsent(ticket.id(), ticket);

		if (existingTicket != null)
			GeckoLibConstants.LOGGER.error("Duplicate SerializableDataTicket registered! This will cause issues. Existing: " + existingTicket.id() + ", New: " + ticket.id());

		return ticket;
	}
}
