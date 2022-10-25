package software.bernie.geckolib3.core.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

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

	static ILoopType fromJson(JsonElement json) {
		if (json == null || !json.isJsonPrimitive()) {
			return EDefaultLoopTypes.PLAY_ONCE;
		}

		JsonPrimitive primitive = json.getAsJsonPrimitive();

		if (primitive.isBoolean()) {
			return primitive.getAsBoolean() ? EDefaultLoopTypes.LOOP : EDefaultLoopTypes.PLAY_ONCE;
		}

		if (primitive.isString()) {
			String string = primitive.getAsString();

			if (string.equalsIgnoreCase("false")) {
				return EDefaultLoopTypes.PLAY_ONCE;
			}

			if (string.equalsIgnoreCase("true")) {
				return EDefaultLoopTypes.LOOP;
			}

			try {
				return EDefaultLoopTypes.valueOf(string.toUpperCase(Locale.ROOT));
			}
			catch (Exception ex) {}
		}

		return EDefaultLoopTypes.PLAY_ONCE;
	}
}
