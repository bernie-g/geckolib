package software.bernie.geckolib.core.animatable.model;

import software.bernie.geckolib.core.state.BoneSnapshot;

import java.util.List;

/**
 * Base class for Geckolib {@link CoreGeoModel model} bones.<br>
 * Mostly a placeholder to allow for splitting up core (non-Minecraft) libraries
 */
public interface CoreGeoBone {
	String getName();

	CoreGeoBone getParent();

	float getRotX();

	float getRotY();

	float getRotZ();

	float getPosX();

	float getPosY();

	float getPosZ();

	float getScaleX();

	float getScaleY();

	float getScaleZ();

	void setRotX(float value);

	void setRotY(float value);

	void setRotZ(float value);

	default void updateRotation(float xRot, float yRot, float zRot) {
		setRotX(xRot);
		setRotY(yRot);
		setRotZ(zRot);
	}

	void setPosX(float value);

	void setPosY(float value);

	void setPosZ(float value);

	default void updatePosition(float posX, float posY, float posZ) {
		setPosX(posX);
		setPosY(posY);
		setPosZ(posZ);
	}

	void setScaleX(float value);

	void setScaleY(float value);

	void setScaleZ(float value);

	default void updateScale(float scaleX, float scaleY, float scaleZ) {
		setScaleX(scaleX);
		setScaleY(scaleY);
		setScaleZ(scaleZ);
	}

	void setPivotX(float value);

	void setPivotY(float value);

	void setPivotZ(float value);

	default void updatePivot(float pivotX, float pivotY, float pivotZ) {
		setPivotX(pivotX);
		setPivotY(pivotY);
		setPivotZ(pivotZ);
	}

	float getPivotX();

	float getPivotY();

	float getPivotZ();

	boolean isHidden();

	boolean isHidingChildren();

	void setHidden(boolean hidden);

	void setChildrenHidden(boolean hideChildren);

	void saveInitialSnapshot();

	void markScaleAsChanged();

	void markRotationAsChanged();

	void markPositionAsChanged();

	boolean hasScaleChanged();

	boolean hasRotationChanged();

	boolean hasPositionChanged();

	void resetStateChanges();

	BoneSnapshot getInitialSnapshot();

	List<? extends CoreGeoBone> getChildBones();

	default BoneSnapshot saveSnapshot() {
		return new BoneSnapshot(this);
	}
}
