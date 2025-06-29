package org.example;

import longList.LongList;
import scanner.BatchScanner;
import scanner.InvalidLineFormatException;
import unionFind.UnionFind;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

@SuppressWarnings("DuplicateThrows")
public final class Main {

    public static void main(String[] args) throws IOException, FileNotFoundException {
        final long timeStart = System.currentTimeMillis();
        System.out.println("Launching the app with args={}");
        String outputFilePath = "output.txt";
        if (args.length != 1 && args.length != 2) {
            System.out.println("Usage: java -Xmx1G -jar build/libs/NewJob-1.0-SNAPSHOT.jar <input-file>");
            System.exit(1);
        } else if (args.length == 2 && !args[1].isEmpty()) {
            outputFilePath = args[1];
        }

        try (
                final InputStream fileStream = new FileInputStream(args[0]);
                final InputStream inputStream = args[0].toLowerCase().endsWith(".gz") ? new GZIPInputStream(fileStream) : fileStream;
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(new FileWriter(outputFilePath))
                );
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final BatchScanner batchScanner = new BatchScanner(bufferedReader);
            final ArrayList<LongList> arrayList = new ArrayList<>();
            final Collection<String> repeating = new HashSet<>();
            LongList longList = new LongList();

            StringBuilder stringBuilder = new StringBuilder();
            System.out.println("Parse started");
            while (batchScanner.hasNext()) {
                try {
                    final String str = batchScanner.next();
                    if (str == null) {
                        if (longList.size() == 0) {
                            batchScanner.openNextLine();
                            continue;
                        }
                        if (repeating.add(stringBuilder.toString())) {
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
            if (repeating.add(stringBuilder.toString())) {
                arrayList.add(longList);
            }

            System.out.println("Parse ended. Number of rows found: {}. Create union started");

            final int n = arrayList.size();
            final UnionFind unionFind = new UnionFind(n);
            final Map<String, Integer> keyToIndex = new HashMap<>();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < arrayList.get(i).size(); j++) {
                    final long number = arrayList.get(i).get(j);
                    if (number != 0) {
                        final String key = j + "#" + number;
                        if (keyToIndex.containsKey(key)) {
                            unionFind.union(i, keyToIndex.get(key));
                        } else {
                            keyToIndex.put(key, i);
                        }
                    }
                }
            }

            System.out.println("Create union ended. Grouping by union started");

            final Map<Integer, List<LongList>> groups = new HashMap<>();
            for (int i = 0; i < n; i++) {
                final int root = unionFind.find(i);
                //noinspection unused
                groups.computeIfAbsent(root, val -> new ArrayList<>())
                        .add(arrayList.get(i));
            }

            System.out.println("Grouping by union ended. Number of groups found: {}. Filter by size and sort started");

            final List<List<LongList>> answer = new ArrayList<>();
            for (List<LongList> group : groups.values()) {
                if (group.size() > 1) {
                    answer.add(group);
                }
            }
            answer.sort(Comparator.<List<LongList>>comparingInt(List::size).reversed());

            System.out.println("Filter by size and sort ended");

            out.println(answer.size());
            for (int i = 0; i < answer.size(); i++) {
                out.println("Group " + i);
                for (LongList longLists : answer.get(i)) {
                    out.println(longLists);
                }
                out.println();
            }

            final long timeDifference = System.currentTimeMillis() - timeStart;
            System.out.println("Program execution time: " + timeDifference / 1000 + " seconds " + timeDifference % 1000 + " milliseconds");
            out.println("Program execution time: " + timeDifference / 1000 + " seconds " + timeDifference % 1000 + " milliseconds");
        }
    }
}