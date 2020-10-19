package software.bernie.geckolib.core.processor;

import software.bernie.geckolib.core.snapshot.BoneSnapshot;

public interface IBone {
    float getRotationX();
    float getRotationY();
    float getRotationZ();

    void setRotationX(float value);
    void setRotationY(float value);
    void setRotationZ(float value);

    float getPositionX();
    float getPositionY();
    float getPositionZ();

    void setPositionX(float value);
    void setPositionY(float value);
    void setPositionZ(float value);

    float getScaleX();
    float getScaleY();
    float getScaleZ();

    void setScaleZ(float value);
    void setScaleY(float value);
    void setScaleX(float value);

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
