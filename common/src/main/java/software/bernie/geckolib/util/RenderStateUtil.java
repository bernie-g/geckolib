package software.bernie.geckolib.util;

import net.minecraft.client.renderer.entity.state.*;
import software.bernie.geckolib.renderer.base.GeoRenderState;

/// Helper class for RenderState-related functionality.
///
/// Primarily used for cloning RenderStates
public final class RenderStateUtil {
    /// Create a fully cloned copy of an existing [EntityRenderState].
    public static EntityRenderState cloneEntityState(EntityRenderState existingState) {
        final EntityRenderState newState = new EntityRenderState();

        fullCopyEntityState(newState, existingState);

        return newState;
    }

    /// Fully copy an existing [EntityRenderState] to a new one.
    public static void fullCopyEntityState(EntityRenderState newRenderState, EntityRenderState oldRenderState) {
        newRenderState.entityType = oldRenderState.entityType;
        newRenderState.x = oldRenderState.x;
        newRenderState.y = oldRenderState.y;
        newRenderState.z = oldRenderState.z;
        newRenderState.ageInTicks = oldRenderState.ageInTicks;
        newRenderState.boundingBoxWidth = oldRenderState.boundingBoxWidth;
        newRenderState.boundingBoxHeight = oldRenderState.boundingBoxHeight;
        newRenderState.eyeHeight = oldRenderState.eyeHeight;
        newRenderState.distanceToCameraSq = oldRenderState.distanceToCameraSq;
        newRenderState.isInvisible = oldRenderState.isInvisible;
        newRenderState.isDiscrete = oldRenderState.isDiscrete;
        newRenderState.displayFireAnimation = oldRenderState.displayFireAnimation;
        newRenderState.lightCoords = oldRenderState.lightCoords;
        newRenderState.outlineColor = oldRenderState.outlineColor;
        newRenderState.passengerOffset = oldRenderState.passengerOffset;
        newRenderState.nameTag = oldRenderState.nameTag;
        newRenderState.nameTagAttachment = oldRenderState.nameTagAttachment;
        newRenderState.leashStates = oldRenderState.leashStates;
        newRenderState.shadowRadius = oldRenderState.shadowRadius;

        newRenderState.shadowPieces.addAll(oldRenderState.shadowPieces);
        ((GeoRenderState)newRenderState).getDataMap().putAll(((GeoRenderState)oldRenderState).getDataMap());
    }

    /// Create a fully cloned copy of an existing [LivingEntityRenderState].
    public static LivingEntityRenderState cloneLivingEntityState(LivingEntityRenderState existingState) {
        final LivingEntityRenderState newState = new LivingEntityRenderState();

        fullCopyLivingEntityState(newState, existingState);

        return newState;
    }

    /// Fully copy an existing [LivingEntityRenderState] to a new one.
    public static void fullCopyLivingEntityState(LivingEntityRenderState newRenderState, LivingEntityRenderState oldRenderState) {
        fullCopyEntityState(newRenderState, oldRenderState);

        newRenderState.bodyRot = oldRenderState.bodyRot;
        newRenderState.yRot = oldRenderState.yRot;
        newRenderState.xRot = oldRenderState.xRot;
        newRenderState.deathTime = oldRenderState.deathTime;
        newRenderState.walkAnimationPos = oldRenderState.walkAnimationPos;
        newRenderState.walkAnimationSpeed = oldRenderState.walkAnimationSpeed;
        newRenderState.scale = oldRenderState.scale;
        newRenderState.ageScale = oldRenderState.ageScale;
        newRenderState.isUpsideDown = oldRenderState.isUpsideDown;
        newRenderState.isFullyFrozen = oldRenderState.isFullyFrozen;
        newRenderState.isBaby = oldRenderState.isBaby;
        newRenderState.isInWater = oldRenderState.isInWater;
        newRenderState.isAutoSpinAttack = oldRenderState.isAutoSpinAttack;
        newRenderState.hasRedOverlay = oldRenderState.hasRedOverlay;
        newRenderState.isInvisibleToPlayer = oldRenderState.isInvisibleToPlayer;
        newRenderState.bedOrientation = oldRenderState.bedOrientation;
        newRenderState.pose = oldRenderState.pose;
        newRenderState.headItem = oldRenderState.headItem;
        newRenderState.wornHeadAnimationPos = oldRenderState.wornHeadAnimationPos;
        newRenderState.wornHeadType = oldRenderState.wornHeadType;
        newRenderState.wornHeadProfile = oldRenderState.wornHeadProfile;
    }

    /// Create a fully cloned copy of an existing [ArmedEntityRenderState].
    public static ArmedEntityRenderState cloneArmedEntityState(ArmedEntityRenderState existingState) {
        final ArmedEntityRenderState newState = new ArmedEntityRenderState();

        fullCopyArmedEntityState(newState, existingState);

        return newState;
    }

