package software.bernie.geckolib.constant;

import com.google.common.reflect.TypeToken;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Stores the default (builtin) [DataTickets][DataTicket] used in GeckoLib
///
/// Also handles registration of [SerializableDataTickets][SerializableDataTicket]
public final class DataTickets {
	private static final Map<String, SerializableDataTicket<?>> SERIALIZABLE_TICKETS = new ConcurrentHashMap<>();

	// Builtin tickets
	// These tickets are used by GeckoLib by default, usually added in by the GeoRenderer for use in animations
	public static final DataTicket<Double> TICK = DataTicket.create("tick", new TypeToken<>() {});
	public static final DataTicket<Class<? extends GeoAnimatable>> ANIMATABLE_CLASS = DataTicket.create("animatable_class", new TypeToken<>() {});
	public static final DataTicket<Float> PARTIAL_TICK = DataTicket.create("partial_tick", new TypeToken<>() {});
	public static final DataTicket<Integer> RENDER_COLOR = DataTicket.create("render_color", new TypeToken<>() {});
	public static final DataTicket<Long> ANIMATABLE_INSTANCE_ID = DataTicket.create("animatable_instance_id", new TypeToken<>() {});
	public static final DataTicket<Boolean> INVISIBLE_TO_PLAYER = DataTicket.create("invisible_to_player", new TypeToken<>() {});
	public static final DataTicket<Integer> PACKED_OVERLAY = DataTicket.create("packed_overlay", new TypeToken<>() {});
	public static final DataTicket<Integer> PACKED_LIGHT = DataTicket.create("packed_light", new TypeToken<>() {});
	public static final DataTicket<Integer> GLOW_COLOUR = DataTicket.create("glow_colour", new TypeToken<>() {});
	public static final DataTicket<Boolean> IS_SHAKING = DataTicket.create("is_shaking", new TypeToken<>() {});
	public static final DataTicket<Pose> ENTITY_POSE = DataTicket.create("entity_pose", new TypeToken<>() {});
	public static final DataTicket<Float> ENTITY_PITCH = DataTicket.create("entity_pitch", new TypeToken<>() {});
	public static final DataTicket<Float> ENTITY_YAW = DataTicket.create("entity_yaw", new TypeToken<>() {});
	public static final DataTicket<Float> ENTITY_BODY_YAW = DataTicket.create("entity_body_yaw", new TypeToken<>() {});
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

	@ApiStatus.Internal
	public static final DataTicket<EnumMap<EquipmentSlot, ? extends HumanoidRenderState>> PER_SLOT_RENDER_DATA = DataTicket.create("per_slot_render_data", new TypeToken<>() {});
	@ApiStatus.Internal
	public static final DataTicket<ControllerState[]> ANIMATION_CONTROLLER_STATES = DataTicket.create("animation_controller_states", new TypeToken<>() {});

	// Builtin serializable tickets
	// These are not used anywhere by default, but are provided as examples and for ease of use
	public static final SerializableDataTicket<Integer> ANIM_STATE = SerializableDataTicket.ofInt(GeckoLibConstants.id("anim_state"));
	public static final SerializableDataTicket<String> ANIM = SerializableDataTicket.ofString(GeckoLibConstants.id("anim"));
	public static final SerializableDataTicket<Integer> USE_TICKS = SerializableDataTicket.ofInt(GeckoLibConstants.id("use_ticks"));
	public static final SerializableDataTicket<Boolean> ACTIVE = SerializableDataTicket.ofBoolean(GeckoLibConstants.id("active"));
	public static final SerializableDataTicket<Boolean> OPEN = SerializableDataTicket.ofBoolean(GeckoLibConstants.id("open"));
	public static final SerializableDataTicket<Boolean> CLOSED = SerializableDataTicket.ofBoolean(GeckoLibConstants.id("closed"));
	public static final SerializableDataTicket<Direction> DIRECTION = SerializableDataTicket.ofEnum(GeckoLibConstants.id("direction"), Direction.class);

	@SuppressWarnings("DataFlowIssue")
    public static @Nullable SerializableDataTicket<?> byName(Identifier id) {
		return SERIALIZABLE_TICKETS.getOrDefault(id.toString(), null);
	}

	/// Internal only. You should NOT be using this
	@ApiStatus.Internal
	public static <D> SerializableDataTicket<D> registerSerializable(SerializableDataTicket<D> ticket) {
		SerializableDataTicket<?> existingTicket = SERIALIZABLE_TICKETS.putIfAbsent(ticket.id(), ticket);

		if (existingTicket != null)
            GeckoLibConstants.LOGGER.error("Duplicate SerializableDataTicket registered! This will cause issues. Existing: {}, New: {}", existingTicket.id(), ticket.id());

		return ticket;
	}
}
