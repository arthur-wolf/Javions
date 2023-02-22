package ch.epfl.javions;

public final class Math2 {
    private Math2() {}

    /**
     * limits the value v to the interval defined by min and max
     * @param min the minimum of the interval
     * @param v the value to limit
     * @param max the maximum of the interval
     * @return min if v is smaller than min, max if v is bigger than max, and v otherwise
     * @throws IllegalArgumentException if the minimum is bigger than the maximum
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        if (v < min) {
            return min;
        } else if (v > max) {
            return max;
        }
        return v;
    }

    /**
     * computes the value of arsinh(x)
     * @param x the parameter of the function
     * @return the value of arsinh(x)
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }
}
