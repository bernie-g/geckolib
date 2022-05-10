package software.bernie.geckolib3.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UvFaces {
	private FaceUv down;
	private FaceUv east;
	private FaceUv north;
	private FaceUv south;
	private FaceUv up;
	private FaceUv west;

	/**
	 * Specifies the UV's for the face that stretches along the x and z axes, and
	 * faces the -y axis
	 */
	@JsonProperty("down")
	public FaceUv getDown() {
		return down;
	}

	@JsonProperty("down")
	public void setDown(FaceUv value) {
		this.down = value;
	}

	/**
	 * Specifies the UV's for the face that stretches along the z and y axes, and
	 * faces the x axis
	 */
	@JsonProperty("east")
	public FaceUv getEast() {
		return east;
	}

	@JsonProperty("east")
	public void setEast(FaceUv value) {
		this.east = value;
	}

	/**
	 * Specifies the UV's for the face that stretches along the x and y axes, and
	 * faces the -z axis.
	 */
	@JsonProperty("north")
	public FaceUv getNorth() {
		return north;
	}

	@JsonProperty("north")
	public void setNorth(FaceUv value) {
		this.north = value;
	}

	/**
	 * Specifies the UV's for the face that stretches along the x and y axes, and
	 * faces the z axis
	 */
	@JsonProperty("south")
	public FaceUv getSouth() {
		return south;
	}

	@JsonProperty("south")
	public void setSouth(FaceUv value) {
		this.south = value;
	}

	/**
	 * Specifies the UV's for the face that stretches along the x and z axes, and
	 * faces the y axis
	 */
	@JsonProperty("up")
	public FaceUv getUp() {
		return up;
	}

	@JsonProperty("up")
	public void setUp(FaceUv value) {
		this.up = value;
	}

	/**
	 * Specifies the UV's for the face that stretches along the z and y axes, and
	 * faces the -x axis
	 */
	@JsonProperty("west")
	public FaceUv getWest() {
		return west;
	}

	@JsonProperty("west")
	public void setWest(FaceUv value) {
		this.west = value;
	}
}
