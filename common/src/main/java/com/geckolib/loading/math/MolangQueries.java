package com.geckolib.loading.math;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.constant.DataTickets;
import com.geckolib.loading.math.value.Variable;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.util.ClientUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToDoubleFunction;

/// Helper class for the builtin <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/molangreference/examples/molangconcepts/molangintroduction?view=minecraft-bedrock-stable">Molang</a> query string constants for the [MathParser].
///
/// These do not constitute a definitive list of queries; merely the default ones
///
/// Note that the implementations of the various queries in GeckoLib may not necessarily match its implementation in Bedrock
public final class MolangQueries {
	public static final String ACTOR_COUNT = "query.actor_count";
	public static final String ANIM_TIME = "query.anim_time";
	public static final String BLOCK_STATE = "query.block_state";
	public static final String BLOCKING = "query.blocking";
	public static final String BODY_X_ROTATION = "query.body_x_rotation";
	public static final String BODY_Y_ROTATION = "query.body_y_rotation";
	public static final String CAN_CLIMB = "query.can_climb";
	public static final String CAN_FLY = "query.can_fly";
	public static final String CAN_SWIM = "query.can_swim";
	public static final String CAN_WALK = "query.can_walk";
	public static final String CARDINAL_FACING = "query.cardinal_facing";
	public static final String CARDINAL_FACING_2D = "query.cardinal_facing_2d";
	public static final String CARDINAL_PLAYER_FACING = "query.cardinal_player_facing";
	public static final String CONTROLLER_SPEED = "query.controller_speed";
	public static final String DAY = "query.day";
	public static final String DEATH_TICKS = "query.death_ticks";
	public static final String DISTANCE_FROM_CAMERA = "query.distance_from_camera";
	public static final String EQUIPMENT_COUNT = "query.equipment_count";
	public static final String FRAME_ALPHA = "query.frame_alpha";
	public static final String GET_ACTOR_INFO_ID = "query.get_actor_info_id";
	public static final String GROUND_SPEED = "query.ground_speed";
	public static final String HAS_CAPE = "query.has_cape";
	public static final String HAS_COLLISION = "query.has_collision";
	public static final String HAS_GRAVITY = "query.has_gravity";
	public static final String HAS_HEAD_GEAR = "query.has_head_gear";
	public static final String HAS_OWNER = "query.has_owner";
	public static final String HAS_PLAYER_RIDER = "query.has_player_rider";
	public static final String HAS_RIDER = "query.has_rider";
	public static final String HEAD_X_ROTATION = "query.head_x_rotation";
	public static final String HEAD_Y_ROTATION = "query.head_y_rotation";
	public static final String HEALTH = "query.health";
	public static final String HURT_TIME = "query.hurt_time";
	public static final String INVULNERABLE_TICKS = "query.invulnerable_ticks";
	public static final String IS_ALIVE = "query.is_alive";
	public static final String IS_ANGRY = "query.is_angry";
	public static final String IS_BABY = "query.is_baby";
	public static final String IS_BREATHING = "query.is_breathing";
	public static final String IS_ENCHANTED = "query.is_enchanted";
	public static final String IS_FIRE_IMMUNE = "query.is_fire_immune";
	public static final String IS_FIRST_PERSON = "query.is_first_person";
	public static final String IS_IN_CONTACT_WITH_WATER = "query.is_in_contact_with_water";
	public static final String IS_IN_LAVA = "query.is_in_lava";
	public static final String IS_IN_WATER = "query.is_in_water";
	public static final String IS_IN_WATER_OR_RAIN = "query.is_in_water_or_rain";
	public static final String IS_INVISIBLE = "query.is_invisible";
	public static final String IS_LEASHED = "query.is_leashed";
	public static final String IS_MOVING = "query.is_moving";
	public static final String IS_ON_FIRE = "query.is_on_fire";
	public static final String IS_ON_GROUND = "query.is_on_ground";
	public static final String IS_RIDING = "query.is_riding";
	public static final String IS_SADDLED = "query.is_saddled";
	public static final String IS_SILENT = "query.is_silent";
	public static final String IS_SLEEPING = "query.is_sleeping";
	public static final String IS_SNEAKING = "query.is_sneaking";
	public static final String IS_SPRINTING = "query.is_sprinting";
	public static final String IS_STACKABLE = "query.is_stackable";
	public static final String IS_SWIMMING = "query.is_swimming";
	public static final String IS_USING_ITEM = "query.is_using_item";
	public static final String IS_WALL_CLIMBING = "query.is_wall_climbing";
	public static final String ITEM_MAX_USE_DURATION = "query.item_max_use_duration";
	public static final String LIFE_TIME = "query.life_time";
	public static final String LIMB_SWING = "query.limb_swing";
	public static final String LIMB_SWING_AMOUNT = "query.limb_swing_amount";
	public static final String MAIN_HAND_ITEM_MAX_DURATION = "query.main_hand_item_max_duration";
	public static final String MAIN_HAND_ITEM_USE_DURATION = "query.main_hand_item_use_duration";
	public static final String MAX_DURABILITY = "query.max_durability";
	public static final String MAX_HEALTH = "query.max_health";
	public static final String MOON_BRIGHTNESS = "query.moon_brightness";
	public static final String MOON_PHASE = "query.moon_phase";
	public static final String MOVEMENT_DIRECTION = "query.movement_direction";
	public static final String PLAYER_LEVEL = "query.player_level";
	public static final String REMAINING_DURABILITY = "query.remaining_durability";
	public static final String RIDER_BODY_X_ROTATION = "query.rider_body_x_rotation";
	public static final String RIDER_BODY_Y_ROTATION = "query.rider_body_y_rotation";
	public static final String RIDER_HEAD_X_ROTATION = "query.rider_head_x_rotation";
	public static final String RIDER_HEAD_Y_ROTATION = "query.rider_head_y_rotation";
	public static final String SCALE = "query.scale";
	public static final String SLEEP_ROTATION = "query.sleep_rotation";
	public static final String TIME_OF_DAY = "query.time_of_day";
	public static final String TIME_STAMP = "query.time_stamp";
	public static final String VERTICAL_SPEED = "query.vertical_speed";
	public static final String YAW_SPEED = "query.yaw_speed";

