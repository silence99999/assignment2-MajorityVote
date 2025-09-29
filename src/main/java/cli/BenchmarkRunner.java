package cli;

import algorithms.MajorityVote;
import metrics.PerformanceTracker;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;


public class BenchmarkRunner {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== MajorityVote Benchmark Runner ===");
        System.out.print("Enter input sizes (comma-separated, e.g. 100,1000,10000,100000): ");
        String[] parts = sc.nextLine().trim().split(",");
        int[] sizes = Arrays.stream(parts)
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();

        System.out.println("Select distribution: ");
        System.out.println("1 - Random");
        System.out.println("2 - Sorted ascending");
        System.out.println("3 - Sorted descending");
        System.out.println("4 - Nearly-sorted (90% sorted, 10% random)");
        System.out.print("Choice: ");
        int choice = sc.nextInt();

        String distribution = switch (choice) {
            case 1 -> "random";
            case 2 -> "sorted";
            case 3 -> "reverse-sorted";
            case 4 -> "nearly-sorted";
            default -> "random";
        };

        System.out.println("Running benchmarks for distribution: " + distribution);
        System.out.println("Results will be saved in docs/performance-plots/benchmarks.csv\n");

        for (int n : sizes) {
            int[] arr = generateArray(n, distribution);

            PerformanceTracker tracker = new PerformanceTracker();
            int candidate = MajorityVote.findMajorityElement(arr, tracker);


            tracker.exportCsv(
                    Paths.get("docs/performance-plots/benchmarks.csv"),
                    "MajorityVote-" + distribution,
                    n
            );

            System.out.printf("n=%-8d candidate=%-5d time=%.3f ms comparisons=%d accesses=%d memΔ=%d bytes%n",
                    n,
                    candidate,
                    tracker.getElapsedMs(),
                    tracker.getComparisons(),
                    tracker.getArrayAccesses(),
                    tracker.getDeltaUsedHeapBytes()
            );
        }

        System.out.println("\nBenchmark complete. Check docs/performance-plots/benchmarks.csv for results.");
    }


    private static int[] generateArray(int n, String distribution) {
        int[] arr = new int[n];

        switch (distribution) {
            case "random" -> {

                int majority = 1;
                int majorityCount = n / 2 + 1;
                for (int i = 0; i < majorityCount; i++) arr[i] = majority;
                for (int i = majorityCount; i < n; i++) arr[i] = RANDOM.nextInt(10);
                shuffle(arr);
            }
            case "sorted" -> {
                for (int i = 0; i < n; i++) arr[i] = i;
            }
            case "reverse-sorted" -> {
                for (int i = 0; i < n; i++) arr[i] = n - i;
            }
            case "nearly-sorted" -> {

                for (int i = 0; i < n; i++) arr[i] = i;
                int swaps = n / 10;
                for (int i = 0; i < swaps; i++) {
                    int idx1 = RANDOM.nextInt(n);
                    int idx2 = RANDOM.nextInt(n);
                    int tmp = arr[idx1];
                    arr[idx1] = arr[idx2];
                    arr[idx2] = tmp;
                }
            }
            default -> {
                for (int i = 0; i < n; i++) arr[i] = RANDOM.nextInt(100);
            }
        }
        return arr;
    }


    private static void shuffle(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}
