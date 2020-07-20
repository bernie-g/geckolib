package software.bernie.geckolib.easing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EasingManagerTest {
    static class BounceTestArg {
        double k;
        double t;
        double expectedResult;

        public BounceTestArg(double k, double t, double expectedResult) {
            this.k = k;
            this.t = t;
            this.expectedResult = expectedResult;
        }

        @Override
        public String toString() {
            return "BounceTestArg{" +
                    "k=" + k +
                    ", t=" + t +
                    ", expectedResult=" + expectedResult +
                    '}';
        }
    }

    static Stream<BounceTestArg> bounceTestProvider() {
        return Stream.of(
            new BounceTestArg(0.5, 0, 0),
            new BounceTestArg(0.5, 0.25,  0.47265625),
            new BounceTestArg(0.5, 0.5,  0.53125),
            new BounceTestArg(0.5, 0.705,  0.88500313),
            new BounceTestArg(0.5, 0.729,  0.99059025),
            new BounceTestArg(0.5, 0.91,  0.99505),
            new BounceTestArg(0.5, 1, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("bounceTestProvider")
    void testBounce(BounceTestArg args) {
        double result = EasingManager.bounce(args.k).apply(args.t);
        assertEquals(args.expectedResult, result, 0.000001);
    }
}