	private static final Map<String, Variable> VARIABLES = new Object2ObjectOpenHashMap<>();
	private static final Map<Variable, ToDoubleFunction<Actor<? extends GeoAnimatable>>> ACTOR_VARIABLES = new Reference2ObjectOpenHashMap<>();

	static {
		setDefaultQueryValues();
	}

	/// Returns whether a variable under the given identifier has already been registered, without creating a new instance
	public static boolean isExistingVariable(String name) {
		return VARIABLES.containsKey(name);
	}

	/// Register a new [Variable] with the math parsing system
	///
	/// Technically supports overriding by matching keys, though you should try to update the existing variable instances instead if possible
	///
	/// @see MathParser#registerVariable(Variable)
	static void registerVariable(Variable variable) {
		VARIABLES.put(variable.name(), variable);
	}

	/// @return The registered [Variable] instance for the given name
	///
	/// @see MathParser#getVariableFor(String)
	static Variable getVariableFor(String name) {
		return VARIABLES.computeIfAbsent(applyPrefixAliases(name, "query.", "q."), key -> new Variable(key, 0));
	}

	/// Parse a given string formatted with a prefix, swapping out any potential aliases for the defined proper name
	///
	/// @param text The base text to parse
	/// @param properName The "correct" prefix to apply
	/// @param aliases The available prefixes to check and replace
	/// @return The unaliased string, or the original string if no aliases match
	private static String applyPrefixAliases(String text, String properName, String... aliases) {
		for (String alias : aliases) {
			if (text.startsWith(alias))
				return properName + text.substring(alias.length());
		}

		return text;
	}

