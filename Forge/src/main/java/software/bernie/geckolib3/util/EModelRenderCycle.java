package software.bernie.geckolib3.util;

/**
 * @author DerToaster98 Copyright (c) 30.03.2022 Developed by DerToaster98
 *         GitHub: https://github.com/DerToaster98
 */
public enum EModelRenderCycle implements IRenderCycle {
	//Initial rendering of the model in this current frame, ExtendedGeoEntityRenderer renders armor, heads and items during this phase
	INITIAL, 
	//The model got re-rendered by a layer renderer or soemthing else during this frame
	REPEATED,
	//For special use by the user, unused by default
	SPECIAL
}