    /// Fully copy an existing [ArmedEntityRenderState] to a new one.
    public static void fullCopyArmedEntityState(ArmedEntityRenderState newRenderState, ArmedEntityRenderState oldRenderState) {
        fullCopyLivingEntityState(newRenderState, oldRenderState);

        newRenderState.mainArm = oldRenderState.mainArm;
        newRenderState.rightArmPose = oldRenderState.rightArmPose;
        newRenderState.rightHandItemState = oldRenderState.rightHandItemState;
        newRenderState.leftArmPose = oldRenderState.leftArmPose;
        newRenderState.leftHandItemState = oldRenderState.leftHandItemState;
    }

    /// Create a fully cloned copy of an existing [HumanoidRenderState].
    public static HumanoidRenderState cloneHumanoidEntityState(HumanoidRenderState existingState) {
        final HumanoidRenderState newState = new HumanoidRenderState();

        fullCopyHumanoidEntityState(newState, existingState);

        return newState;
    }

    /// Fully copy an existing [HumanoidRenderState] to a new one.
    public static void fullCopyHumanoidEntityState(HumanoidRenderState newRenderState, HumanoidRenderState oldRenderState) {
        fullCopyArmedEntityState(newRenderState, oldRenderState);

        newRenderState.swimAmount = oldRenderState.swimAmount;
        newRenderState.attackTime = oldRenderState.attackTime;
        newRenderState.speedValue = oldRenderState.speedValue;
        newRenderState.maxCrossbowChargeDuration = oldRenderState.maxCrossbowChargeDuration;
        newRenderState.ticksUsingItem = oldRenderState.ticksUsingItem;
        newRenderState.attackArm = oldRenderState.attackArm;
        newRenderState.useItemHand = oldRenderState.useItemHand;
        newRenderState.isCrouching = oldRenderState.isCrouching;
        newRenderState.isFallFlying = oldRenderState.isFallFlying;
        newRenderState.isVisuallySwimming = oldRenderState.isVisuallySwimming;
        newRenderState.isPassenger = oldRenderState.isPassenger;
        newRenderState.isUsingItem = oldRenderState.isUsingItem;
        newRenderState.elytraRotX = oldRenderState.elytraRotX;
        newRenderState.elytraRotY = oldRenderState.elytraRotY;
        newRenderState.elytraRotZ = oldRenderState.elytraRotZ;
        newRenderState.headEquipment = oldRenderState.headEquipment;
        newRenderState.chestEquipment = oldRenderState.chestEquipment;
        newRenderState.legsEquipment = oldRenderState.legsEquipment;
        newRenderState.feetEquipment = oldRenderState.feetEquipment;
    }

    /// Create a fully cloned copy of an existing [AvatarRenderState].
    public static AvatarRenderState cloneAvatarEntityState(AvatarRenderState existingState) {
        final AvatarRenderState newState = new AvatarRenderState();

        fullCopyAvatarEntityState(newState, existingState);

        return newState;
    }

    /// Fully copy an existing [AvatarRenderState] to a new one.
    public static void fullCopyAvatarEntityState(AvatarRenderState newRenderState, AvatarRenderState oldRenderState) {
        fullCopyHumanoidEntityState(newRenderState, oldRenderState);

        newRenderState.skin = oldRenderState.skin;
        newRenderState.capeFlap = oldRenderState.capeFlap;
        newRenderState.capeLean = oldRenderState.capeLean;
        newRenderState.capeLean2 = oldRenderState.capeLean2;
        newRenderState.arrowCount = oldRenderState.arrowCount;
        newRenderState.stingerCount = oldRenderState.stingerCount;
        newRenderState.isSpectator = oldRenderState.isSpectator;
        newRenderState.showHat = oldRenderState.showHat;
        newRenderState.showJacket = oldRenderState.showJacket;
        newRenderState.showLeftPants = oldRenderState.showLeftPants;
        newRenderState.showRightPants = oldRenderState.showRightPants;
        newRenderState.showLeftSleeve = oldRenderState.showLeftSleeve;
        newRenderState.showRightSleeve = oldRenderState.showRightSleeve;
        newRenderState.showCape = oldRenderState.showCape;
        newRenderState.fallFlyingTimeInTicks = oldRenderState.fallFlyingTimeInTicks;
        newRenderState.shouldApplyFlyingYRot = oldRenderState.shouldApplyFlyingYRot;
        newRenderState.flyingYRot = oldRenderState.flyingYRot;
        newRenderState.scoreText = oldRenderState.scoreText;
        newRenderState.parrotOnLeftShoulder = oldRenderState.parrotOnLeftShoulder;
        newRenderState.parrotOnRightShoulder = oldRenderState.parrotOnRightShoulder;
        newRenderState.id = oldRenderState.id;
        newRenderState.showExtraEars = oldRenderState.showExtraEars;
        newRenderState.heldOnHead = oldRenderState.heldOnHead;
    }

