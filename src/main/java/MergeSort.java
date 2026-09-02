import java.util.Arrays;

/**
 * 
 * MergeSort
 * mergeSort() recursively splits indices until each array has one element left
 * once one element left for each sub-array, call merge() to sort the two subarrays being compared and overwrite in original array
 */

public class MergeSort{

    public static <T extends Comparable<T>> void mergeSort(T[] arr, boolean ascending, int delayMs) {
        boolean[] sortedMask = new boolean[arr.length]; //persistent, checks state of each element of array: sortedMask[k]==true means "highlight in green"
        boolean[] gapMask = new boolean[arr.length]; // persistent, mirrors sortedMask: gapMask[k]==true means "draw a gap after index k",
                                                       // set by renderSplit and cleared by renderJoin, visible through every level of recursion
        mergeSort(arr, 0, arr.length - 1, ascending, delayMs, sortedMask, gapMask);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, int left, int right, boolean ascending, int delayMs, boolean[] sortedMask, boolean[] gapMask) {
        if (left >= right) {
            if (left == right) sortedMask[left] = true; // single-element base case is trivially sorted
            return;
        }

        int mid = (left + right)/2;
        renderSplit(arr, left, mid, right, sortedMask, gapMask, delayMs); // opens this level's gap, then shows it, before recursing
        
        String leftPart = formatSlice(arr, left, mid);
        String rightPart = formatSlice(arr, mid + 1, right);
        Animator.StepLogger.log("Split: " + leftPart + " and " + rightPart);

        mergeSort(arr, left, mid, ascending, delayMs, sortedMask, gapMask); //recurse left array from split
        mergeSort(arr, mid + 1, right, ascending, delayMs, sortedMask, gapMask); //recurse right array from split
        merge(arr, left, mid, right, ascending, delayMs, sortedMask, gapMask);
    }

    private static <T extends Comparable<T>> void merge(T[] arr, int left, int mid, int right, boolean ascending, int delayMs, boolean[] sortedMask, boolean[] gapMask) {
        T[] leftArr = Arrays.copyOfRange(arr, left, mid + 1); //Third parameter is exclusive
        T[] rightArr = Arrays.copyOfRange(arr, mid + 1, right + 1); //Third parameter is exclusive

        int i = 0, j = 0, k = left; // k tracks the lowest element index of sub-array

        while (i < leftArr.length && j < rightArr.length) { //replace position of lowest element with lowest value while left and right arrays still have values to compare
            renderCompare(arr, left, right, i, j, mid, leftArr[i], rightArr[j], sortedMask, gapMask, delayMs); // comparing the two candidates

            int value = leftArr[i].compareTo(rightArr[j]); //if smaller, returns negative value
            boolean takeLeft = ascending ? (value<=0) : (value>=0); //determines if sorting order is ascending or descending

            if (takeLeft) {
                arr[k] = leftArr[i++];
            } else {
                arr[k] = rightArr[j++];
            }
            sortedMask[k] = true; // placed into merged position
            renderPlace(arr, left, right, k, sortedMask, gapMask, delayMs); // show the overwrite
            k++;
        }

        //if one array's elements are all taken, add all remaining values of other array to remaining positions
        while (i < leftArr.length) {
            arr[k] = leftArr[i++];
            sortedMask[k] = true;
            renderPlace(arr, left, right, k, sortedMask, gapMask, delayMs);
            k++;
        }
        while (j < rightArr.length) {
            arr[k] = rightArr[j++];
            sortedMask[k] = true;
            renderPlace(arr, left, right, k, sortedMask, gapMask, delayMs);
            k++;
        }

        renderJoin(arr, left, mid, right, sortedMask, gapMask, delayMs); // this range is fully merged, heal the gap this level opened

        String mergedPart = formatSlice(arr, left, right);
        boolean isFinal = (left == 0 && right == arr.length - 1);
        Animator.StepLogger.log((isFinal ? "Merge final: " : "Merge step: ") + mergedPart);
    }

    private static <T> String formatSlice(T[] arr, int from, int to) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = from; i <= to; i++) {
            sb.append(arr[i]);
            if (i < to) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    // ---- render wrappers: build a BoxSpec[] for this moment, then hand it to the box drawer ----

    /**
     * Base spec builder shared by all of MergeSort's render moments: marks everything
     * outside [left,right] as inactive, marks sorted positions green, and opens visual
     * gaps so the active range reads as its own separated block. Two independent sources
     * of gaps are combined:
     *   - boundaryGap: local to THIS call, framing the currently active window --
     *     at left-1 (so it doesn't blend into the dimmed boxes before it) and at
     *     right (so it doesn't blend into the dimmed boxes after it).
     *   - gapMask: persistent across the whole recursion tree, like sortedMask.
     *     Every split opened by renderSplit (at any level, ancestor or current)
     *     stays visible until the matching renderJoin heals it, so a gap opened
     *     three levels up is still drawn even while rendering a leaf-level compare.
     */
    private static <T> Animator.BoxSpec[] baseSpecs(T[] arr, int left, int right, boolean[] sortedMask, boolean[] gapMask) {
        Animator.BoxSpec[] specs = Animator.freshSpecs(arr);
        for (int k = 0; k < arr.length; k++) {
            if (k < left || k > right) {
                specs[k].state = Animator.CellState.OUT_OF_RANGE;
            } else if (sortedMask[k]) {
                specs[k].state = Animator.CellState.SORTED;
            }
            boolean boundaryGap = (k == left - 1) || (k == right);
            specs[k].gapAfter = boundaryGap || gapMask[k];
        }
        return specs;
    }

    private static <T> void renderSplit(T[] arr, int left, int mid, int right, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        gapMask[mid] = true; // open this level's gap -- stays open through both halves' full recursion until this range's renderJoin heals it
        Animator.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, gapMask);
        String message = "Splitting [" + left + ".." + right + "] into [" + left + ".." + mid + "] and [" + (mid + 1) + ".." + right + "]";
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderCompare(T[] arr, int left, int right, int i, int j, int mid, T leftVal, T rightVal, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        Animator.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, gapMask); // this level's gap (and every ancestor's) is already open in gapMask
        int leftIdx = left + i;
        int rightIdx = mid + 1 + j;
        specs[leftIdx].state = Animator.CellState.COMPARING;
        specs[leftIdx].lift = true;
        specs[rightIdx].state = Animator.CellState.COMPARING;
        specs[rightIdx].lift = true;
        String message = "Comparing " + leftVal + " (left half) and " + rightVal + " (right half)";
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderPlace(T[] arr, int left, int right, int k, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        Animator.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, gapMask); // still split until this level's join
        specs[k].state = Animator.CellState.SWAPPING; // reuse "active write" red; message clarifies it's a placement, not a swap
        specs[k].lift = true;
        String message = "Placing " + arr[k] + " at index " + k;
        Animator.render(arr, specs, message, delayMs);
    }

    private static <T> void renderJoin(T[] arr, int left, int mid, int right, boolean[] sortedMask, boolean[] gapMask, int delayMs) {
        gapMask[mid] = false; // heal exactly the gap this level's renderSplit opened -- ancestor gaps, if any, remain open
        Animator.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, gapMask);
        String message = "Merged [" + left + ".." + right + "]";
        Animator.render(arr, specs, message, delayMs);
    }
}