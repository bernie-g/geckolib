package software.bernie.geckolib.util.helper;

/// Duck-interface for GeckoLib on LevelRenderer, mostly to capture world-render state
public interface LevelRendererGeckoLibAccess {
	/// @return `true` when the level is currently in a render pass
	boolean geckolib$isRenderingInWorld();
}
