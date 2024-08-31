package software.bernie.geckolib.loading.math;

import com.google.common.collect.Streams;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.util.ClientUtil;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToDoubleFunction;

/**
 * Helper class for the builtin <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/molangreference/examples/molangconcepts/molangintroduction?view=minecraft-bedrock-stable">Molang</a> query string constants for the {@link MathParser}.
 * <p>
 * These do not constitute a definitive list of queries; merely the default ones
 * <p>
 * Note that the implementations of the various queries in GeckoLib may not necessarily match its implementation in Bedrock
 */
public final class MolangQueries {
	public static final String ACTOR_COUNT = "query.actor_count";
	public static final String ANIM_TIME = "query.anim_time";
	public static final String BLOCKING = "query.blocking";
	public static final String BLOCK_STATE = "query.block_state";
	public static final String BODY_X_ROTATION = "query.body_x_rotation";
	public static final String BODY_Y_ROTATION = "query.body_y_rotation";
	public static final String CAN_CLIMB = "query.can_climb";
	public static final String CAN_FLY = "query.can_fly";
	public static final String CAN_SWIM = "query.can_swim";
	public static final String CAN_WALK = "query.can_walk";
	public static final String CARDINAL_FACING = "query.cardinal_facing";
	public static final String CARDINAL_FACING_2D = "query.cardinal_facing_2d";
	public static final String CARDINAL_PLAYER_FACING = "query.cardinal_player_facing";
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
	public static final String IS_INVISIBLE = "query.is_invisible";
	public static final String IS_IN_CONTACT_WITH_WATER = "query.is_in_contact_with_water";
	public static final String IS_IN_LAVA = "query.is_in_lava";
	public static final String IS_IN_WATER = "query.is_in_water";
	public static final String IS_IN_WATER_OR_RAIN = "query.is_in_water_or_rain";
	public static final String IS_LEASHED = "query.is_leashed";
	public static final String IS_MOVING = "query.is_moving";
	public static final String IS_ON_FIRE = "query.is_on_fire";
	public static final String IS_ON_GROUND = "query.is_on_ground";
	public static final String IS_POWERED = "query.is_powered";
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

	private static final Map<String, Variable> VARIABLES = new ConcurrentHashMap<>();
	private static Actor<?> ACTOR = null;

	static {
		setDefaultQueryValues();
	}

	/**
	 * Returns whether a variable under the given identifier has already been registered, without creating a new instance
	 */
	public static boolean isExistingVariable(String name) {
		return VARIABLES.containsKey(name);
	}

	/**
	 * Register a new {@link Variable} with the math parsing system
	 * <p>
	 * Technically supports overriding by matching keys, though you should try to update the existing variable instances instead if possible
	 *
	 * @see MathParser#registerVariable(Variable)
	 */
	static void registerVariable(Variable variable) {
		VARIABLES.put(variable.name(), variable);
	}

	/**
	 * @return The registered {@link Variable} instance for the given name
	 *
	 * @see MathParser#getVariableFor(String)
	 */
	static Variable getVariableFor(String name) {
		return VARIABLES.computeIfAbsent(applyPrefixAliases(name, "query.", "q."), key -> new Variable(key, 0));
	}

	/**
	 * Parse a given string formatted with a prefix, swapping out any potential aliases for the defined proper name
	 *
	 * @param text The base text to parse
	 * @param properName The "correct" prefix to apply
	 * @param aliases The available prefixes to check and replace
	 * @return The unaliased string, or the original string if no aliases match
	 */
	private static String applyPrefixAliases(String text, String properName, String... aliases) {
		for (String alias : aliases) {
			if (text.startsWith(alias))
				return properName + text.substring(alias.length());
		}

		return text;
	}

	/**
	 * Update the currently rendering animatable. Should be called via {@link software.bernie.geckolib.model.GeoModel#applyMolangQueries(AnimationState, double) GeoModel.applyMolangQueries} when rendering
	 * @param animationState The AnimationState for the current render pass
	 * @param animTime The internal tick counter kept by the {@link AnimatableManager manager} for this animatable
	 */
	public static void updateActor(AnimationState<? extends GeoAnimatable> animationState, double animTime) {
		ACTOR = new Actor<>(animationState, animationState.getAnimatable(), animTime, Minecraft.getInstance(), Minecraft.getInstance().level);
	}

	/**
	 * Cleanup method called automatically by {@link software.bernie.geckolib.renderer.GeoRenderer#defaultRender} to eliminate a memory leak
	 */
	public static void clearActor() {
		ACTOR = null;
	}

	/**
	 * Container record holding animation frame information for the currently rendering animatable.
	 * <p>
	 * This is used by Molang queries to retrieve information for evaluation.
	 */
	public record Actor<T>(AnimationState<? extends GeoAnimatable> animationState, T animatable, double animTime, Minecraft mc, Level level) {}

	/**
	 * Set a variable value utilising the {@link #ACTOR} field, with convenient generic handling for ease of use
	 *
	 * @param name The variable name
	 * @param value The value supplier
	 * @param <T> The lowest-common type of object your actor needs to be in order to evaluate this variable
	 */
	public static <T> void setActorVariable(String name, ToDoubleFunction<Actor<T>> value) {
		getVariableFor(name).set(() -> value.applyAsDouble((Actor)getActor()));
	}