    /// Create a partial-clone of an existing unknown RenderState into a new [HumanoidRenderState] for the purpose of
    /// armor rendering, which explicitly requires an `HumanoidRenderState`
    ///
    /// Because this is only being used for armor rendering, we don't need an exhaustive copy of the renderstate and instead focus
    /// solely on the data points we know are needed.
    ///
    /// If you are doing custom modeling and a data point here is missing and causing you issues, let me know in Discord and I'll add it
    public static HumanoidRenderState makeMinimalArmorRenderingClone(final HumanoidRenderState newRenderState, final EntityRenderState oldRenderState) {
        ((GeoRenderState)newRenderState).getDataMap().putAll(((GeoRenderState)oldRenderState).getDataMap());

        newRenderState.entityType = oldRenderState.entityType; // Optional
        newRenderState.x = oldRenderState.x; // Optional
        newRenderState.y = oldRenderState.y; // Optional
        newRenderState.z = oldRenderState.z; // Optional
        newRenderState.ageInTicks = oldRenderState.ageInTicks;
        newRenderState.eyeHeight = oldRenderState.eyeHeight; // Optional
        newRenderState.distanceToCameraSq = oldRenderState.distanceToCameraSq; // Optional
        newRenderState.isInvisible = oldRenderState.isInvisible; // Optional
        newRenderState.isDiscrete = oldRenderState.isDiscrete; // Optional
        newRenderState.displayFireAnimation = oldRenderState.displayFireAnimation; // Optional
        newRenderState.lightCoords = oldRenderState.lightCoords; // Optional
        newRenderState.outlineColor = oldRenderState.outlineColor; // Optional

        if (oldRenderState instanceof LivingEntityRenderState livingEntityState) {
            newRenderState.bodyRot = livingEntityState.bodyRot; // Optional
            newRenderState.yRot = livingEntityState.yRot;
            newRenderState.xRot = livingEntityState.xRot;
            newRenderState.deathTime = livingEntityState.deathTime; // Optional
            newRenderState.walkAnimationPos = livingEntityState.walkAnimationPos;
            newRenderState.walkAnimationSpeed = livingEntityState.walkAnimationSpeed;
            newRenderState.scale = livingEntityState.scale; // Optional
            newRenderState.ageScale = livingEntityState.ageScale;
            newRenderState.isUpsideDown = livingEntityState.isUpsideDown; // Optional
            newRenderState.isFullyFrozen = livingEntityState.isFullyFrozen; // Optional
            newRenderState.isBaby = livingEntityState.isBaby; // Optional
            newRenderState.isInWater = livingEntityState.isInWater; // Optional
            newRenderState.isAutoSpinAttack = livingEntityState.isAutoSpinAttack; // Optional
            newRenderState.hasRedOverlay = livingEntityState.hasRedOverlay; // Optional
            newRenderState.isInvisibleToPlayer = livingEntityState.isInvisibleToPlayer; // Optional
            newRenderState.bedOrientation = livingEntityState.bedOrientation; // Optional
            newRenderState.pose = livingEntityState.pose; // Optional

            if (livingEntityState instanceof ArmedEntityRenderState armedState) {
                newRenderState.mainArm = armedState.mainArm;
                newRenderState.rightArmPose = armedState.rightArmPose;
                newRenderState.leftArmPose = armedState.leftArmPose;

                if (armedState instanceof HumanoidRenderState humanoidState) {
                    newRenderState.swimAmount = humanoidState.swimAmount;
                    newRenderState.attackTime = humanoidState.attackTime;
                    newRenderState.speedValue = humanoidState.speedValue;
                    newRenderState.maxCrossbowChargeDuration = humanoidState.maxCrossbowChargeDuration;
                    newRenderState.ticksUsingItem = humanoidState.ticksUsingItem;
                    newRenderState.attackArm = humanoidState.attackArm;
                    newRenderState.useItemHand = humanoidState.useItemHand;
                    newRenderState.isCrouching = humanoidState.isCrouching;
                    newRenderState.isFallFlying = humanoidState.isFallFlying;
                    newRenderState.isVisuallySwimming = humanoidState.isVisuallySwimming; // Optional
                    newRenderState.isPassenger = humanoidState.isPassenger;
                    newRenderState.isUsingItem = humanoidState.isUsingItem;
                    newRenderState.elytraRotX = humanoidState.elytraRotX; // Optional
                    newRenderState.elytraRotY = humanoidState.elytraRotY; // Optional
                    newRenderState.elytraRotZ = humanoidState.elytraRotZ; // Optional
                    newRenderState.headEquipment = humanoidState.headEquipment; // Optional
                    newRenderState.chestEquipment = humanoidState.chestEquipment; // Optional
                    newRenderState.legsEquipment = humanoidState.legsEquipment; // Optional
                    newRenderState.feetEquipment = humanoidState.feetEquipment; // Optional
                }
            }
        }

        return newRenderState;
    }

    private RenderStateUtil() {}
}
