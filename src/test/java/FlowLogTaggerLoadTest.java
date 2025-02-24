import org.flc.FlowLogTagger;
import org.flc.LookupTableLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.flc.Constants.BASE_PATH_TEST;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlowLogTaggerLoadTest {
    private static final String PROTOCOL_FILE = BASE_PATH_TEST + "protocol-numbers-1.csv";
    private static final String LOOKUP_FILE = BASE_PATH_TEST + "lookup.csv";
    private static final String OUTPUT_FILE = BASE_PATH_TEST + "output.csv";
    private static final String FLOW_LOG_FILE = BASE_PATH_TEST + "flowlogs_loadtesting.txt";
    private static final int THREAD_COUNT = 10; // Simulating 10 concurrent users

    @BeforeEach
    void setUp() throws IOException {
        // Load protocol mappings
        Map<String, String> protocolMap = LookupTableLoader.loadProtocolTable(PROTOCOL_FILE);

        // Generate lookup table data
        List<String> lookupEntries = Utils.generateLookupEntries(protocolMap);

        // Generate Flow log file
        Utils.generateFlowLogs(FLOW_LOG_FILE);

        // Write lookup entries to file
        Files.write(Paths.get(LOOKUP_FILE), lookupEntries);
        System.out.println("Lookup file generated successfully: " + LOOKUP_FILE);
    }

    @Test
    void testConcurrentProcessingPerformance() throws InterruptedException, ExecutionException, IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<Long>> futures = new ArrayList<>();

        // Resource used before processing
        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Suggest GC to get a more accurate measurement
        long beforeUsedMem = runtime.totalMemory() - runtime.freeMemory();

        FlowLogTagger.categorizeLogs(LOOKUP_FILE, FLOW_LOG_FILE, OUTPUT_FILE);

        for (Future<Long> future : futures) {
            future.get(); // Wait for all threads to complete
        }
        executorService.shutdown();

        // total resource usage after processing
        double fileSize = Math.round((double) Files.size(Paths.get(FLOW_LOG_FILE)) / (1024 * 1024));
        long endTime = System.currentTimeMillis();
        long totalExecutionTime = endTime - startTime;
        long afterUsedMem = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Resource Usage :: ");
        System.out.println("File Size: " + fileSize + " MB");
        System.out.println("Total Execution Time: " + (double) totalExecutionTime / 1000 + " sec");
        System.out.println("Memory Used: " + (afterUsedMem - beforeUsedMem) / 1024 + " KB");

        assertTrue(totalExecutionTime < 10000, "Execution time should be under 10 seconds for performance test.");
    }
}
