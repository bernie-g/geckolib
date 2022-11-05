package software.bernie.geckolib3.core.animatable.model;

import software.bernie.geckolib3.core.state.BoneSnapshot;

import java.util.List;

/**
 * Base class for Geckolib {@link GeoModel model} bones.<br>
 * Mostly a placeholder to allow for splitting up core (non-Minecraft) libraries
 */
public interface GeoBone {
	String getName();

	float getRotationX();

	float getRotationY();

	float getRotationZ();

	float getPositionX();

	float getPositionY();

	float getPositionZ();

	float getScaleX();

	float getScaleY();

	float getScaleZ();

	void setRotationX(float value);

	void setRotationY(float value);

	void setRotationZ(float value);

	void setPositionX(float value);

	void setPositionY(float value);

	void setPositionZ(float value);

	void setScaleX(float value);

	void setScaleY(float value);

	void setScaleZ(float value);

	void setPivotX(float value);

	void setPivotY(float value);

	void setPivotZ(float value);

	float getPivotX();

	float getPivotY();

	float getPivotZ();

	boolean isHidden();

	boolean cubesAreHidden();

	boolean childBonesAreHiddenToo();

	void setHidden(boolean hidden);

	void setCubesHidden(boolean hidden);

	void setHidden(boolean selfHidden, boolean skipChildRendering);

	void setModelRendererName(String modelRendererName);

	void saveInitialSnapshot();

	void markScaleAsChanged();

	void markRotationAsChanged();

	void markPositionAsChanged();

	boolean hasScaleChanged();

	boolean hasRotationChanged();

	boolean hasPositionChanged();

	boolean resetStateChanges();

	BoneSnapshot getInitialSnapshot();

	List<GeoBone> getChildBones();

	default BoneSnapshot saveSnapshot() {
		return new BoneSnapshot(this);
	}
}
