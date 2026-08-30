import java.util.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
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

        // Ask for number of values (still free-text, so Scanner stays)
        System.out.println("Enter number of values to sort:");
        int num = sc.nextInt();

        if (typeChoice == 1) {
            // Integer input
            Integer[] arr = new Integer[num];
            System.out.println("Enter " + num + " integers:");
            for (int i = 0; i < num; i++) {
                arr[i] = sc.nextInt();
            }

            SelectionSort.selectionSort(arr, ascending);
            System.out.println("Sorted result: " + Arrays.toString(arr));

        } else {
            // String input
            String[] arr = new String[num];
            System.out.println("Enter " + num + " strings:");
            for (int i = 0; i < num; i++) {
                arr[i] = sc.next();
            }

            SelectionSort.selectionSort(arr, ascending);
            System.out.println("Sorted result: " + Arrays.toString(arr));
        }

        sc.close();
        terminal.close();
    }
}