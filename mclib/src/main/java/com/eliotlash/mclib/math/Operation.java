package com.eliotlash.mclib.math;

import java.util.HashSet;
import java.util.Set;

/**
 * Operation enumeration
 *
 * This enumeration provides different hardcoded enumerations of default
 * math operators such addition, substraction, multiplication, division,
 * modulo and power.
 *
 * TODO: maybe convert to classes (for the sake of API)?
 */
public enum Operation
{
    ADD("+", 1)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a + b;
        }
    },
    SUB("-", 1)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a - b;
        }
    },
    MUL("*", 2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a * b;
        }
    },
    DIV("/", 2)
    {
        @Override
        public double calculate(double a, double b)
        {
            /* To avoid any exceptions */
            return a / (b == 0 ? 1 : b);
        }
    },
    MOD("%", 2)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a % b;
        }
    },
    POW("^", 3)
    {
        @Override
        public double calculate(double a, double b)
        {
            return Math.pow(a, b);
        }
    },
    AND("&&", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a != 0 && b != 0 ? 1 : 0;
        }
    },
    OR("||", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a != 0 || b != 0 ? 1 : 0;
        }
    },
    LESS("<", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a < b ? 1 : 0;
        }
    },
    LESS_THAN("<=", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a <= b ? 1 : 0;
        }
    },
    GREATER_THAN(">=", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a >= b ? 1 : 0;
        }
    },
    GREATER(">", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return a > b ? 1 : 0;
        }
    },
    EQUALS("==", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return equals(a, b) ? 1 : 0;
        }
    },
    NOT_EQUALS("!=", 5)
    {
        @Override
        public double calculate(double a, double b)
        {
            return !equals(a, b) ? 1 : 0;
        }
    };

    public final static Set<String> OPERATORS = new HashSet<String>();

    public static boolean equals(double a, double b)
    {
        return Math.abs(a - b) < 0.00001;
    }

    static
    {
        for (Operation op : values())
        {
            OPERATORS.add(op.sign);
        }
    }

    /**
     * String-ified name of this operation
     */
    public final String sign;

    /**
     * Value of this operation in relation to other operations (i.e
     * precedence importance)
     */
    public final int value;

    private Operation(String sign, int value)
    {
        this.sign = sign;
        this.value = value;
    }

    /**
     * Calculate the value based on given two doubles
     */
    public abstract double calculate(double a, double b);
}
