package anightdazingzoroark.riftlib.ridePositionLogic;

public class DynamicRidePosUtils {
    public static boolean locatorCanBeRidePos(String locatorName) {
        return locatorName.length() >= 10 && locatorName.substring(0, 10).equals("rider_pos_");
    }

    public static int locatorRideIndex(String locatorName) {
        if (locatorName.length() < 10 || !locatorName.substring(0, 10).equals("rider_pos_")) return -1;
        return Integer.parseInt(locatorName.substring(10));
    }
}
