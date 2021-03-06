package uk.org.fyodor.random;

final class FailedWithSeedException extends RuntimeException {

    private final long seed;

    FailedWithSeedException(final long seed) {
        super("Test failed with seed " + seed);
        this.seed = seed;
    }

    long seed() {
        return seed;
    }
}