	/// Set a Molang variable that operates on data relevant to the [GeoAnimatable] or associated variables at the time of rendering.
	///
	/// Because of the state-based nature of the render pipeline, this has to be handled slightly differently
	/// to standard [Variable]s
	///
	/// You should only be doing this once, at mod construct
	///
	/// @param <T> The animatable type your variable operates on
	/// @param valueFunction The function that generates the variable value based on the animatable and render state
	@SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> void setActorVariable(String name, ToDoubleFunction<Actor<T>> valueFunction) {
		Variable variable = getVariableFor(name);

		ACTOR_VARIABLES.put(variable, (ToDoubleFunction)valueFunction);
        variable.set(state -> state.getQueryValue(variable));
	}

	/// Set a Molang variable to a given value function based on an [AnimationState]
	///
	/// Note that [actor variables][#setActorVariable(String, ToDoubleFunction)] cannot be overridden here
	///
	/// @param valueFunction The value function to set the variable to
	public static <T extends GeoAnimatable> void setVariableFunction(String name, ToDoubleFunction<ControllerState> valueFunction) {
		Variable variable = getVariableFor(name);

		if (ACTOR_VARIABLES.containsKey(variable))
			throw new IllegalArgumentException("Cannot replace actor variables");

		variable.set(valueFunction);
	}

	/// Set a Molang variable to a given value
	///
	/// Note that [actor variables][#setActorVariable(String, ToDoubleFunction)] cannot be overridden here
	///
	/// @param value The value to set the variable to
	public static void setVariableValue(String name, double value) {
		Variable variable = getVariableFor(name);

		if (ACTOR_VARIABLES.containsKey(variable))
			throw new IllegalArgumentException("Cannot replace actor variables");

		variable.set(value);
	}

	/// Compute and cache the provided variables into the provided value map, to be passed into a following render pass
	///
	/// @param actor The actor instance for this render pass
	/// @param variables The list of variables to compute values for
	/// @param valueMap The map to store the computed values into
	/// @param <T> The lowest-common type of object your actor needs to be in order to evaluate this variable
	public static <T extends GeoAnimatable> void buildActorVariables(Actor<T> actor, Set<Variable> variables, Reference2DoubleMap<Variable> valueMap) {
		for (Variable variable : variables) {
            ToDoubleFunction<Actor<? extends GeoAnimatable>> function = ACTOR_VARIABLES.get(variable);

            if (function != null && !valueMap.containsKey(variable))
                valueMap.put(variable, function.applyAsDouble(actor));
		}
	}

	/// Holder object representing an animatable about to be rendered, along with some associated helper objects.
	/// Used in [actor variables][#setActorVariable(String, ToDoubleFunction)] for pre-computing variable values
	///
	/// @param animatable The animatable instance being prepared for render
	/// @param renderState The [GeoRenderState] being built for the render pass
	/// @param controller The [AnimationController] relevant to the actor at the time this actor is being used
	/// @param renderTime The amount of time (in ticks) this animatable has existed since the first time it rendered
	/// @param partialTick The fraction of a tick that has passed as of the upcoming render frame
	/// @param level The client's level
	/// @param clientPlayer The client player
	/// @param cameraPos The position of the client player's camera
	public record Actor<T>(T animatable, GeoRenderState renderState, AnimationController<?> controller, double renderTime, float partialTick, Level level, Player clientPlayer, Vec3 cameraPos) {}

