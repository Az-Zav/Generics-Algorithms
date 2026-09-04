# Generics-Algorithms

Four classic sorting algorithms written against `T extends Comparable<T>`, each
one animating itself in the terminal as it runs.

No build tool and no dependencies — just a JDK (17 or newer).

## Run

```sh
javac -d out src/*.java
java -cp out Main
```

`Main` asks four questions on stdin — sort order, value type, algorithm, and how
many values — then reads one value per line. It animates the sort, prints the
full step log, and prints the sorted result.

## Algorithms

| Algorithm | Entry point |
|---|---|
| Selection sort | `SelectionSort.selectionSort(arr, ascending)` |
| Insertion sort | `InsertionSort.insertionSort(arr, ascending)` |
| Merge sort | `MergeSort.mergeSort(arr, ascending)` |
| Quick sort | `QuickSort.quickSort(arr, ascending)` |

Each also takes an `Animator<T>` as a third argument. The two-argument form
sorts silently; pass an animator to watch it happen:

```java
Integer[] arr = {5, 2, 8, 1, 9};

QuickSort.quickSort(arr, true);                            // just sort it

Animator<Integer> anim = new Animator<>(arr, 500);         // 500ms per frame
QuickSort.quickSort(arr, true, anim);                      // sort and draw it
anim.printSummary();                                       // replay the steps
```

## How the animation works

`Animator` is created once per sort run and owns everything that has to survive
between frames: the active window, which cells are finished, where the splits
are, and which cell is the currently held pivot or key. Algorithms hold a single
reference to it instead of threading `delayMs` / `sortedMask` / `gapMask` through
every recursive call.

They then just narrate themselves with one-line verbs:

```java
anim.pivot(high, "Selected pivot: " + pivot + " at index " + high);
anim.compare(j, "Comparing " + arr[j] + " with pivot " + pivot);
anim.swap(i, j, "Swapped " + arr[i] + " and " + arr[j]);
anim.settle(p, "Pivot " + arr[p] + " locked in at index " + p);
```

Every verb draws one frame **and** records one line in the step log, so the
summary always matches what was on screen. Colours and box-drawing live entirely
inside `Animator`; no algorithm ever names a colour or builds a frame.

The active window is scoped with try-with-resources, so a recursive call can
never leak its slice into the frames its caller draws afterwards:

```java
try (var range = anim.range(low, high)) {
    int p = partition(arr, low, high, ascending, anim);
    anim.settle(p, "...");
    quickSort(arr, low, p - 1, ascending, anim);
    quickSort(arr, p + 1, high, ascending, anim);
}
```

### Colour key

| Colour | Meaning |
|---|---|
| yellow | being compared |
| red | being swapped or written |
| cyan | shifting right to open a gap |
| magenta | the held pivot or key |
| green | in its final position |
| dim | outside the active window |