	private static Actor<?> getActor() {
		return ACTOR;
	}

	private static void setDefaultQueryValues() {
		getVariableFor("PI").set(Math.PI);
		getVariableFor("E").set(Math.E);
		setActorVariable(CARDINAL_PLAYER_FACING, actor -> actor.mc.player.getDirection().ordinal());
		setActorVariable(DAY, actor -> actor.level.getGameTime() / 24000d);
		setActorVariable(FRAME_ALPHA, actor -> actor.animationState().getPartialTick());
		setActorVariable(HAS_CAPE, actor -> actor.mc.player.getSkin().capeTexture() != null ? 1 : 0);
		setActorVariable(IS_FIRST_PERSON, actor -> actor.mc.options.getCameraType() == CameraType.FIRST_PERSON ? 1 : 0);
		setActorVariable(LIFE_TIME, actor -> actor.animTime / 20d);
		setActorVariable(MOON_BRIGHTNESS, actor -> actor.level.getMoonBrightness());
		setActorVariable(MOON_PHASE, actor -> actor.level.getMoonPhase());
		setActorVariable(PLAYER_LEVEL, actor -> actor.mc.player.experienceLevel);
		setActorVariable(TIME_OF_DAY, actor -> actor.level.getDayTime() / 24000f);
		setActorVariable(TIME_STAMP, actor -> actor.mc.level.getGameTime());

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
		MolangQueries.<Entity>setActorVariable(BODY_X_ROTATION, actor -> actor.animatable instanceof LivingEntity ? 0 : actor.animatable.getViewXRot(actor.animationState.getPartialTick()));
		MolangQueries.<Entity>setActorVariable(BODY_Y_ROTATION, actor -> actor.animatable instanceof LivingEntity living ? Mth.lerp(actor.animationState.getPartialTick(), living.yBodyRotO, living.yBodyRot) : actor.animatable.getViewYRot(actor.animationState.getPartialTick()));
		MolangQueries.<Entity>setActorVariable(CARDINAL_FACING, actor -> actor.animatable.getDirection().get3DDataValue());
		MolangQueries.<Entity>setActorVariable(CARDINAL_FACING_2D, actor -> {
			int directionId = actor.animatable.getDirection().get3DDataValue();

			return directionId < 2 ? 6 : directionId;
		});
		MolangQueries.<Entity>setActorVariable(DISTANCE_FROM_CAMERA, actor -> actor.mc.gameRenderer.getMainCamera().getPosition().distanceTo(actor.animatable.position()));
		MolangQueries.<Entity>setActorVariable(GET_ACTOR_INFO_ID, actor -> actor.animatable.getId());
		MolangQueries.<Entity>setActorVariable(HAS_COLLISION, actor -> !actor.animatable.noPhysics ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_GRAVITY, actor -> !actor.animatable.isNoGravity() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_OWNER, actor -> actor.animatable instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_PLAYER_RIDER, actor -> actor.animatable.hasPassenger(Player.class::isInstance) ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(HAS_RIDER, actor -> actor.animatable.isVehicle() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ALIVE, actor -> actor.animatable.isAlive() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ANGRY, actor -> actor.animatable instanceof NeutralMob neutralMob && neutralMob.isAngry() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_BREATHING, actor -> actor.animatable.getAirSupply() >= actor.animatable.getMaxAirSupply() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_FIRE_IMMUNE, actor -> actor.animatable.getType().fireImmune() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_INVISIBLE, actor -> actor.animatable.isInvisible() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_CONTACT_WITH_WATER, actor -> actor.animatable.isInWaterRainOrBubble() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_LAVA, actor -> actor.animatable.isInLava() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_WATER, actor -> actor.animatable.isInWater() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_IN_WATER_OR_RAIN, actor -> actor.animatable.isInWaterOrRain() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_LEASHED, actor -> actor.animatable instanceof Leashable leashable && leashable.isLeashed() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_MOVING, actor -> actor.animationState.isMoving() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ON_FIRE, actor -> actor.animatable.isOnFire() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_ON_GROUND, actor -> actor.animatable.onGround() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_POWERED, actor -> actor.animatable instanceof PowerableMob powerable && powerable.isPowered() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_RIDING, actor -> actor.animatable.isPassenger() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SADDLED, actor -> actor.animatable instanceof Saddleable saddleable && saddleable.isSaddled() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SILENT, actor -> actor.animatable.isSilent() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SNEAKING, actor -> actor.animatable.isCrouching() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SPRINTING, actor -> actor.animatable.isSprinting() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(IS_SWIMMING, actor -> actor.animatable.isSwimming() ? 1 : 0);
		MolangQueries.<Entity>setActorVariable(MOVEMENT_DIRECTION, actor -> actor.animationState.isMoving() ? Direction.getNearest(actor.animatable.getDeltaMovement()).get3DDataValue() : 6);
		MolangQueries.<Entity>setActorVariable(RIDER_BODY_X_ROTATION, actor -> actor.animatable.isVehicle() ? actor.animatable.getFirstPassenger() instanceof LivingEntity ? 0 : actor.animatable.getFirstPassenger().getViewXRot(actor.animationState.getPartialTick()) : 0);
		MolangQueries.<Entity>setActorVariable(RIDER_BODY_Y_ROTATION, actor -> actor.animatable.isVehicle() ? actor.animatable.getFirstPassenger() instanceof LivingEntity living ? Mth.lerp(actor.animationState.getPartialTick(), living.yBodyRotO, living.yBodyRot) : actor.animatable.getFirstPassenger().getViewYRot(actor.animationState.getPartialTick()) : 0);
		MolangQueries.<Entity>setActorVariable(RIDER_HEAD_X_ROTATION, actor -> actor.animatable.getFirstPassenger() instanceof LivingEntity living ? living.getViewXRot(actor.animationState.getPartialTick()) : 0);
		MolangQueries.<Entity>setActorVariable(RIDER_HEAD_Y_ROTATION, actor -> actor.animatable.getFirstPassenger() instanceof LivingEntity living ? living.getViewYRot(actor.animationState.getPartialTick()) : 0);
		MolangQueries.<Entity>setActorVariable(VERTICAL_SPEED, actor -> actor.animatable.getDeltaMovement().y);
		MolangQueries.<Entity>setActorVariable(YAW_SPEED, actor -> actor.animatable.getYRot() - actor.animatable.yRotO);
	}

	private static void setDefaultLivingEntityQueryValues() {
		MolangQueries.<LivingEntity>setActorVariable(BLOCKING, actor -> actor.animatable.isBlocking() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(DEATH_TICKS, actor -> actor.animatable.deathTime == 0 ? 0 : actor.animatable.deathTime + actor.animationState.getPartialTick());
		MolangQueries.<LivingEntity>setActorVariable(EQUIPMENT_COUNT, actor -> Streams.stream(actor.animatable.getArmorSlots()).filter(stack -> !stack.isEmpty()).count());
		MolangQueries.<LivingEntity>setActorVariable(GROUND_SPEED, actor -> actor.animatable.getDeltaMovement().horizontalDistance());
		MolangQueries.<LivingEntity>setActorVariable(HAS_HEAD_GEAR, actor -> !actor.animatable.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(HEAD_X_ROTATION, actor -> actor.animatable.getViewXRot(actor.animationState.getPartialTick()));
		MolangQueries.<LivingEntity>setActorVariable(HEAD_Y_ROTATION, actor -> actor.animatable.getViewYRot(actor.animationState.getPartialTick()));
		MolangQueries.<LivingEntity>setActorVariable(HEALTH, actor -> actor.animatable.getHealth());
		MolangQueries.<LivingEntity>setActorVariable(HURT_TIME, actor -> actor.animatable.hurtTime == 0 ? 0 : actor.animatable.hurtTime - actor.animationState.getPartialTick());
		MolangQueries.<LivingEntity>setActorVariable(INVULNERABLE_TICKS, actor -> actor.animatable.invulnerableTime == 0 ? 0 : actor.animatable.invulnerableTime - actor.animationState.getPartialTick());
		MolangQueries.<LivingEntity>setActorVariable(IS_BABY, actor -> actor.animatable.isBaby() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(IS_SLEEPING, actor -> actor.animatable.isSleeping() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(IS_USING_ITEM, actor -> actor.animatable.isUsingItem() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(IS_WALL_CLIMBING, actor -> actor.animatable.onClimbable() ? 1 : 0);
		MolangQueries.<LivingEntity>setActorVariable(MAIN_HAND_ITEM_MAX_DURATION, actor -> actor.animatable.getMainHandItem().getUseDuration(actor.animatable));
		MolangQueries.<LivingEntity>setActorVariable(MAIN_HAND_ITEM_USE_DURATION, actor -> actor.animatable.getUsedItemHand() == InteractionHand.MAIN_HAND ? actor.animatable.getTicksUsingItem() / 20d + actor.animationState.getPartialTick() : 0);
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
		MolangQueries.<Item>setActorVariable(IS_ENCHANTED, actor -> actor.animationState.getData(DataTickets.ITEMSTACK).isEnchanted() ? 1 : 0);
		MolangQueries.<Item>setActorVariable(IS_STACKABLE, actor -> actor.animationState.getData(DataTickets.ITEMSTACK).isStackable() ? 1 : 0);
		MolangQueries.<Item>setActorVariable(ITEM_MAX_USE_DURATION, actor -> actor.animationState.getData(DataTickets.ITEMSTACK).getUseDuration(ClientUtil.getClientPlayer()));
		MolangQueries.<Item>setActorVariable(MAX_DURABILITY, actor -> actor.animationState.getData(DataTickets.ITEMSTACK).getMaxDamage());
		MolangQueries.<Item>setActorVariable(REMAINING_DURABILITY, actor -> {
			ItemStack stack = actor.animationState.getData(DataTickets.ITEMSTACK);

			return stack.isDamageableItem() ? stack.getMaxDamage() - stack.getDamageValue() : 1;
		});
	}
}
