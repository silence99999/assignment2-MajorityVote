package algorithms;

import metrics.PerformanceTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;


class MajorityVoteTest {

    @Test
    @DisplayName("Throws on null input")
    void testNullArray() {
        assertThrows(IllegalArgumentException.class,
                () -> MajorityVote.findMajorityElement(null));
    }

    @Test
    @DisplayName("Throws on empty array")
    void testEmptyArray() {
        int[] nums = {};
        assertThrows(IllegalArgumentException.class,
                () -> MajorityVote.findMajorityElement(nums));
    }

    @Test
    @DisplayName("Single-element array returns the element itself")
    void testSingleElement() {
        int[] nums = {42};
        assertEquals(42, MajorityVote.findMajorityElement(nums));
    }

    @Test
    @DisplayName("All elements identical → that element is majority")
    void testAllSame() {
        int[] nums = {7, 7, 7, 7, 7};
        assertEquals(7, MajorityVote.findMajorityElement(nums));
    }

    @Test
    @DisplayName("Typical majority example")
    void testTypicalMajority() {
        int[] nums = {2, 2, 1, 1, 1, 2, 2};
        assertEquals(2, MajorityVote.findMajorityElement(nums));
    }

    @Test
    @DisplayName("Array with majority at the end")
    void testMajorityAtEnd() {
        int[] nums = {1, 3, 3, 2, 2, 2, 2, 2};
        assertEquals(2, MajorityVote.findMajorityElement(nums));
    }

    @Test
    @DisplayName("Array without strict majority still returns a candidate")
    void testNoStrictMajority() {
        int[] nums = {1, 2, 3, 1, 2, 3};
        int candidate = MajorityVote.findMajorityElement(nums);
        assertTrue(candidate == 1 || candidate == 2 || candidate == 3);
    }

    @Test
    @DisplayName("Metrics tracker records operations")
    void testWithMetrics() {
        int[] nums = {2, 2, 1, 2};
        PerformanceTracker tracker = new PerformanceTracker();
        int result = MajorityVote.findMajorityElement(nums, tracker);

        assertEquals(2, result);
        assertTrue(tracker.getComparisons() > 0, "Comparisons should be recorded");
        assertTrue(tracker.getArrayAccesses() >= nums.length, "Array accesses should be recorded");
        assertTrue(tracker.getElapsedMs() >= 0);
    }

    @Test
    void testEmptyArrayThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                MajorityVote.findMajorityElement(new int[]{}));
    }

    @Test
    void testSingleElementArray() {
        int result = MajorityVote.findMajorityElement(new int[]{42});
        assertEquals(42, result);
    }
}
