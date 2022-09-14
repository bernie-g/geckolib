package software.bernie.geckolib3q.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

public class VectorUtils {
	public static Vector3d fromArray(double[] array) {
		Validate.validIndex(ArrayUtils.toObject(array), 2);
		return new Vector3d(array[0], array[1], array[2]);
	}

	public static Vector3f fromArray(float[] array) {
		Validate.validIndex(ArrayUtils.toObject(array), 2);
		return new Vector3f(array[0], array[1], array[2]);
	}

	public static Vector3f convertDoubleToFloat(Vector3d vector) {
		return new Vector3f((float) vector.x, (float) vector.y, (float) vector.z);
	}

	public static Vector3d convertFloatToDouble(Vector3f vector) {
		return new Vector3d(vector.x(), vector.y(), vector.z());
	}
}
