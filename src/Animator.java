import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Animator -- one instance per sort run, and the only thing an algorithm needs
 * in order to narrate itself.
 *
 * It owns the state that has to survive across frames (which cells are finished,
 * where the visual gaps are, which slice of the array is active, which cell is
 * the currently held pivot/key) so the algorithms no longer thread a delayMs /
 * sortedMask / gapMask trio through every recursive signature. A single Animator
 * reference replaces all three.
 *
 * Algorithms only ever call the verbs below -- compare, swap, shift, place,
 * settle. They never build a frame, never name a colour, and never see a
 * BoxSpec. Every verb both draws one frame and records one line in the step log,
 * so the log stays in sync with what was on screen for free.
 *
 * The drawing half at the bottom of this file is deliberately ignorant: it knows
 * how to colour a CellState and how to centre text in a box, and nothing at all
 * about what "pivot" or "comparing" mean algorithmically.
 */
public final class Animator<T> {

    // ---PALETTE---

    private static final String CLEAR   = "\033[H\033[2J"; // home the cursor, wipe the screen
    private static final String RESET   = "\033[0m";
    private static final String YELLOW  = "\033[33m"; // comparing
    private static final String RED     = "\033[31m"; // swapping / writing
    private static final String CYAN    = "\033[36m"; // shifting
    private static final String MAGENTA = "\033[35m"; // pivot / held key
    private static final String GREEN   = "\033[32m"; // finished
    private static final String DIM     = "\033[2m";  // outside the active window

    /** The visual role a single box plays in one frame. Private: it is the
     *  drawing layer's private vocabulary, not something algorithms speak. */
    private enum CellState {
        NORMAL, COMPARING, SWAPPING, SHIFTING, PIVOT, SORTED, OUT_OF_RANGE
    }

    /** Everything needed to draw ONE box for one frame. Built fresh per frame. */
    private static final class BoxSpec {
        CellState state = CellState.NORMAL;
        boolean lift;      // draw a "^" marker above this box
        boolean gapAfter;  // widen the whitespace after this box
    }

    // ---STATE---

    private final T[] arr;
    private final int delayMs;
    private final boolean drawing;       // false -> record the log, touch nothing else
    private final boolean[] sorted;      // sorted[i] -> i is drawn green from now on
    private final boolean[] gap;         // gap[i]    -> a split is still open after i
    private final List<String> steps = new ArrayList<>();

    private int low;                     // active window, inclusive
    private int high;
    private int pivot = -1;              // represents pivot inside the active window, -1 = none currently held

    public Animator(T[] arr, int delayMs) {
        this.arr = arr;
        this.delayMs = delayMs;
        this.drawing = delayMs > 0; // if delayMs is 0, we are in silent mode and don't draw anything
        this.sorted = new boolean[arr.length]; // all false at first, then true for each index that is finished
        this.gap = new boolean[arr.length]; // all false at first, then true for each index that has a split after it
        this.low = 0;
        this.high = arr.length - 1;
    }

    /** An animator that still records the step log but never touches the screen. */
    public static <T> Animator<T> silent(T[] arr) {
        return new Animator<>(arr, 0);
    }

    // ---ACTIVE WINDOW---

    /**
     * Narrows the active window for the duration of a try-with-resources block,
     * then puts back whatever the caller had. Recursive algorithms open one per
     * level, so a child's window (and its pivot) can never leak into the frames
     * its parent draws afterwards.
     */
    public Range range(int low, int high) {
        Range scope = new Range(this.low, this.high, this.pivot);
        this.low = low;
        this.high = high;
        this.pivot = -1; // a fresh window starts with no pivot of its own
        return scope;
    }

    public final class Range implements AutoCloseable {
        private final int prevLow, prevHigh, prevPivot;

        private Range(int prevLow, int prevHigh, int prevPivot) {
            this.prevLow = prevLow;
            this.prevHigh = prevHigh;
            this.prevPivot = prevPivot;
        }

        @Override public void close() {
            Animator.this.low = prevLow;
            Animator.this.high = prevHigh;
            Animator.this.pivot = prevPivot;
        }
    }

    // ---STICKY STATE, NO FRAME---

    /** Index i is finished; it stays green in every later frame. */
    public void markSorted(int i) {
        sorted[i] = true;
    }

    /** Everything from 0 through end is finished -- a growing sorted prefix. */
    public void markSortedThrough(int end) {
        Arrays.fill(sorted, 0, end + 1, true);
    }

    // ---VERBS---

    /** The current picture with no cell singled out. */
    public void frame(String message) {
        draw(specs(), message);
    }

    /**
     * Names index i the pivot (quicksort) or held key (insertion sort) for the
     * rest of this window: it is drawn magenta in every following frame until a
     * shift, place or settle retires it, or the window closes.
     */
    public void pivot(int i, String message) {
        pivot = i;
        draw(mark(specs(), i, CellState.PIVOT), message);
    }

    /** Examines index i -- the other operand is the sticky pivot, or the message. */
    public void compare(int i, String message) {
        draw(mark(specs(), i, CellState.COMPARING), message);
    }

