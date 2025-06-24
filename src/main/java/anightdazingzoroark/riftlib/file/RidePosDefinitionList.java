package anightdazingzoroark.riftlib.file;

import net.minecraft.util.math.Vec3d;

import java.util.*;

public class RidePosDefinitionList {
    public final Map<Integer, Vec3d> map = new HashMap<>();

    private int[] getOrderedIndexes() {
        int[] toReturn = new int[this.map.size()];
        Object[] keys = this.map.keySet().toArray();

        //get integers first
        for (int x = 0; x < this.map.size(); x++) {
            toReturn[x] = (int) keys[x];
        }

        //now sort the numbers
        for (int x = 0; x < toReturn.length; x++) {
            for (int y = x; y < toReturn.length; y++) {
                if (toReturn[y] < toReturn[x]) {
                    int temp = toReturn[x];
                    toReturn[x] = toReturn[y];
                    toReturn[y] = temp;
                }
            }
        }

        return toReturn;
    }

    public List<Vec3d> finalOrderedRiderPositions() {
        List<Vec3d> toReturn = new ArrayList<>();

        for (int x = 0; x < this.getOrderedIndexes().length; x++) {
            toReturn.add(map.get(this.getOrderedIndexes()[x]));
        }

        return toReturn;
    }
}
