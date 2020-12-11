package software.bernie.geckolib3.geo.render.built;

import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GeoBone implements IBone
{
	public GeoBone parent;

	public List<GeoBone> childBones = new ArrayList<>();
	public List<GeoCube> childCubes = new ArrayList<>();

	public String name;
	public Boolean mirror;
	public Double inflate;
	public Boolean dontRender;
	public boolean isHidden;
	//I still have no idea what this field does, but its in the json file so ¯\_(ツ)_/¯
	public Boolean reset;
	public float pivotX;
	public float pivotY;
	public float pivotZ;
	private BoneSnapshot initialSnapshot;
	private float scaleX = 1;
	private float scaleY = 1;
	private float scaleZ = 1;
	private float positionX;
	private float positionY;
	private float positionZ;
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
		if (this.initialSnapshot == null)
		{
			this.initialSnapshot = new BoneSnapshot(this, true);
		}
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
	public void setRotationX(float value)
	{
		this.rotateX = value;
	}

	@Override
	public float getRotationY()
	{
		return rotateY;
	}

	@Override
	public void setRotationY(float value)
	{
		this.rotateY = value;
	}

	@Override
	public float getRotationZ()
	{
		return rotateZ;
	}

	@Override
	public void setRotationZ(float value)
	{
		this.rotateZ = value;
	}

	@Override
	public float getPositionX()
	{
		return positionX;
	}

	@Override
	public void setPositionX(float value)
	{
		this.positionX = value;
	}

	@Override
	public float getPositionY()
	{
		return positionY;
	}

	@Override
	public void setPositionY(float value)
	{
		this.positionY = value;
	}

	@Override
	public float getPositionZ()
	{
		return positionZ;
	}

	@Override
	public void setPositionZ(float value)
	{
		this.positionZ = value;
	}

	@Override
	public float getScaleX()
	{
		return scaleX;
	}

	@Override
	public void setScaleX(float value)
	{
		this.scaleX = value;
	}

	@Override
	public float getScaleY()
	{
		return scaleY;
	}

	@Override
	public void setScaleY(float value)
	{
		this.scaleY = value;
	}

	@Override
	public float getScaleZ()
	{
		return scaleZ;
	}

	@Override
	public void setScaleZ(float value)
	{
		this.scaleZ = value;
	}

	@Override
	public void setPivotX(float value)
	{
		this.pivotX = value;
	}

	@Override
	public void setPivotY(float value)
	{
		this.pivotY = value;
	}

	@Override
	public void setPivotZ(float value)
	{
        this.pivotZ = value;
	}

	@Override
	public float getPivotX()
	{
		return this.pivotX;
	}

	@Override
	public float getPivotY()
	{
		return this.pivotY;
	}

	@Override
	public float getPivotZ()
	{
		return this.pivotZ;
	}

	@Override
	public boolean isHidden()
	{
		return this.isHidden;
	}

	@Override
	public void setHidden(boolean hidden)
	{
		this.isHidden = hidden;
	}

}