	private static void setDefaultQueryValues() {
		setVariableValue("PI", Math.PI);
		setVariableValue("E", Math.E);

		setActorVariable(ACTOR_COUNT, actor -> ClientUtil.getVisibleEntityCount());
		setActorVariable(ANIM_TIME, actor -> actor.controller.getCurrentAnimationTime());
		setActorVariable(CONTROLLER_SPEED, actor -> actor.controller.getAnimationSpeed());
		setActorVariable(CARDINAL_PLAYER_FACING, actor -> actor.clientPlayer.getDirection().ordinal());
		setActorVariable(DAY, actor -> actor.level.getGameTime() / 24000d);
		setActorVariable(FRAME_ALPHA, actor -> actor.partialTick);
		setActorVariable(HAS_CAPE, actor -> ClientUtil.clientPlayerHasCape() ? 1 : 0);
		setActorVariable(IS_FIRST_PERSON, actor -> ClientUtil.isFirstPerson() ? 1 : 0);
		setActorVariable(LIFE_TIME, actor -> actor.renderTime / 20d);
		setActorVariable(MOON_BRIGHTNESS, actor -> DimensionType.MOON_BRIGHTNESS_PER_PHASE[ClientUtil.getClientMoonPhase().index()]);
		setActorVariable(MOON_PHASE, actor -> ClientUtil.getClientMoonPhase().index());
		setActorVariable(PLAYER_LEVEL, actor -> actor.clientPlayer.experienceLevel);
		setActorVariable(TIME_OF_DAY, actor -> actor.level.getDefaultClockTime() / 24000d);
		setActorVariable(TIME_STAMP, actor -> actor.level.getGameTime());

		setDefaultBlockEntityQueryValues();
		setDefaultEntityQueryValues();
		setDefaultLivingEntityQueryValues();
		setDefaultMobQueryValues();
		setDefaultItemQueryValues();
	}

	private static void setDefaultBlockEntityQueryValues() {
		MolangQueries.<BlockEntity>setActorVariable(BLOCK_STATE, actor -> actor.animatable.getBlockState().getBlock().getStateDefinition().getPossibleStates().indexOf(actor.animatable.getBlockState()));
	}

