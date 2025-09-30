package metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long arrayAccesses;

    private long startTimeNs;
    private long endTimeNs;

    private long startUsedHeapBytes;
    private long endUsedHeapBytes;

    public PerformanceTracker() {
        reset();
    }

    public synchronized void reset() {
        comparisons = 0;
        swaps = 0;
        arrayAccesses = 0;
        startTimeNs = 0;
        endTimeNs = 0;
        startUsedHeapBytes = 0;
        endUsedHeapBytes = 0;
    }

    public synchronized void start() {
        System.gc();
        startUsedHeapBytes = getUsedHeapBytes();
        startTimeNs = System.nanoTime();
    }

    public synchronized void stop() {
        endTimeNs = System.nanoTime();
        endUsedHeapBytes = getUsedHeapBytes();
    }

    public synchronized void incrementComparisons() { comparisons++; }
    public synchronized void addComparisons(long n) { if (n > 0) comparisons += n; }

    public synchronized void incrementSwaps() { swaps++; }
    public synchronized void addSwaps(long n) { if (n > 0) swaps += n; }

    public synchronized void incrementArrayAccesses() { arrayAccesses++; }
    public synchronized void addArrayAccesses(long n) { if (n > 0) arrayAccesses += n; }

    public synchronized double getElapsedMs() {
        if (startTimeNs == 0) return 0.0;
        long end = (endTimeNs == 0) ? System.nanoTime() : endTimeNs;
        return (end - startTimeNs) / 1_000_000.0;
    }

    public synchronized long getDeltaUsedHeapBytes() {
        if (startUsedHeapBytes == 0) return 0L;
        long end = (endUsedHeapBytes == 0) ? getUsedHeapBytes() : endUsedHeapBytes;
        return end - startUsedHeapBytes;
    }

    public synchronized long getComparisons() { return comparisons; }
    public synchronized long getSwaps() { return swaps; }
    public synchronized long getArrayAccesses() { return arrayAccesses; }

    private static long getUsedHeapBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public synchronized void exportCsv(Path csvPath, String algorithm, long inputSize) throws IOException {
        Objects.requireNonNull(csvPath, "csvPath");
        Objects.requireNonNull(algorithm, "algorithm");

        Path parent = csvPath.getParent();
        if (parent != null) Files.createDirectories(parent);

        boolean writeHeader = Files.notExists(csvPath) || Files.size(csvPath) == 0;

        String header = "algorithm,inputSize,comparisons,swaps,arrayAccesses,timeMs,memoryDeltaBytes,timestamp";
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        // Force US locale to ensure '.' decimal separator
        String formattedTime = String.format(Locale.US, "%.3f", getElapsedMs());

        String row = String.format(Locale.US,
                "%s,%d,%d,%d,%d,%s,%d,%s%n",
                escapeCsv(algorithm),
                inputSize,
                comparisons,
                swaps,
                arrayAccesses,
                formattedTime,
                getDeltaUsedHeapBytes(),
                timestamp);

        OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND};

        try (BufferedWriter bw = Files.newBufferedWriter(csvPath, java.nio.charset.StandardCharsets.UTF_8, options)) {
            if (writeHeader) {
                bw.write(header);
                bw.newLine();
            }
            bw.write(row);
            bw.flush();
        }
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            String escaped = s.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return s;
    }

    @Override
    public synchronized String toString() {
        return "PerformanceTracker{" +
                "comparisons=" + comparisons +
                ", swaps=" + swaps +
                ", arrayAccesses=" + arrayAccesses +
                ", elapsedMs=" + String.format(Locale.US, "%.3f", getElapsedMs()) +
                ", memoryDeltaBytes=" + getDeltaUsedHeapBytes() +
                '}';
    }
}
