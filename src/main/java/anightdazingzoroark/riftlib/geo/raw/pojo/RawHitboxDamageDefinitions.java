package anightdazingzoroark.riftlib.geo.raw.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RawHitboxDamageDefinitions {
    private String damageSource;
    private String damageType;
    private float damageMultiplier;

    /***
    damage source is a DamageSource instance, damage type is an attribute attached to a damage source
     such as whether or not its a projectile or its from fire or whatever
     both cannot be in the same RawHitboxDamageDefinition
     if they somehow are, damageSource will override damageType
    */

    @JsonProperty("damageSource")
    public String getDamageSource() {
        return this.damageSource;
    }

    @JsonProperty("damageSource")
    public void setDamageSource(String value) {
        this.damageSource = value;
    }

    @JsonProperty("damageType")
    public String getDamageType() {
        return this.damageType;
    }

    @JsonProperty("damageType")
    public void setDamageType(String value) {
        this.damageType = value;
    }

    @JsonProperty("damageMultiplier")
    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }

    @JsonProperty("damageMultiplier")
    public void setDamageMultiplier(float value) {
        this.damageMultiplier = value;
    }
}
