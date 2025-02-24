import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {
    private static final int NUM_ENTRIES = 10000; // Generate 10,000 entries
    private static final int LOG_COUNT = 100000;

    public static List<String> generateLookupEntries(Map<String, String> protocolMap) {
        List<String> lookupEntries = new ArrayList<>();
        lookupEntries.add("dstport,protocol,tag"); // Header

        Random random = new Random();
        String[] tags = {"sv_P1", "sv_P2", "sv_P3", "sv_P4", "sv_P5", "email"};

        for (int i = 0; i < NUM_ENTRIES; i++) {
            int dstPort = random.nextInt(65535); // Random port number (0 - 65535)
            String protocolNumber = String.valueOf(random.nextInt(256)); // Random protocol number (0 - 255)
            String protocol = protocolMap.getOrDefault(protocolNumber, "unknown");
            String tag = tags[random.nextInt(tags.length)]; // Random tag

            lookupEntries.add(dstPort + "," + protocol + "," + tag);
        }

        return lookupEntries;
    }

    public static void generateFlowLogs(String flowlogs) throws IOException {
        List<String> logs = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < LOG_COUNT; i++) {
            int dstPort = random.nextInt(65535); // Random port number (0 - 65535)
            String protocolNumber = String.valueOf(random.nextInt(256)); // Random protocol number (0 - 255)
            logs.add("2 123456789012 eni-0a1b2c3d 10.0.1.201 198.51.100.2 443 " + dstPort + " " + protocolNumber + " 25 20000 1620140761 1620140821 ACCEPT OK");
        }

        Files.write(Paths.get(flowlogs), logs);
    }
}
