package anightdazingzoroark.riftlib.molang.math;

public class Ternary implements IValue {
    public IValue condition;
    public IValue ifTrue;
    public IValue ifFalse;

    public Ternary(IValue condition, IValue ifTrue, IValue ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public double get() {
        return this.condition.get() != (double)0.0F ? this.ifTrue.get() : this.ifFalse.get();
    }

    public String toString() {
        return this.condition.toString() + " ? " + this.ifTrue.toString() + " : " + this.ifFalse.toString();
    }
}

