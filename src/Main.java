import java.util.Arrays;
import java.util.Scanner;

public class Main {
    //test
    private static final int DELAY_MS = 2500; // pace of the animation, single source of truth

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while(true) {
            boolean ascending = menu(sc, "Sort order", "Ascending", "Descending") == 1;
            int typeChoice = menu(sc, "Value type", "Numbers", "Strings");
            int algoChoice = menu(sc, "Sorting algorithm",
                    "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort");
            int count = readCount(sc);

            if (typeChoice == 1) {
                run(readIntegers(sc, count), algoChoice, ascending);
            } else {
                run(readStrings(sc, count), algoChoice, ascending);
            }

            if(!askRepeat(sc)) break;
        }
        sc.close();
    }

    private static <T extends Comparable<T>> void run(T[] arr, int algoChoice, boolean ascending) {
        Animator<T> anim = new Animator<>(arr, DELAY_MS);

        switch (algoChoice) {
            case 1 -> SelectionSort.selectionSort(arr, ascending, anim);
            case 2 -> InsertionSort.insertionSort(arr, ascending, anim);
            case 3 -> MergeSort.mergeSort(arr, ascending, anim);
            case 4 -> QuickSort.quickSort(arr, ascending, anim);
            default -> throw new IllegalArgumentException("Unknown algorithm choice: " + algoChoice);
        }

        anim.printSummary();
        System.out.println("\n\033[32mSorted result: " + Arrays.toString(arr) + "\033[0m");
    }

    // --- INPUT HELPERS ---

    /** Prints a numbered menu and re-asks until one of its numbers comes back. */
    private static int menu(Scanner sc, String title, String... options) {
        System.out.println("\n" + title + ":");
        for (int i = 0; i < options.length; i++) {
            System.out.println("  [" + (i + 1) + "]" + options[i]);
        }
        while (true) {
            System.out.print("Choice [1-" + options.length + "]: ");

            Integer choice = parseInt(readLine(sc));
            if (choice != null && choice >= 1 && choice <= options.length) {
                return choice;
            }
            System.out.println("Please enter a number between 1 and " + options.length + ".");
        }
    }

    private static int readCount(Scanner sc) {
        while (true) {
            System.out.print("\nHow many values? ");
            Integer count = parseInt(readLine(sc));
            if (count != null && count > 0) {
                return count;
            }
            System.out.println("Please enter a positive whole number.");
        }
    }

    private static Integer[] readIntegers(Scanner sc, int count) {
        Integer[] values = new Integer[count];
        int filled = 0;

        System.out.println("\nEnter " + count + " integers, one per line:");
        while (filled < count) {
            String line = readLine(sc);
            Integer value = parseInt(line);
            if (value == null) {
                System.out.println("  '" + line + "' is not an integer -- skipped");
            } else {
                values[filled++] = value;
            }
        }
        return values;
    }

    private static String[] readStrings(Scanner sc, int count) {
        String[] values = new String[count];
        int filled = 0;

        System.out.println("\nEnter " + count + " strings, one per line:");
        while (filled < count) {
            values[filled++] = readLine(sc);
        }
        return values;
    }

    private static boolean askRepeat(Scanner sc) {
        while (true) {
            System.out.print("\nSort again? [y/n]: ");
            String again = readLine(sc);
            if (again.equalsIgnoreCase("y")) return true;
            if (again.equalsIgnoreCase("n")) return false;
            System.out.println("Please enter 'y' or 'n'.");
        }
    }

    private static String readLine(Scanner sc) {
        return sc.nextLine().trim();
    }

    /** Parses a whole number, or returns null rather than throwing. */
    private static Integer parseInt(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
