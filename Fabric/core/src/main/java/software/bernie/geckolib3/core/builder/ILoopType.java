package software.bernie.geckolib3.core.builder;

public interface ILoopType {

	boolean isRepeatingAfterEnd();
	
	enum EDefaultLoopTypes implements ILoopType {
		LOOP(true),
		PLAY_ONCE,
		HOLD_ON_LAST_FRAME;
		
		private final boolean looping;
		
		EDefaultLoopTypes(boolean looping) {
			this.looping = looping;
		}
		
		EDefaultLoopTypes() {
			this(false);
		}

		@Override
		public boolean isRepeatingAfterEnd() {
			return this.looping;
		}
	}
	
}
