import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnimationUtil {

    // ANSI color codes
    static final String RESET   = "\033[0m";
    static final String YELLOW  = "\033[33m"; // comparing
    static final String RED     = "\033[31m"; // swapping
    static final String CYAN    = "\033[36m"; // shifting (insertion sort)
    static final String MAGENTA = "\033[35m"; // pivot (quicksort)
    static final String GREEN   = "\033[32m"; // sorted
    static final String DIM     = "\033[2m";  // out of range / inactive

    /**
     * The visual role a single box plays in a given frame.
     * This is pure vocabulary -- AnimationUtil.render() only knows how to
     * color/draw each state, it has no idea what "comparing" or "pivot"
     * means algorithmically. That meaning is decided entirely by the
     * per-algorithm render wrappers that build the BoxSpec[] array.
     */
    public enum CellState {
        NORMAL,
        COMPARING,
        SWAPPING,
        SHIFTING,
        PIVOT,
        SORTED,
        OUT_OF_RANGE
    }

    /**
     * Everything needed to draw ONE box for one frame.
     * Built fresh per render call by the algorithm-specific wrapper --
     * never shared/mutated across frames.
     */
    public static class BoxSpec {
        public CellState state = CellState.NORMAL;
        public boolean lift = false;      // draw the "^" marker above this box
        public boolean gapAfter = false;  // extra whitespace after this box (visual split/grouping)

        public BoxSpec() {}

        public BoxSpec(CellState state) {
            this.state = state;
        }

        public BoxSpec(CellState state, boolean lift) {
            this.state = state;
            this.lift = lift;
        }
    }

    /**
     * Convenience: builds a BoxSpec[] where every box is NORMAL, not lifted,
     * no gaps -- i.e. one connected chain of boxes with the array's raw values.
     * Every algorithm's very first frame should be this: the untouched array.
     */
    public static <T> BoxSpec[] freshSpecs(T[] arr) {
        BoxSpec[] specs = new BoxSpec[arr.length];
        for (int i = 0; i < specs.length; i++) {
            specs[i] = new BoxSpec();
        }
        return specs;
    }

    /**
     * Clears the screen and redraws the array as a row of boxes.
     * Purely mechanical: colors/lifts/gaps each box exactly according to
     * the BoxSpec given for it, and prints the message as-is. Carries no
     * knowledge of what any algorithm is doing.
     *
     * @param arr       the array to draw
     * @param specs     per-index drawing instructions, must be same length as arr
     * @param message   status line to print beneath the array, verbatim
     * @param delayMs   number of milliseconds to delay frame rendering
     */
    public static <T> void render(T[] arr, BoxSpec[] specs, String message, int delayMs) {
        clearScreen();

        int width = 1;
        for (T v : arr) {
            width = Math.max(width, String.valueOf(v).length()); // tracks largest width across the array
        }
        width += 2; // adds left and right padding

        // Box Components
        StringBuilder lift = new StringBuilder();
        StringBuilder top  = new StringBuilder();
        StringBuilder mid  = new StringBuilder();
        StringBuilder bot  = new StringBuilder();
        StringBuilder idx  = new StringBuilder();

        int n = arr.length;

        for (int i = 0; i < n; i++) {
            BoxSpec spec = specs[i];

            String color = colorFor(spec.state);

            // spacer after this box: normal single space, or a wider gap if requested
            String spacer = spec.gapAfter ? "   " : " ";

            // lift row: ^ centered above the box, same width as top/bot
            String liftStr = spec.lift ? "^" : "";
            int liftPad = (width + 2) - liftStr.length();
            int liftLeft = liftPad / 2;
            int liftRight = liftPad - liftLeft;
            lift.append(" ".repeat(liftLeft)).append(liftStr).append(" ".repeat(liftRight)).append(spacer);

            // top border
            top.append(color).append("+").append("-".repeat(width)).append("+").append(RESET).append(spacer);

            // middle row: the number, centered
            String valStr = String.valueOf(arr[i]);
            int totalPad = width - valStr.length();
            int padLeft = totalPad / 2;
            int padRight = totalPad - padLeft;
            mid.append(color).append("|")
               .append(" ".repeat(padLeft)).append(valStr).append(" ".repeat(padRight))
               .append("|").append(RESET).append(spacer);

            // bottom border
            bot.append(color).append("+").append("-".repeat(width)).append("+").append(RESET).append(spacer);

            // index row, centered same way under the box
            String idxStr = String.valueOf(i);
            int idxPad = (width + 2) - idxStr.length();
            int idxLeft = idxPad / 2;
            int idxRight = idxPad - idxLeft;
            idx.append(" ".repeat(idxLeft)).append(idxStr).append(" ".repeat(idxRight)).append(spacer);
        }

        System.out.println(lift);
        System.out.println(top);
        System.out.println(mid);
        System.out.println(bot);
        System.out.println(idx);
        System.out.println(message);

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Helper for colors
    private static String colorFor(CellState state) {
        return switch (state) {
            case COMPARING    -> YELLOW;
            case SWAPPING     -> RED;
            case SHIFTING     -> CYAN;
            case PIVOT        -> MAGENTA;
            case SORTED       -> GREEN;
            case OUT_OF_RANGE -> DIM;
            case NORMAL       -> "";
        };
    }

    // Helper for step logger
    public static class StepLogger {
        private static final List<String> logs = new ArrayList<>();

        public static void log(String line) {
            logs.add(line);
        }

        public static void printSummary() {
            System.out.println("\n=== Algorithm Steps ===");
            for (String line : logs) {
                System.out.println(line);
            }
        }

        public static void clear() {
            logs.clear();
        }
    }



    //Helper method to clear screen
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // fallback: print a bunch of blank lines
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

    

}
 
