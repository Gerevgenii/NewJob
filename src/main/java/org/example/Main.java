package org.example;

import longList.LongList;
import scanner.BatchScanner;
import scanner.InvalidLineFormatException;
import unionFind.UnionFind;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("DuplicateThrows")
public final class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, FileNotFoundException {
        final long timeStart = System.currentTimeMillis();
        log.info("Launching the app with args={}", Arrays.toString(args));
        if (args.length != 1) {
            log.info("Usage: java -Xmx1G -jar build/libs/NewJob-1.0-SNAPSHOT.jar <input-file>");
            System.exit(1);
        }

        try (
                final InputStream fileStream = new FileInputStream(args[0]);
                final InputStream inputStream = args[0].toLowerCase().endsWith(".gz") ? new GZIPInputStream(fileStream) : fileStream;
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(new FileWriter("output.txt"))
                );
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final BatchScanner batchScanner = new BatchScanner(bufferedReader);
            final ArrayList<LongList> arrayList = new ArrayList<>();
            final Collection<String> seen = new HashSet<>();
            LongList longList = new LongList();

            StringBuilder stringBuilder = new StringBuilder();
            log.info("Parse started");
            while (batchScanner.hasNext()) {
                try {
                    final String str = batchScanner.next();
                    if (str == null) {
                        if (seen.add(stringBuilder.toString())) {
                            arrayList.add(longList);
                        }
                        batchScanner.openNextLine();
                        longList = new LongList();
                        stringBuilder = new StringBuilder();
                        continue;
                    }
                    longList.add(str.isEmpty() ? 0 : Long.parseLong(str));
                    stringBuilder.append("\"").append(str).append("\";");
                } catch (InvalidLineFormatException e) {
                    longList = new LongList();
                }
            }
            if (seen.add(stringBuilder.toString())) {
                arrayList.add(longList);
            }

            log.info("Parse ended. Number of rows found: {}. Create union started", seen.size());

            final int n = arrayList.size();
            final UnionFind uf = new UnionFind(n);
            final Map<String, Integer> keyToIndex = new HashMap<>();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < arrayList.get(i).size(); j++) {
                    final long number = arrayList.get(i).get(j);
                    if (number != 0) {
                        final String key = j + "#" + number;
                        if (keyToIndex.containsKey(key)) {
                            uf.union(i, keyToIndex.get(key));
                        } else {
                            keyToIndex.put(key, i);
                        }
                    }
                }
            }

            log.info("Create union ended. Grouping by union started");

            final Map<Integer, List<LongList>> groups = new HashMap<>();
            for (int i = 0; i < n; i++) {
                final int root = uf.find(i);
                //noinspection unused
                groups.computeIfAbsent(root, val -> new ArrayList<>())
                        .add(arrayList.get(i));
            }

            log.info("Grouping by union ended. Number of groups found: {}. Filter by size and sort started", groups.size());

            final List<List<LongList>> multi = new ArrayList<>();
            for (List<LongList> g : groups.values()) {
                if (g.size() > 1) {
                    multi.add(g);
                }
            }
            multi.sort(Comparator.<List<LongList>>comparingInt(List::size).reversed());

            log.info("Filter by size and sort ended");

            out.println(multi.size());
            for (int i = 0; i < multi.size(); i++) {
                out.println("Group " + i);
                for (LongList longLists : multi.get(i)) {
                    out.println(longLists);
                }
                out.println();
            }

            final long timeDifference = System.currentTimeMillis() - timeStart;
            log.info("Program execution time: {} seconds {} milliseconds", timeDifference / 1000, timeDifference % 1000);
            out.println("Program execution time: " + timeDifference / 1000 + " seconds " + timeDifference % 1000 + " milliseconds");
        }
    }
}