
public class AnimationUtil {
 
    // ANSI color codes
    static final String RESET = "\033[0m";
    static final String YELLOW = "\033[33m"; // comparing
    static final String RED = "\033[31m";    // swapping
    static final String GREEN = "\033[32m";  // sorted

    /**
     * Clears the screen and redraws the array as a row of boxes.
     * The two active boxes (a, b) are drawn one line higher than the rest,
     * with a connecting "lift" marker, to make the active operation obvious.
     *
     * @param arr           the array to draw
     * @param a             first active index, or -1 for none
     * @param b             second active index, or -1 for none
     * @param isSwap        true = highlight active boxes red (swap), false = yellow (compare)
     * @param sortedMask    which elements are sorted
     * @param delayMs       Number of miliseconds to delay frame rendering
     */
    public static <T> void render(T[] arr, int a, int b, boolean isSwap, boolean[] sortedMask, int delayMs) {
        clearScreen();

        int width = 1;
        for (T v: arr) {
            width = Math.max(width, String.valueOf(v).length()); // tracks largest width across the array
        }
        width += 2; // adds left and right padding

        //Box Components
        StringBuilder lift = new StringBuilder();
        StringBuilder top  = new StringBuilder();
        StringBuilder mid  = new StringBuilder();
        StringBuilder bot  = new StringBuilder();
        StringBuilder idx  = new StringBuilder();

        int n = arr.length;

        for (int i = 0; i < n; i++) {
            boolean active = (i == a || i == b);
            boolean sorted = sortedMask[i];

            String color = "";
            if (active) {
            color = isSwap ? RED : YELLOW;
            } else if (sorted) {
                color = GREEN;
            }
                
            // lift row: ^ centered above the box, same width as top/bot
            String liftStr = active ? "^" : "";
            int liftPad = (width + 2) - liftStr.length();
            int liftLeft = liftPad / 2;
            int liftRight = liftPad - liftLeft;
            lift.append(" ".repeat(liftLeft)).append(liftStr).append(" ".repeat(liftRight)).append(" ");

            // top border
            top.append(color).append("+").append("-".repeat(width)).append("+").append(RESET).append(" ");

            // middle row: the number, centered
            String valStr = String.valueOf(arr[i]);
            int totalPad = width - valStr.length();
            int padLeft = totalPad / 2;
            int padRight = totalPad - padLeft;
            mid.append(color).append("|")
            .append(" ".repeat(padLeft)).append(valStr).append(" ".repeat(padRight))
            .append("|").append(RESET).append(" ");
            
            // bottom border
            bot.append(color).append("+").append("-".repeat(width)).append("+").append(RESET).append(" ");
            
            // index row, centered same way under the box
            String idxStr = String.valueOf(i);
            int idxPad = (width + 2) - idxStr.length();
            int idxLeft = idxPad / 2;
            int idxRight = idxPad - idxLeft;
            idx.append(" ".repeat(idxLeft)).append(idxStr).append(" ".repeat(idxRight)).append(" ");
        }

        System.out.println(lift);
        System.out.println(top);
        System.out.println(mid);
        System.out.println(bot);
        System.out.println(idx);

        if (isSwap) {
            System.out.println("Swapping indices " + a + " and " + b);
        } else if (a >= 0 && b >= 0) {
            System.out.println("Comparing indices " + a + " and " + b);
        } else {
            System.out.println("Sorted!");
        }

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
 
