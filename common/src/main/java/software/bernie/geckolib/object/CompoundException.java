package software.bernie.geckolib.object;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.StringJoiner;

/**
 * Nestable {@link Exception} that allows for a stacktrace to additively append context to a singular exception without needing to re-build the stack trace for each wrapper.
 * <p>
 * This allows for faster stacked exceptions without having errors land sporadically throughout the log as tasks are asynchronously completed.
 */
public class CompoundException extends RuntimeException {
    private final List<String> messages = new ObjectArrayList<>();

    public CompoundException(String message) {
        this.messages.add(message);
    }

    /**
     * Add a message to the stack.
     * The message will be given its own line in the log, displayed <u>before</u> any lines already added
     *
     * @param message The message to add
     * @return this
     */
    public CompoundException withMessage(String message) {
        this.messages.add(message);

        return this;
    }

    @Override
    public String getLocalizedMessage() {
        final StringJoiner joiner = new StringJoiner("\n");
        final int count = this.messages.size() - 1;

        for (int i = count; i >= 0; i--) {
            joiner.add((i == count ? "" : "\t".repeat(Math.max(0, count - i)) + "-> ") + this.messages.get(i));
        }

        return joiner.toString();
    }

    @Override
    public String toString() {
        final String name = "Geckolib.CompoundException";
        final String message = getLocalizedMessage();

        return message != null ? name + ": " + message : name;
    }
}
