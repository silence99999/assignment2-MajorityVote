package algorithms;

import metrics.PerformanceTracker;

public class MajorityVote {

    public static int findMajorityElement(int[] nums, PerformanceTracker tracker) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty");
        }


        long comparisons = 0;
        long arrayAccesses = 0;

        if (tracker != null) tracker.start();

        int candidate = 0;
        int count = 0;

        for (int i = 0; i < nums.length; i++) {
            arrayAccesses++;
            int num = nums[i];

            comparisons++;
            if (count == 0) {
                candidate = num;
            }

            comparisons++;
            if (num == candidate) {
                count++;
            } else {
                count--;
            }
        }

        if (tracker != null) {
            tracker.stop();
            tracker.addArrayAccesses(arrayAccesses);
            tracker.addComparisons(comparisons);
        }
        return candidate;
    }

    public static int findMajorityElement(int[] nums) {
        return findMajorityElement(nums, null);
    }
}
