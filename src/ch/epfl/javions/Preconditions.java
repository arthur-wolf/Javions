package ch.epfl.javions;

/**
 * Method to check preconditions in other methods
 *
 * @author Arthur Wolf (344200)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Check if a given argument is true or false
     *
     * @param shouldBeTrue the argument to check
     * @throws IllegalArgumentException if the argument is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
