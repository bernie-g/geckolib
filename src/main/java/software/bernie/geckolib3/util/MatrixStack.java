package software.bernie.geckolib3.util;

import org.lwjgl.util.vector.Quaternion;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.Stack;

/**
 * Simple implementation of a matrix stack
 */
public class MatrixStack
{
	private Stack<Matrix4f> model = new Stack<Matrix4f>();
	private Stack<Matrix3f> normal = new Stack<Matrix3f>();

	private Matrix4f tempModelMatrix = new Matrix4f();
	private Matrix3f tempNormalMatrix = new Matrix3f();
	private float[] tempArray = new float[16];

	public MatrixStack()
	{
		Matrix4f model = new Matrix4f();
		Matrix3f normal = new Matrix3f();

		model.setIdentity();
		normal.setIdentity();

		this.model.add(model);
		this.normal.add(normal);
	}

	public Matrix4f getModelMatrix()
	{
		return this.model.peek();
	}

	public Matrix3f getNormalMatrix()
	{
		return this.normal.peek();
	}

	public void push()
	{
		this.model.add(new Matrix4f(this.model.peek()));
		this.normal.add(new Matrix3f(this.normal.peek()));
	}

	public void pop()
	{
		if (this.model.size() == 1)
		{
			throw new IllegalStateException("A one level stack can't be popped!");
		}

		this.model.pop();
		this.normal.pop();
	}

	/* Translate */

	public void translate(float x, float y, float z)
	{
		this.translate(new Vector3f(x, y, z));
	}

	public void translate(Vector3f vec)
	{
		this.tempModelMatrix.setIdentity();
		this.tempModelMatrix.setTranslation(vec);

		this.model.peek().mul(this.tempModelMatrix);
	}

	public void moveToPivot(GeoCube cube)
	{
		Vector3f pivot = cube.pivot;
		this.translate(pivot.getX() / 16, pivot.getY() / 16, pivot.getZ() / 16);
	}

	public void moveBackFromPivot(GeoCube cube)
	{
		Vector3f pivot = cube.pivot;
		this.translate(-pivot.getX() / 16, -pivot.getY() / 16, -pivot.getZ() / 16);
	}

	public void moveToPivot(GeoBone bone)
	{
		this.translate(bone.rotationPointX / 16, bone.rotationPointY / 16, bone.rotationPointZ / 16);
	}

	public void moveBackFromPivot(GeoBone bone)
	{
		this.translate(-bone.rotationPointX / 16, -bone.rotationPointY / 16, -bone.rotationPointZ / 16);
	}

	public void translate(GeoBone bone)
	{
		this.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
	}

	/* Scale */

	public void scale(float x, float y, float z)
	{
		this.tempModelMatrix.setIdentity();
		this.tempModelMatrix.setM00(x);
		this.tempModelMatrix.setM11(y);
		this.tempModelMatrix.setM22(z);

		this.model.peek().mul(this.tempModelMatrix);

		if (x < 0 || y < 0 || z < 0)
		{
			this.tempNormalMatrix.setIdentity();
			this.tempNormalMatrix.setM00(x < 0 ? -1 : 1);
			this.tempNormalMatrix.setM11(y < 0 ? -1 : 1);
			this.tempNormalMatrix.setM22(z < 0 ? -1 : 1);

			this.normal.peek().mul(this.tempNormalMatrix);
		}
	}

	public void scale(GeoBone bone)
	{
		this.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	/* Rotate */

	public void rotateX(float radian)
	{
		this.tempModelMatrix.setIdentity();
		this.tempModelMatrix.rotX(radian);

		this.tempNormalMatrix.setIdentity();
		this.tempNormalMatrix.rotX(radian);

		this.model.peek().mul(this.tempModelMatrix);
		this.normal.peek().mul(this.tempNormalMatrix);
	}

	public void rotateY(float radian)
	{
		this.tempModelMatrix.setIdentity();
		this.tempModelMatrix.rotY(radian);

		this.tempNormalMatrix.setIdentity();
		this.tempNormalMatrix.rotY(radian);

		this.model.peek().mul(this.tempModelMatrix);
		this.normal.peek().mul(this.tempNormalMatrix);
	}

	public void rotateZ(float radian)
	{
		this.tempModelMatrix.setIdentity();
		this.tempModelMatrix.rotZ(radian);

		this.tempNormalMatrix.setIdentity();
		this.tempNormalMatrix.rotZ(radian);

		this.model.peek().mul(this.tempModelMatrix);
		this.normal.peek().mul(this.tempNormalMatrix);
	}

	public void rotate(GeoBone bone)
	{
		if (bone.getRotationZ() != 0.0F)
		{
			this.rotateZ(bone.getRotationZ());
		}

		if (bone.getRotationY() != 0.0F)
		{
			this.rotateY(bone.getRotationY());
		}

		if (bone.getRotationX() != 0.0F)
		{
			this.rotateX(bone.getRotationX());
		}
	}

	public void rotate(GeoCube bone)
	{
		Vector3f rotation = bone.rotation;
		Quaternion quat = new Quaternion(rotation.getX(), rotation.getY(), rotation.getZ(), 0);

		this.tempNormalMatrix.setIdentity();
		this.tempModelMatrix.setIdentity();

		/* https://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/index.htm */
		float xx = quat.x * quat.x;
		float xy = quat.x * quat.y;
		float xz = quat.x * quat.z;
		float xw = quat.x * quat.w;
		float yy = quat.y * quat.y;
		float yz = quat.y * quat.z;
		float yw = quat.y * quat.w;
		float zz = quat.z * quat.z;
		float zw = quat.z * quat.w;

		this.tempArray[0] = 1.0F - 2.0F * (yy + zz);
		this.tempArray[1] = 2.0F * (xy + zw);
		this.tempArray[2] = 2.0F * (xz - yw);
		this.tempArray[3] = 0.0F;

		this.tempArray[4] = 2.0F * (xy - zw);
		this.tempArray[5] = 1.0F - 2.0F * (xx + zz);
		this.tempArray[6] = 2.0F * (yz + xw);
		this.tempArray[7] = 0.0F;

		this.tempArray[8] = 2.0F * (xz + yw);
		this.tempArray[9] = 2.0F * (yz - xw);
		this.tempArray[10] = 1.0F - 2.0F * (xx + yy);
		this.tempArray[11] = 0.0F;

		this.tempArray[12] = 0.0F;
		this.tempArray[13] = 0.0F;
		this.tempArray[14] = 0.0F;
		this.tempArray[15] = 1.0F;

		this.tempModelMatrix.set(this.tempArray);

		this.tempArray[0] = 1.0F - 2.0F * (yy + zz);
		this.tempArray[1] = 2.0F * (xy + zw);
		this.tempArray[2] = 2.0F * (xz - yw);

		this.tempArray[3] = 2.0F * (xy - zw);
		this.tempArray[4] = 1.0F - 2.0F * (xx + zz);
		this.tempArray[5] = 2.0F * (yz + xw);

		this.tempArray[6] = 2.0F * (xz + yw);
		this.tempArray[7] = 2.0F * (yz - xw);
		this.tempArray[8] = 1.0F - 2.0F * (xx + yy);

		this.tempNormalMatrix.set(this.tempArray);

		this.model.peek().mul(this.tempModelMatrix);
		this.normal.peek().mul(this.tempNormalMatrix);
	}
}
