package software.bernie.geckolib3q.network;

public interface ISyncable {
	void onAnimationSync(int id, int state);

	default String getSyncKey() {
		return this.getClass().getName();
	}
}