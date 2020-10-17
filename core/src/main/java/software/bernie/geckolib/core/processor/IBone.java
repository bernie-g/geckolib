package software.bernie.geckolib.core.processor;

import software.bernie.geckolib.core.snapshot.BoneSnapshot;

public interface IBone {
    float getPitch();

    void setPitch(float value);

    float getYaw();

    void setYaw(float value);

    float getRoll();

    void setRoll(float value);

    float getPositionX();

    void setPositionX(float value);

    float getPositionY();

    void setPositionY(float value);

    float getPositionZ();

    void setPositionZ(float value);

    float getScaleX();

    void setScaleX(float value);

    float getScaleY();

    void setScaleY(float value);

    float getScaleZ();

    void setScaleZ(float value);

    boolean isHidden();

    void setHidden(boolean hidden);

    void setModelRendererName(String modelRendererName);

    void saveInitialSnapshot();

    BoneSnapshot getInitialSnapshot();

    default BoneSnapshot saveSnapshot() {
        return new BoneSnapshot(this);
    }

    String getName();
}
