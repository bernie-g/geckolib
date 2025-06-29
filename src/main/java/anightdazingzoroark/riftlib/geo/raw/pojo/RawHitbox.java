package anightdazingzoroark.riftlib.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawHitbox {
    private String locator;
    private float width;
    private float height;
    private float damageMultiplier;
    private boolean affectedByAnim;
    private RawHitboxDamageDefinitions[] rawHitboxDamageDefinitions;
    private RawHitboxAnimations[] rawHitboxAnimations;

    //this is the locator on the model the hitbox will be associated with
    @JsonProperty("locator")
    public String getLocator() {
        return this.locator;
    }

    @JsonProperty("locator")
    public void setLocator(String value) {
        this.locator = value;
    }

    //hitbox width
    @JsonProperty("width")
    public float getWidth() {
        return this.width;
    }

     @JsonProperty("width")
     public void setWidth(float value) {
        this.width = value;
     }

     //hitbox height
    @JsonProperty("height")
    public float getHeight() {
        return this.height;
    }

    @JsonProperty("height")
    public void  setHeight(float value) {
        this.height = value;
    }

    //when the entity is hurt in this hitbox, the damage it takes is multiplied by this amount
    @JsonProperty("damageMultiplier")
    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    @JsonProperty("damageMultiplier")
    public void setDamageMultiplier(float value) {
        this.damageMultiplier = value;
    }

    //whether or not the hitbox can move with an animation
    @JsonProperty("affectedByAnim")
    public boolean getAffectedByAnim() {
        return this.affectedByAnim;
    }

    @JsonProperty("affectedByAnim")
    public void setAffectedByAnim(boolean value) {
        this.affectedByAnim = value;
    }

    @JsonProperty("damageDefinitions")
    public RawHitboxDamageDefinitions[] getRawHitboxDamageDefinitions() {
        return this.rawHitboxDamageDefinitions;
    }

    @JsonProperty("damageDefinitions")
    public void setRawHitboxDamageDefinitions(RawHitboxDamageDefinitions[] value) {
        this.rawHitboxDamageDefinitions = value;
    }

    //hitbox properties can also be affected by animations
    //this is where they're put in
    @JsonProperty("animations")
    public RawHitboxAnimations[] getRawHitboxAnimations() {
        return this.rawHitboxAnimations;
    }

    @JsonProperty("animations")
    public void setRawHitboxAnimations(RawHitboxAnimations[] value) {
        this.rawHitboxAnimations = value;
    }
}
