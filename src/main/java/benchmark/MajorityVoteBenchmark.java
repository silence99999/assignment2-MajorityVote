package benchmark;

import algorithms.MajorityVote;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class MajorityVoteBenchmark {

    @Param({"100", "1000", "10000", "100000"})
    private int size;

    private int[] input;

    @Setup(Level.Iteration)
    public void setup() {
        Random random = new Random(42);
        input = new int[size];


        int majorityCount = size / 2 + 1;
        for (int i = 0; i < majorityCount; i++) input[i] = 1;
        for (int i = majorityCount; i < size; i++) input[i] = random.nextInt(10);


        for (int i = input.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = input[i];
            input[i] = input[j];
            input[j] = tmp;
        }
    }

    @Benchmark
    public int runMajorityVote() {
        return MajorityVote.findMajorityElement(input);
    }
}