    /** Weighs two indices against each other. */
    public void compare(int a, int b, String message) {
        draw(mark(mark(specs(), a, CellState.COMPARING), b, CellState.COMPARING), message);
    }

    /** Two indices trade places. */
    public void swap(int a, int b, String message) {
        draw(mark(mark(specs(), a, CellState.SWAPPING), b, CellState.SWAPPING), message);
    }

    /** A value slides sideways into the open gap. Retires the held key. */
    public void shift(int i, String message) {
        pivot = -1; // whatever was being held has been overwritten at its old slot
        draw(mark(specs(), i, CellState.SHIFTING), message);
    }

    /** A value is written into index i, which is finished from here on. */
    public void place(int i, String message) {
        sorted[i] = true;
        pivot = -1; // the held value has landed
        draw(mark(specs(), i, CellState.SWAPPING), message);
    }

    /** Index i reaches its final position and turns green for good. */
    public void settle(int i, String message) {
        sorted[i] = true;
        pivot = -1; // this window's pivot business is done
        draw(mark(specs(), i, CellState.SORTED), message);
    }

    /** Resets every mask and draws one last all-green picture of the whole array. */
    public void finish(String message) {
        low = 0;
        high = arr.length - 1;
        pivot = -1;
        Arrays.fill(gap, false);
        Arrays.fill(sorted, true);
        frame(message);
    }

    // ---SPLIT & JOIN---

    /** Opens a visual gap after index mid; it stays open until join() heals it. */
    public void split(int mid, String message) {
        gap[mid] = true;
        frame(message);
    }

    /** Heals exactly the gap that the matching split() opened. */
    public void join(int mid, String message) {
        gap[mid] = false;
        frame(message);
    }

    // ---STEP LOG---

    public void printSummary() {
        System.out.println("\n=== Algorithm steps (" + steps.size() + ") ===");
        for (String step : steps) {
            System.out.println(step);
        }
    }

    // ================================ drawing =================================
    // Everything below this line understands boxes and colours, and nothing else.

    /**
     * The frame every verb starts from: dim outside the window, green where
     * finished, magenta on the sticky pivot, and a wide gap at each window edge
     * plus wherever a split is still open. The window edges are gapped so the
     * active slice reads as its own separated block instead of blending into the
     * dimmed boxes on either side.
     */
    private BoxSpec[] specs() {
        BoxSpec[] specs = new BoxSpec[arr.length];
        for (int i = 0; i < arr.length; i++) {
            BoxSpec spec = new BoxSpec();
            if (i < low || i > high) {
                spec.state = CellState.OUT_OF_RANGE;
            } else if (sorted[i]) {
                spec.state = CellState.SORTED;
            }
            spec.gapAfter = (i == low - 1) || (i == high) || gap[i];
            specs[i] = spec;
        }
        if (pivot >= 0) {
            specs[pivot].state = CellState.PIVOT;
        }
        return specs;
    }

    /** Singles out one box: recolours it and raises the "^" marker above it. */
    private static BoxSpec[] mark(BoxSpec[] specs, int i, CellState state) {
        specs[i].state = state;
        specs[i].lift = true;
        return specs;
    }

    /** Records the step, then -- only if animating -- paints it and holds. */
    private void draw(BoxSpec[] specs, String message) {
        steps.add(message);
        if (!drawing) return;

        System.out.print(compose(specs, message)); // one write per frame, so no tearing
        System.out.flush();
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Builds the whole frame -- five rows of box art plus the status line. */
    private String compose(BoxSpec[] specs, String message) {
        //clearScreen();
        int width = 1;
        for (T value : arr) {
            width = Math.max(width, String.valueOf(value).length());
        }
        width += 2;               // one space of padding either side of the value
        int cell = width + 2;     // the box including both of its borders

        StringBuilder lift = new StringBuilder();
        StringBuilder top  = new StringBuilder();
        StringBuilder mid  = new StringBuilder();
        StringBuilder bot  = new StringBuilder();
        StringBuilder idx  = new StringBuilder();
        String border = "+" + "-".repeat(width) + "+";

        for (int i = 0; i < arr.length; i++) {
            BoxSpec spec = specs[i];
            String color = colorFor(spec.state);
            String spacer = spec.gapAfter ? "   " : " ";

            lift.append(center(spec.lift ? "^" : "", cell)).append(spacer);
            top.append(color).append(border).append(RESET).append(spacer);
            mid.append(color).append("|").append(center(String.valueOf(arr[i]), width))
               .append("|").append(RESET).append(spacer);
            bot.append(color).append(border).append(RESET).append(spacer);
            idx.append(center(String.valueOf(i), cell)).append(spacer);
        }

        return CLEAR + lift + "\n" + top + "\n" + mid + "\n" + bot + "\n" + idx + "\n" + message + "\n";
    }

    /** Centers text in a box of given width, padding with spaces. */
    private static String center(String text, int width) {
        int pad = Math.max(0, width - text.length());
        int left = pad / 2;
        return " ".repeat(left) + text + " ".repeat(pad - left);
    }

    /** Clears the screen. */
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

    /** Returns the color code for a given cell state. */
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
}
