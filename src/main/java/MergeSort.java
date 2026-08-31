import java.util.Arrays;

/**
 * 
 * MergeSort
 * mergeSort() recursively splits indices until each array has one element left
 * once one element left for each sub-array, call merge() to sort the two subarrays being compared and overwrite in original array
 */

public class MergeSort{

    public static <T extends Comparable<T>> void mergeSort(T[] arr, boolean ascending, int delayMs) {
        boolean[] sortedMask = new boolean[arr.length];
        mergeSort(arr, 0, arr.length - 1, ascending, delayMs, sortedMask);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, int left, int right, boolean ascending, int delayMs, boolean[] sortedMask) {
        if (left >= right) {
            if (left == right) sortedMask[left] = true; // single-element base case is trivially sorted
            return;
        }

        int mid = (left + right)/2;
        renderSplit(arr, left, mid, right, sortedMask, delayMs); // show this level's split before recursing
        mergeSort(arr, left, mid, ascending, delayMs, sortedMask); //recurse left array from split
        mergeSort(arr, mid + 1, right, ascending, delayMs, sortedMask); //recurse right array from split
        merge(arr, left, mid, right, ascending, delayMs, sortedMask);
    }

    private static <T extends Comparable<T>> void merge(T[] arr, int left, int mid, int right, boolean ascending, int delayMs, boolean[] sortedMask) {
        T[] leftArr = Arrays.copyOfRange(arr, left, mid + 1); //Third parameter is exclusive
        T[] rightArr = Arrays.copyOfRange(arr, mid + 1, right + 1); //Third parameter is exclusive

        int i = 0, j = 0, k = left; // k tracks the lowest element index of sub-array

        while (i < leftArr.length && j < rightArr.length) { //replace position of lowest element with lowest value while left and right arrays still have values to compare
            renderCompare(arr, left, mid, right, i, j, leftArr[i], rightArr[j], sortedMask, delayMs); // comparing the two candidates

            int value = leftArr[i].compareTo(rightArr[j]); //if smaller, returns negative value
            boolean takeLeft = ascending ? (value<=0) : (value>=0); //determines if sorting order is ascending or descending

            if (takeLeft) {
                arr[k] = leftArr[i++];
            } else {
                arr[k] = rightArr[j++];
            }
            sortedMask[k] = true; // placed into merged position
            renderPlace(arr, left, mid, right, k, sortedMask, delayMs); // show the overwrite
            k++;
        }

        //if one array's elements are all taken, add all remaining values of other array to remaining positions
        while (i < leftArr.length) {
            arr[k] = leftArr[i++];
            sortedMask[k] = true;
            renderPlace(arr, left, mid, right, k, sortedMask, delayMs);
            k++;
        }
        while (j < rightArr.length) {
            arr[k] = rightArr[j++];
            sortedMask[k] = true;
            renderPlace(arr, left, mid, right, k, sortedMask, delayMs);
            k++;
        }

        renderJoin(arr, left, right, sortedMask, delayMs); // this range is fully merged, heal the gap
    }

    // ---- render wrappers: build a BoxSpec[] for this moment, then hand it to the dumb box drawer ----

    /**
     * Base spec builder shared by all of MergeSort's render moments: marks everything
     * outside [left,right] as inactive, marks sorted positions green, and opens visual
     * gaps so the active range reads as its own separated block:
     *   - at left-1, so the active range doesn't blend into the dimmed boxes before it
     *   - at right, so it doesn't blend into the dimmed boxes after it
     *   - at gapIndex (the split point), so the two active halves read as separate
     * gapIndex = -1 means no internal split gap (used once a range has been merged/joined) --
     * the left/right boundary gaps still apply as long as this range isn't the whole array.
     */
    private static <T> AnimationUtil.BoxSpec[] baseSpecs(T[] arr, int left, int right, boolean[] sortedMask, int gapIndex) {
        AnimationUtil.BoxSpec[] specs = AnimationUtil.freshSpecs(arr);
        for (int k = 0; k < arr.length; k++) {
            if (k < left || k > right) {
                specs[k].state = AnimationUtil.CellState.OUT_OF_RANGE;
            } else if (sortedMask[k]) {
                specs[k].state = AnimationUtil.CellState.SORTED;
            }
            boolean boundaryGap = (k == left - 1) || (k == right);
            specs[k].gapAfter = boundaryGap || (k == gapIndex);
        }
        return specs;
    }

    private static <T> void renderSplit(T[] arr, int left, int mid, int right, boolean[] sortedMask, int delayMs) {
        AnimationUtil.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, mid);
        String message = "Splitting [" + left + ".." + right + "] into [" + left + ".." + mid + "] and [" + (mid + 1) + ".." + right + "]";
        AnimationUtil.render(arr, specs, message, delayMs);
    }

    private static <T> void renderCompare(T[] arr, int left, int mid, int right, int i, int j, T leftVal, T rightVal, boolean[] sortedMask, int delayMs) {
        AnimationUtil.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, mid); // keep the split visible until this range is joined
        int leftIdx = left + i;
        int rightIdx = mid + 1 + j;
        specs[leftIdx].state = AnimationUtil.CellState.COMPARING;
        specs[leftIdx].lift = true;
        specs[rightIdx].state = AnimationUtil.CellState.COMPARING;
        specs[rightIdx].lift = true;
        String message = "Comparing " + leftVal + " (left half) and " + rightVal + " (right half)";
        AnimationUtil.render(arr, specs, message, delayMs);
    }

    private static <T> void renderPlace(T[] arr, int left, int mid, int right, int k, boolean[] sortedMask, int delayMs) {
        AnimationUtil.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, mid); // still split until join
        specs[k].state = AnimationUtil.CellState.SWAPPING; // reuse "active write" red; message clarifies it's a placement, not a swap
        specs[k].lift = true;
        String message = "Placing " + arr[k] + " at index " + k;
        AnimationUtil.render(arr, specs, message, delayMs);
    }

    private static <T> void renderJoin(T[] arr, int left, int right, boolean[] sortedMask, int delayMs) {
        AnimationUtil.BoxSpec[] specs = baseSpecs(arr, left, right, sortedMask, -1); // no gap: halves are healed back together
        String message = "Merged [" + left + ".." + right + "]";
        AnimationUtil.render(arr, specs, message, delayMs);
    }
}