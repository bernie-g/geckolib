package anightdazingzoroark.riftlib.file;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HitboxDefinitionList {
    public final List<HitboxDefinition> list = new ArrayList<>();

    public HitboxDefinition getHitboxDefinitionByName(String name) {
        List<HitboxDefinition> filteredList = this.list.stream().filter(h -> h.locator.equals(name))
                .collect(Collectors.toList());
        if (!filteredList.isEmpty()) return filteredList.get(0);
        return null;
    }

    public static class HitboxDefinition {
        public final String locator;
        public final float width;
        public final float height;
        public final boolean affectedByAnim;

        public HitboxDefinition(String locator, float width, float height, boolean affectedByAnim) {
            this.locator = locator;
            this.width = width;
            this.height = height;
            this.affectedByAnim = affectedByAnim;
        }

        //its here just to make debugging easier
        @Override
        public String toString() {
            return "[locator="+this.locator+", size=("+this.width+", "+this.height+"), affectedByAnim="+this.affectedByAnim+"]";
        }
    }
}
