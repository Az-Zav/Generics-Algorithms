import java.util.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
    private static final int DELAY_MS = 500; // pace of the animation, single source of truth

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Terminal terminal = TerminalBuilder.builder().system(true).build();

        // Ask for sort order (arrow-key menu)
        int orderChoice = Navigator.promptMenu(
                terminal,
                "Ascending or Descending?",
                List.of("Ascending", "Descending"));
        boolean ascending = (orderChoice == 1);

        // Ask for type (arrow-key menu)
        int typeChoice = Navigator.promptMenu(
                terminal,
                "Numbers or Strings?",
                List.of("Numbers", "Strings"));

        // Ask for sorting algorithm (arrow-key menu)
        int algoChoice = Navigator.promptMenu(
                terminal,
                "Which sorting algorithm?",
                List.of("Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"));

        // Ask for number of values (still free-text, so Scanner stays)
        clearScreen();
        System.out.println("Enter number of values to sort:");
        int num = sc.nextInt();

        if (typeChoice == 1) {
            // Integer input
            Integer[] arr = new Integer[num];
            System.out.println("Enter " + num + " integers:");
            for (int i = 0; i < num; i++) {
                arr[i] = sc.nextInt();
            }

            AnimationUtil.StepLogger.clear();
            runSort(arr, algoChoice, ascending);
            AnimationUtil.StepLogger.printSummary();
            System.out.println("\033[32m" + "Sorted result: " + Arrays.toString(arr) + "\033[0m");

        } else {
            // String input
            String[] arr = new String[num];
            System.out.println("Enter " + num + " strings:");
            for (int i = 0; i < num; i++) {
                arr[i] = sc.next();
            }

            AnimationUtil.StepLogger.clear();
            runSort(arr, algoChoice, ascending);
            AnimationUtil.StepLogger.printSummary();
            System.out.println("\033[32m" + "Sorted result: " + Arrays.toString(arr) + "\033[0m");
        }

        sc.close();
        terminal.close();
    }

    private static <T extends Comparable<T>> void runSort(T[] arr, int algoChoice, boolean ascending) {
        switch (algoChoice) {
            case 1 -> SelectionSort.selectionSort(arr, ascending);
            case 2 -> InsertionSort.insertionSort(arr, ascending);
            case 3 -> MergeSort.mergeSort(arr, ascending, DELAY_MS);
            case 4 -> QuickSort.quickSort(arr, ascending, DELAY_MS);
            default -> throw new IllegalArgumentException("Unknown algorithm choice: " + algoChoice);
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