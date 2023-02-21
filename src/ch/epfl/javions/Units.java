package ch.epfl.javions;

public final class Units {

    private Units(){}
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    public static class Angle {

        private Angle (){}
        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI;
        public static final double DEGREE = TURN/360;
        public static final double T32 = Math.scalb(TURN,-32);
    }
    public static class Length{

        private Length (){}
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = CENTI * CENTIMETER;
        public static final double INCH = 2.54;
        public static final double FOOT = 12;
        public static final double NAUTICAL_MILE = 1852;

    }
    public static class Time {
        private Time (){}
        public static final double SECOND = 1;
        public static final double MINUTE = 60;
        public static final double HOUR = 60;

    }
    public static class Speed {
        private Speed (){}
        public static final double KNOT = 1;
        public static final double KILOMETER_PER_HOUR = 1;

    }
    public static double convert(double value, double fromUnit, double toUnit){
        return value * (fromUnit/toUnit);
    }
    public static double convertFrom(double value, double fromUnit){
        return value * (fromUnit);
    }
    public static double convertTo(double value, double toUnit){
        return value * (toUnit);
    }
}
