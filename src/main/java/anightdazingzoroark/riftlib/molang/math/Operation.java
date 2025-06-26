package anightdazingzoroark.riftlib.molang.math;

import java.util.HashSet;
import java.util.Set;

public enum Operation {
    ADD("+", 1) {
        public double calculate(double a, double b) {
            return a + b;
        }
    },
    SUB("-", 1) {
        public double calculate(double a, double b) {
            return a - b;
        }
    },
    MUL("*", 2) {
        public double calculate(double a, double b) {
            return a * b;
        }
    },
    DIV("/", 2) {
        public double calculate(double a, double b) {
            return a / (b == (double)0.0F ? (double)1.0F : b);
        }
    },
    MOD("%", 2) {
        public double calculate(double a, double b) {
            return a % b;
        }
    },
    POW("^", 3) {
        public double calculate(double a, double b) {
            return Math.pow(a, b);
        }
    },
    AND("&&", 5) {
        public double calculate(double a, double b) {
            return a != (double)0.0F && b != (double)0.0F ? (double)1.0F : (double)0.0F;
        }
    },
    OR("||", 5) {
        public double calculate(double a, double b) {
            return a == (double)0.0F && b == (double)0.0F ? (double)0.0F : (double)1.0F;
        }
    },
    LESS("<", 5) {
        public double calculate(double a, double b) {
            return a < b ? (double)1.0F : (double)0.0F;
        }
    },
    LESS_THAN("<=", 5) {
        public double calculate(double a, double b) {
            return a <= b ? (double)1.0F : (double)0.0F;
        }
    },
    GREATER_THAN(">=", 5) {
        public double calculate(double a, double b) {
            return a >= b ? (double)1.0F : (double)0.0F;
        }
    },
    GREATER(">", 5) {
        public double calculate(double a, double b) {
            return a > b ? (double)1.0F : (double)0.0F;
        }
    },
    EQUALS("==", 5) {
        public double calculate(double a, double b) {
            return equals(a, b) ? (double)1.0F : (double)0.0F;
        }
    },
    NOT_EQUALS("!=", 5) {
        public double calculate(double a, double b) {
            return !equals(a, b) ? (double)1.0F : (double)0.0F;
        }
    };

    public static final Set<String> OPERATORS = new HashSet();
    public final String sign;
    public final int value;

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < 1.0E-5;
    }

    private Operation(String sign, int value) {
        this.sign = sign;
        this.value = value;
    }

    public abstract double calculate(double var1, double var3);

    static {
        for(Operation op : values()) {
            OPERATORS.add(op.sign);
        }

    }
}
