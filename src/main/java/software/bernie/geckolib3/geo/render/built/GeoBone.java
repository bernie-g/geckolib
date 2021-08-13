package software.bernie.geckolib3.geo.render.built;

import java.util.ArrayList;
import java.util.List;

import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

public class GeoBone implements IBone {
	public GeoBone parent;

	public List<GeoBone> childBones = new ArrayList<>();
	public List<GeoCube> childCubes = new ArrayList<>();

	public String name;
	private BoneSnapshot initialSnapshot;

	public Boolean mirror;
	public Double inflate;
	public Boolean dontRender;
	public boolean isHidden;
	// I still have no idea what this field does, but its in the json file so
	// ¯\_(ツ)_/¯
	public Boolean reset;

	private float scaleX = 1;
	private float scaleY = 1;
	private float scaleZ = 1;

	private float positionX;
	private float positionY;
	private float positionZ;

	private float rotationPointX;
	private float rotationPointY;
	private float rotationPointZ;

	private float rotateX;
	private float rotateY;
	private float rotateZ;

	public Object extraData;

	private boolean dirty = false;

	@Override
	public void setModelRendererName(String modelRendererName) {
		this.name = modelRendererName;
	}

	@Override
	public void saveInitialSnapshot() {
		if (this.initialSnapshot == null) {
			this.initialSnapshot = new BoneSnapshot(this, true);
		}
	}

	@Override
	public BoneSnapshot getInitialSnapshot() {
		return this.initialSnapshot;
	}

	@Override
	public String getName() {
		return this.name;
	}

	// Boilerplate code incoming

	@Override
	public float getRotationX() {
		return rotateX;
	}

	@Override
	public float getRotationY() {
		return rotateY;
	}

	@Override
	public float getRotationZ() {
		return rotateZ;
	}

	@Override
	public float getPositionX() {
		return positionX;
	}

	@Override
	public float getPositionY() {
		return positionY;
	}

	@Override
	public float getPositionZ() {
		return positionZ;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	@Override
	public float getScaleZ() {
		return scaleZ;
	}

	@Override
	public void setRotationX(float value) {
		if(this.rotateX != value) this.dirty = true;
		this.rotateX = value;
	}

	@Override
	public void setRotationY(float value) {
		if(this.rotateY != value) this.dirty = true;
		this.rotateY = value;
	}

	@Override
	public void setRotationZ(float value) {
		if(this.rotateZ != value) this.dirty = true;
		this.rotateZ = value;
	}

	@Override
	public void setPositionX(float value) {
		if(this.positionX != value) this.dirty = true;
		this.positionX = value;
	}

	@Override
	public void setPositionY(float value) {
		if(this.positionY != value) this.dirty = true;
		this.positionY = value;
	}

	@Override
	public void setPositionZ(float value) {
		if(this.positionZ != value) this.dirty = true;
		this.positionZ = value;
	}

	@Override
	public void setScaleX(float value) {
		if(this.scaleX != value) this.dirty = true;
		this.scaleX = value;
	}

	@Override
	public void setScaleY(float value) {
		if(this.scaleY != value) this.dirty = true;
		this.scaleY = value;
	}

	@Override
	public void setScaleZ(float value) {
		if(this.scaleZ != value) this.dirty = true;
		this.scaleZ = value;
	}

	@Override
	public boolean isHidden() {
		return this.isHidden;
	}

	@Override
	public void setHidden(boolean hidden) {
		if(this.isHidden != hidden) this.dirty = true;
		this.isHidden = hidden;
	}

	@Override
	public void setPivotX(float value) {
		if(this.rotationPointX != value) this.dirty = true;
		this.rotationPointX = value;
	}

	@Override
	public void setPivotY(float value) {
		if(this.rotationPointY != value) this.dirty = true;
		this.rotationPointY = value;
	}

	@Override
	public void setPivotZ(float value) {
		if(this.rotationPointZ != value) this.dirty = true;
		this.rotationPointZ = value;
	}

	@Override
	public float getPivotX() {
		return this.rotationPointX;
	}

	@Override
	public float getPivotY() {
		return this.rotationPointY;
	}

	@Override
	public float getPivotZ() {
		return this.rotationPointZ;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
