package software.bernie.mclib.math.utils;

public class Timer {
	public boolean enabled;
	public long time;
	public long duration;

	public Timer(long duration) {
		this.duration = duration;
	}

	public long getRemaining() {
		return this.time - System.currentTimeMillis();
	}

	public void mark() {
		mark(this.duration);
	}

	public void mark(long duration) {
		this.enabled = true;
		this.time = System.currentTimeMillis() + duration;
	}

	public void reset() {
		this.enabled = false;
	}

	public boolean checkReset() {
		final boolean enabled = check();

		if (enabled)
			reset();

		return enabled;
	}

	public boolean check() {
		return this.enabled && isTime();
	}

	public boolean isTime() {
		return System.currentTimeMillis() >= this.time;
	}

	public boolean checkRepeat() {
		if (!this.enabled)
			mark();

		return checkReset();
	}
}