	private static void setDefaultEntityQueryValues() {
		MolangQueries.<Entity>setActorVariable(BODY_X_ROTATION, actor -> actor.animatable instanceof LivingEntity ? 0 : actor.animatable.getViewXRot(actor.partialTick));
		MolangQueries.<Entity>setActorVariable(BODY_Y_ROTATION, actor -> actor.animatable instanceof LivingEntity living ? Mth.lerp(actor.partialTick, living.yBodyRotO, living.yBodyRot) : actor.animatable.getViewYRot(actor.partialTick));
		MolangQueries.<Entity>setActorVariable(CARDINAL_FACING, actor -> actor.animatable.getDirection().get3DDataValue());
		MolangQueries.<Entity>setActorVariable(CARDINAL_FACING_2D, actor -> {
			int directionId = actor.animatable.getDirection().get3DDataValue();

			return directionId < 2 ? 6 : directionId;
		});
		MolangQueries.<Entity>setActorVariable(DISTANCE_FROM_CAMERA, actor -> actor.cameraPos.distanceTo(actor.animatable.position()));
		MolangQueries.<Entity>setActorVariable(GET_ACTOR_INFO_ID, actor -> actor.animatable.getId());
		MolangQueries.<Entity>setActorVariable(EQUIPMENT_COUNT, actor -> actor.animatable instanceof EquipmentUser equipmentUser ? Arrays.stream(EquipmentSlot.values()).filter(EquipmentSlot::isArmor).filter(slot -> !equipmentUser.getItemBySlot(slot).isEmpty()).count() : 0);
		MolangQueries.<Entity>setActorVariable(HAS_COLLISION, actor -> !actor.animatable.noPhysics ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_GRAVITY, actor -> !actor.animatable.isNoGravity() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_OWNER, actor -> actor.animatable instanceof OwnableEntity ownable && ownable.getOwnerReference() != null ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_PLAYER_RIDER, actor -> actor.animatable.hasPassenger(Player.class::isInstance) ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_RIDER, actor -> actor.animatable.isVehicle() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ALIVE, actor -> actor.animatable.isAlive() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ANGRY, actor -> actor.animatable instanceof NeutralMob neutralMob && neutralMob.isAngry() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_BREATHING, actor -> actor.animatable.getAirSupply() >= actor.animatable.getMaxAirSupply() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_FIRE_IMMUNE, actor -> actor.animatable.getType().fireImmune() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_INVISIBLE, actor -> actor.animatable.isInvisible() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_CONTACT_WITH_WATER, actor -> actor.animatable.isInWaterOrRain() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_LAVA, actor -> actor.animatable.isInLava() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_WATER, actor -> actor.animatable.isInWater() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_WATER_OR_RAIN, actor -> actor.animatable.isInWaterOrRain() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_LEASHED, actor -> actor.animatable instanceof Leashable leashable && leashable.isLeashed() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_MOVING, actor -> actor.renderState.getOrDefaultGeckolibData(DataTickets.IS_MOVING, false) ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ON_FIRE, actor -> actor.animatable.isOnFire() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ON_GROUND, actor -> actor.animatable.onGround() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_RIDING, actor -> actor.animatable.isPassenger() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SADDLED, actor -> actor.animatable instanceof EquipmentUser equipmentUser && !equipmentUser.getItemBySlot(EquipmentSlot.SADDLE).isEmpty() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SILENT, actor -> actor.animatable.isSilent() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SNEAKING, actor -> actor.animatable.isCrouching() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SPRINTING, actor -> actor.animatable.isSprinting() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SWIMMING, actor -> actor.animatable.isSwimming() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(MOVEMENT_DIRECTION, actor -> actor.renderState.getOrDefaultGeckolibData(DataTickets.IS_MOVING, false) ? Direction.getApproximateNearest(actor.animatable.getDeltaMovement()).get3DDataValue() : 6);
		MolangQueries.<Entity>setActorVariable(RIDER_BODY_X_ROTATION, actor -> actor.animatable.isVehicle() ? actor.animatable.getFirstPassenger() instanceof LivingEntity ? 0 : actor.animatable.getFirstPassenger().getViewXRot(actor.partialTick) : 0);
		MolangQueries.<Entity>setActorVariable(RIDER_BODY_Y_ROTATION, actor -> actor.animatable.isVehicle() ? actor.animatable.getFirstPassenger() instanceof LivingEntity living ? Mth.lerp(actor.partialTick, living.yBodyRotO, living.yBodyRot) : actor.animatable.getFirstPassenger().getViewYRot(actor.partialTick) : 0);
		MolangQueries.<Entity>setActorVariable(RIDER_HEAD_X_ROTATION, actor -> actor.animatable.getFirstPassenger() instanceof LivingEntity living ? living.getViewXRot(actor.partialTick) : 0);
		MolangQueries.<Entity>setActorVariable(RIDER_HEAD_Y_ROTATION, actor -> actor.animatable.getFirstPassenger() instanceof LivingEntity living ? living.getViewYRot(actor.partialTick) : 0);
		MolangQueries.<Entity>setActorVariable(VERTICAL_SPEED, actor -> actor.animatable.getDeltaMovement().y);
		MolangQueries.<Entity>setActorVariable(YAW_SPEED, actor -> actor.animatable.getYRot() - actor.animatable.yRotO);
	}

	private static void setDefaultLivingEntityQueryValues() {
		MolangQueries.<LivingEntity>setActorVariable(BLOCKING, actor -> actor.animatable.isBlocking() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(DEATH_TICKS, actor -> actor.animatable.deathTime == 0 ? 0 : actor.animatable.deathTime + actor.partialTick);
		MolangQueries.<LivingEntity>setActorVariable(GROUND_SPEED, actor -> actor.animatable.getDeltaMovement().horizontalDistance());
		MolangQueries.<LivingEntity>setActorVariable(HAS_HEAD_GEAR, actor -> !actor.animatable.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(HEAD_X_ROTATION, actor -> actor.renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, actor.animatable.getViewXRot(actor.partialTick)));
		MolangQueries.<LivingEntity>setActorVariable(HEAD_Y_ROTATION, actor -> actor.renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, actor.animatable.getViewYRot(actor.partialTick)));
		MolangQueries.<LivingEntity>setActorVariable(HEALTH, actor -> actor.animatable.getHealth());
		MolangQueries.<LivingEntity>setActorVariable(HURT_TIME, actor -> actor.animatable.hurtTime == 0 ? 0 : actor.animatable.hurtTime - actor.partialTick);
		MolangQueries.<LivingEntity>setActorVariable(INVULNERABLE_TICKS, actor -> actor.animatable.invulnerableTime == 0 ? 0 : actor.animatable.invulnerableTime - actor.partialTick);
		MolangQueries.<LivingEntity>setActorVariable(IS_BABY, actor -> actor.animatable.isBaby() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(IS_SLEEPING, actor -> actor.animatable.isSleeping() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(IS_USING_ITEM, actor -> actor.animatable.isUsingItem() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(IS_WALL_CLIMBING, actor -> actor.animatable.onClimbable() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(LIMB_SWING, actor -> actor.animatable.walkAnimation.position());
		MolangQueries.<LivingEntity>setActorVariable(LIMB_SWING_AMOUNT, actor -> actor.animatable.walkAnimation.speed(actor.partialTick()));
		MolangQueries.<LivingEntity>setActorVariable(MAIN_HAND_ITEM_MAX_DURATION, actor -> actor.animatable.getMainHandItem().getUseDuration(actor.animatable));
		MolangQueries.<LivingEntity>setActorVariable(MAIN_HAND_ITEM_USE_DURATION, actor -> actor.animatable.getUsedItemHand() == InteractionHand.MAIN_HAND ? actor.animatable.getTicksUsingItem() / 20d + actor.partialTick : 0);
		MolangQueries.<LivingEntity>setActorVariable(MAX_HEALTH, actor -> actor.animatable.getMaxHealth());
		MolangQueries.<LivingEntity>setActorVariable(SCALE, actor -> actor.animatable.getScale());
		MolangQueries.<LivingEntity>setActorVariable(SLEEP_ROTATION, actor -> Optional.ofNullable(actor.animatable.getBedOrientation()).map(Direction::toYRot).orElse(0f));
	}

	private static void setDefaultMobQueryValues() {
		MolangQueries.<Mob>setActorVariable(CAN_CLIMB, actor -> !actor.animatable.isNoAi() && actor.animatable.getNavigation() instanceof WallClimberNavigation ? 1 : 0);
		MolangQueries.<Mob>setActorVariable(CAN_FLY, actor -> !actor.animatable.isNoAi() && actor.animatable.getNavigation() instanceof FlyingPathNavigation ? 1 : 0);
		MolangQueries.<Mob>setActorVariable(CAN_SWIM, actor -> !actor.animatable.isNoAi() && actor.animatable.getNavigation() instanceof WaterBoundPathNavigation || actor.animatable.getNavigation() instanceof AmphibiousPathNavigation ? 1 : 0);
		MolangQueries.<Mob>setActorVariable(CAN_WALK, actor -> !actor.animatable.isNoAi() && actor.animatable.getNavigation() instanceof GroundPathNavigation || actor.animatable.getNavigation() instanceof AmphibiousPathNavigation ? 1 : 0);
	}

	private static void setDefaultItemQueryValues() {
		MolangQueries.<Item>setActorVariable(IS_ENCHANTED, actor -> actor.renderState.getGeckolibData(DataTickets.IS_ENCHANTED) ? 1 : 0);
		MolangQueries.<Item>setActorVariable(IS_STACKABLE, actor -> actor.renderState.getGeckolibData(DataTickets.IS_STACKABLE) ? 1 : 0);
		MolangQueries.<Item>setActorVariable(ITEM_MAX_USE_DURATION, actor -> actor.renderState.getGeckolibData(DataTickets.MAX_USE_DURATION));
		MolangQueries.<Item>setActorVariable(MAX_DURABILITY, actor -> actor.renderState.getGeckolibData(DataTickets.MAX_DURABILITY));
		MolangQueries.<Item>setActorVariable(REMAINING_DURABILITY, actor -> actor.renderState.getGeckolibData(DataTickets.REMAINING_DURABILITY));
	}
}
