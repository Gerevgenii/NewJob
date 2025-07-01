package org.example;

import doubleList.DoubleList;
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
        System.err.println("Launching the app");
        String outputFilePath = "output.txt";
        if (args.length != 1 && args.length != 2) {
            throw new IllegalArgumentException("Usage: java -Xmx1G -jar build/libs/NewJob-1.0-SNAPSHOT.jar <input-file>");
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
            final ArrayList<DoubleList> arrayList = new ArrayList<>();
            final Collection<String> repeating = new HashSet<>();
            DoubleList doubleList = new DoubleList();

            StringBuilder stringBuilder = new StringBuilder();
            System.err.println("Parse started");
            while (batchScanner.hasNext()) {
                try {
                    final String str = batchScanner.next();
                    if (str == null) {
                        if (doubleList.size() == 0) {
                            batchScanner.openNextLine();
                            continue;
                        }
                        if (repeating.add(stringBuilder.toString())) {
                            arrayList.add(doubleList);
                        }
                        batchScanner.openNextLine();
                        doubleList = new DoubleList();
                        stringBuilder = new StringBuilder();
                        continue;
                    }
                    doubleList.add(str.isEmpty() ? 0 : Double.parseDouble(str));
                    stringBuilder.append("\"").append(str).append("\";");
                } catch (InvalidLineFormatException e) {
                    doubleList = new DoubleList();
                }
            }
            if (repeating.add(stringBuilder.toString())) {
                arrayList.add(doubleList);
            }

            System.err.println("Parse ended. Create union started");

            final int n = arrayList.size();
            final UnionFind unionFind = new UnionFind(n);
            final Map<String, Integer> keyToIndex = new HashMap<>();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < arrayList.get(i).size(); j++) {
                    final double number = arrayList.get(i).get(j);
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

            System.err.println("Create union ended. Grouping by union started");

            final Map<Integer, List<DoubleList>> groups = new HashMap<>();
            for (int i = 0; i < n; i++) {
                final int root = unionFind.find(i);
                //noinspection unused
                groups.computeIfAbsent(root, val -> new ArrayList<>())
                        .add(arrayList.get(i));
            }

            System.err.println("Grouping by union ended. Filter by size and sort started");

            final List<List<DoubleList>> answer = new ArrayList<>();
            for (List<DoubleList> group : groups.values()) {
                if (group.size() > 1) {
                    answer.add(group);
                }
            }
            answer.sort(Comparator.<List<DoubleList>>comparingInt(List::size).reversed());

            System.err.println("Filter by size and sort ended");

            out.println(answer.size());
            for (int i = 0; i < answer.size(); i++) {
                out.println("Group " + i);
                for (DoubleList doubleLists : answer.get(i)) {
                    out.println(doubleLists);
                }
                out.println();
            }

            final long timeDifference = System.currentTimeMillis() - timeStart;
            System.err.println("Program execution time: " + timeDifference / 1000 + " seconds " + timeDifference % 1000 + " milliseconds");
            out.println("Program execution time: " + timeDifference / 1000 + " seconds " + timeDifference % 1000 + " milliseconds");
        }
    }
}