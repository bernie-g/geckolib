package anightdazingzoroark.riftlib.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawHitboxDefinitions {
    private RawHitbox[] rawHitboxes;

    @JsonProperty("hitboxes")
    public RawHitbox[] getRawHitboxes() {
        return this.rawHitboxes;
    }

    @JsonProperty("hitboxes")
    public void setRawHitboxes(RawHitbox[] value) {
        this.rawHitboxes = value;
    }
}
