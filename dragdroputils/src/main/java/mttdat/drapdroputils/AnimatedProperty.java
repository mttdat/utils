package mttdat.drapdroputils;

public class AnimatedProperty {

    AnimatedViewUtils.Property property;
    float originalValue;
    float targetValue;
    boolean isInverse;

    float min, max, range;

    float maxRange = -1;

    public AnimatedProperty(AnimatedViewUtils.Property property, float originalValue, float targetValue) {
        this.property = property;
        this.originalValue = originalValue;
        this.targetValue = targetValue;

        init();
    }

    //*  isDirectionTouchNegative: Direction touch moving is positive or negative.
    public AnimatedProperty(AnimatedViewUtils.Property property, float originalValue, float targetValue, boolean isInverse) {
        this.property = property;
        this.originalValue = originalValue;
        this.targetValue = targetValue;
        this.isInverse = isInverse;

        init();
    }

    private void init() {
        min = Math.min(originalValue, targetValue);
        max = Math.max(originalValue, targetValue);
        range = Math.abs(originalValue - targetValue);
    }

    public AnimatedProperty setInverse(boolean isInverse){
        this.isInverse = isInverse;
        return this;
    }

    public AnimatedProperty setMaxRange(float maxRange){
        this.maxRange = maxRange;
        return this;
    }
}
