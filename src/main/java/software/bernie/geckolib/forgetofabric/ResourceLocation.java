package software.bernie.geckolib.forgetofabric;

import net.minecraft.util.Identifier;

public class ResourceLocation extends Identifier
{
	public ResourceLocation(String id) {
		super(split(id, ':'));
	}

	public ResourceLocation(String namespace, String path) {
		super(new String[]{namespace, path});
	}
}
