import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Ask for sort order
        System.out.println("Ascending [true] or Descending [false]?");
        boolean ascending = sc.nextBoolean();

        // Ask for type
        System.out.println("Numbers [1] or Strings [2]?");
        int type = sc.nextInt();

        // Ask for number of values
        System.out.println("Enter number of values to sort:");
        int num = sc.nextInt();

        if (type == 1) {
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
    }
}
