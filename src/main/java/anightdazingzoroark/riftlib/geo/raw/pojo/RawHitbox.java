package anightdazingzoroark.riftlib.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawHitbox {
    private String locator;
    private float width;
    private float height;
    private boolean affectedByAnim;

    @JsonProperty("locator")
    public String getLocator() {
        return this.locator;
    }

    @JsonProperty("locator")
    public void setLocator(String value) {
        this.locator = value;
    }

    @JsonProperty("width")
    public float getWidth() {
        return this.width;
    }

     @JsonProperty("width")
     public void setWidth(float value) {
        this.width = value;
     }

    @JsonProperty("height")
    public float getHeight() {
        return this.height;
    }

    @JsonProperty("height")
    public void  setHeight(float value) {
        this.height = value;
    }

    @JsonProperty("affectedByAnim")
    public boolean getAffectedByAnim() {
        return this.affectedByAnim;
    }

    @JsonProperty("affectedByAnim")
    public void setAffectedByAnim(boolean value) {
        this.affectedByAnim = value;
    }
}
