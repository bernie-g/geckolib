package software.bernie.geckolib.geo.render.built;

import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.core.snapshot.BoneSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GeoBone implements IBone
{
	public GeoBone parent;

	public List<GeoBone> childBones = new ArrayList<>();
	public List<GeoCube> childCubes = new ArrayList<>();

	public String name;
	private BoneSnapshot initialSnapshot;

	public Boolean mirror;
	public Double inflate;
	public Boolean dontRender;

	//I still have no idea what this field does, but its in the json file so ¯\_(ツ)_/¯
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

	@Override
	public void setModelRendererName(String modelRendererName)
	{
		this.name = modelRendererName;
	}

	@Override
	public void saveInitialSnapshot()
	{
		this.initialSnapshot = new BoneSnapshot(this, true);
	}

	@Override
	public BoneSnapshot getInitialSnapshot()
	{
		return this.initialSnapshot;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	// Boilerplate code incoming

	@Override
	public float getRotationX()
	{
		return rotateX;
	}

	@Override
	public float getRotationY()
	{
		return rotateY;
	}

	@Override
	public float getRotationZ()
	{
		return rotateZ;
	}

	@Override
	public float getPositionX()
	{
		return positionX;
	}

	@Override
	public float getPositionY()
	{
		return positionY;
	}

	@Override
	public float getPositionZ()
	{
		return positionZ;
	}

	@Override
	public float getScaleX()
	{
		return scaleX;
	}

	@Override
	public float getScaleY()
	{
		return scaleY;
	}

	@Override
	public float getScaleZ()
	{
		return scaleZ;
	}

	@Override
	public void setRotationX(float value)
	{
		this.rotateX = value;
	}

	@Override
	public void setRotationY(float value)
	{
		this.rotateY = value;
	}

	@Override
	public void setRotationZ(float value)
	{
		this.rotateZ = value;
	}

	@Override
	public void setPositionX(float value)
	{
		this.positionX = value;
	}

	@Override
	public void setPositionY(float value)
	{
		this.positionY = value;
	}

	@Override
	public void setPositionZ(float value)
	{
		this.positionZ = value;
	}

	@Override
	public void setScaleX(float value)
	{
		this.scaleX = value;
	}

	@Override
	public void setScaleY(float value)
	{
		this.scaleY = value;
	}

	@Override
	public void setScaleZ(float value)
	{
		this.scaleZ = value;
	}
}
