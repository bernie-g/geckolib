package anightdazingzoroark.riftlib.geo.render.built;

import java.util.ArrayList;
import java.util.List;

import anightdazingzoroark.riftlib.core.processor.IBone;
import anightdazingzoroark.riftlib.core.snapshot.BoneSnapshot;

public class GeoBone implements IBone {
	public GeoBone parent;

	public List<GeoBone> childBones = new ArrayList<>();
	public List<GeoCube> childCubes = new ArrayList<>();
	public List<GeoLocator> childLocators = new ArrayList<>();

	public String name;
	private BoneSnapshot initialSnapshot;

	public Boolean mirror;
	public Double inflate;
	public Boolean dontRender;
	public boolean isHidden;
	public boolean areCubesHidden = false;
	public boolean hideChildBonesToo;
	// I still have no idea what this field does, but its in the json file so
	// ¯\_(ツ)_/¯
	public Boolean reset;

	private float scaleX = 1;
	private float scaleY = 1;
	private float scaleZ = 1;

	private float positionX;
	private float positionY;
	private float positionZ;

	public float rotationPointX;
	public float rotationPointY;
	public float rotationPointZ;

	private float rotateX;
	private float rotateY;
	private float rotateZ;

	public Object extraData;

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
		/*
		if (this.name.equals("neck")) {
			System.out.println(this.name+" x rot deg: "+Math.toDegrees(this.rotateX));
			for (GeoLocator locator : this.childLocators) {
				System.out.println("locator "+locator.name+" x rot deg: "+Math.toDegrees(locator.getXRotationFromPivot()));
				float hypotenuse = (float) Math.sqrt(Math.pow(locator.positionZ - (this.getPivotZ() / 16), 2) + Math.pow(locator.positionY - (this.getPivotY() / 16), 2));
				float yDisplacement = (float) (hypotenuse * Math.cos(this.rotateX + locator.getXRotationFromPivot()));
				float zDisplacement = (float) (hypotenuse * Math.sin(this.rotateX + locator.getXRotationFromPivot()));
				//locator.positionY = yDisplacement;
				//locator.positionZ = zDisplacement;
				//System.out.println("y pos: "+locator.positionY);
				//System.out.println("z pos: "+locator.positionZ);
			}
		}
		 */

		this.rotateX = value;

		/*
		//setting x rotation of a cube sets the y and z positions of the locators from the pivot point
		for (GeoLocator locator : this.childLocators) {
			float hypotenuse = (float) Math.sqrt(Math.pow(locator.positionZ - this.getPivotZ(), 2) + Math.pow(locator.positionY - this.getPivotY(), 2));
			float yDisplacement = (float) (hypotenuse * Math.cos(value + locator.getXRotationFromPivot()));
			float zDisplacement = (float) (hypotenuse * Math.sin(value + locator.getXRotationFromPivot()));
			locator.positionY = yDisplacement / 16f;
			locator.positionZ = zDisplacement / 16f;
		}
		 */
	}

	@Override
	public void setRotationY(float value) {
		this.rotateY = value;

		/*
		//setting y rotation of a cube sets the x and z positions of the locators from the pivot point
		for (GeoLocator locator : this.childLocators) {
			float hypotenuse = (float) Math.sqrt(Math.pow(locator.positionZ - this.getPivotZ(), 2) + Math.pow(locator.positionX - this.getPivotX(), 2));
			float zDisplacement = -(float) (hypotenuse * Math.cos(value));
			float xDisplacement = (float) (hypotenuse * Math.sin(value));
			locator.positionX = xDisplacement / 16f;
			locator.positionZ = zDisplacement / 16f;
		}
		 */
	}

	@Override
	public void setRotationZ(float value) {
		this.rotateZ = value;

		/*
		//setting z rotation of a cube sets the x and y positions of the locators from the pivot point
		for (GeoLocator locator : this.childLocators) {
			float hypotenuse = (float) Math.sqrt(Math.pow(locator.positionX - this.getPivotX(), 2) + Math.pow(locator.positionY - this.getPivotY(), 2));
			float yDisplacement = (float) (hypotenuse * Math.sin(value));
			float xDisplacement = (float) (hypotenuse * Math.cos(value));
			locator.positionY = yDisplacement / 16f;
			locator.positionX = xDisplacement / 16f;
		}
		 */
	}

	@Override
	public void setPositionX(float value) {
		this.positionX = value;
	}

	@Override
	public void setPositionY(float value) {
		this.positionY = value;
	}

	@Override
	public void setPositionZ(float value) {
		this.positionZ = value;
	}

	@Override
	public void setScaleX(float value) {
		this.scaleX = value;
	}

	@Override
	public void setScaleY(float value) {
		this.scaleY = value;
	}

	@Override
	public void setScaleZ(float value) {
		this.scaleZ = value;
	}

	@Override
	public boolean isHidden() {
		return this.isHidden;
	}

	@Override
	public void setHidden(boolean hidden) {
		this.setHidden(hidden, hidden);
	}

	@Override
	public void setPivotX(float value) {
		this.rotationPointX = value;
	}

	@Override
	public void setPivotY(float value) {
		this.rotationPointY = value;
	}

	@Override
	public void setPivotZ(float value) {
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
	public boolean cubesAreHidden() {
		return areCubesHidden;
	}

	@Override
	public boolean childBonesAreHiddenToo() {
		return hideChildBonesToo;
	}

	@Override
	public void setCubesHidden(boolean hidden) {
		this.areCubesHidden = hidden;
	}

	@Override
	public void setHidden(boolean selfHidden, boolean skipChildRendering) {
		this.isHidden = selfHidden;
		this.hideChildBonesToo = skipChildRendering;
	}
}
