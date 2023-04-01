package ch.epfl.javions;

/**
 * Methods and attributes to define conversions from one unit to another and express various quantities
 *
 * @author Arthur Wolf (344200)
 * @author Oussama Ghali (341478)
 */
public final class Units {

    private Units() {}
    private static final int BASE_UNIT = 1;
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    public static class Angle {

        private Angle() {}

        public static final double RADIAN = BASE_UNIT;
        public static final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = Math.scalb(TURN, -32);
    }

    public static class Length {

        private Length() {}

        public static final double METER = BASE_UNIT;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852 * METER;

    }

    public static class Time {
        private Time() {}

        public static final double SECOND = BASE_UNIT;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;

    }

    public static class Speed {
        private Speed() {}

        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;

    }

    /**
     * Converts a value from one unit to another
     *
     * @param value    the given value that we want to convert
     * @param fromUnit the initial unit in which the value is expressed
     * @param toUnit   the arrival unit in which the value will be expressed
     * @return the given value in the unit "toUnit"
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }

    /**
     * Converts a value from a given unit to the base unit
     *
     * @param value    the given value that we want to convert
     * @param fromUnit the initial unit in which the value is expressed
     * @return the given value in the base unit
     */
    public static double convertFrom(double value, double fromUnit) {
        return convert(value, fromUnit, BASE_UNIT);
    }

    /**
     * Converts a value from the base unit to a given unit
     *
     * @param value  the given value that we want to convert
     * @param toUnit the arrival unit in which the value will be expressed
     * @return the given value in the unit "toUnit"
     */
    public static double convertTo(double value, double toUnit) {
        return convert(value, BASE_UNIT, toUnit);
    }
}
