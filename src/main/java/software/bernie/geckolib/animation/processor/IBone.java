package software.bernie.geckolib.animation.processor;

import software.bernie.geckolib.animation.snapshot.BoneSnapshot;

public interface IBone
{
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

	void setModelRendererName(String modelRendererName);

	void saveInitialSnapshot();
	BoneSnapshot getInitialSnapshot();
	default BoneSnapshot saveSnapshot()
	{
		return new BoneSnapshot(this);
	}
	String getName();
}
