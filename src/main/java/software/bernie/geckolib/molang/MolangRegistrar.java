package software.bernie.geckolib.molang;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;

public class MolangRegistrar {
    public static void registerVars(MolangParser parser) {
        parser.register(new Variable("query.anim_time", 0));
    }
}
