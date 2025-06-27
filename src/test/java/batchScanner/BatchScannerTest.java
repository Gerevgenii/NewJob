package batchScanner;

import longList.LongList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scanner.BatchScanner;
import scanner.InvalidLineFormatException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("AssignmentToForLoopParameter")
public class BatchScannerTest {

    @Test
    void readWithoutDeleteAnyLine() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"222";"333"\r
                "444";"555";"666"\r
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final LongList longList = new LongList();
        for (int i = 1; i < 10; i++) {
            final String str = batchScanner.next();
            if (str == null) {
                batchScanner.openNextLine();
                i--;
                continue;
            }
            longList.add(Long.parseLong(str));
        }
        Assertions.assertEquals("\"111\";\"222\";\"333\";\"444\";\"555\";\"666\";\"777\";\"888\";\"999\"", longList.toString());

        bufferedReader.close();
    }

    @Test
    void readWithDeleteLine() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"222";"333"\r
                "444;"555";"666"\r
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final LongList currentLongList = new LongList();
        for (int i = 1; batchScanner.hasNext(); i++) {
            final String str = batchScanner.next();
            if (str == null) {
                if (i == 4) {
                    Assertions.assertThrows(InvalidLineFormatException.class, batchScanner::openNextLine);
                } else {
                    batchScanner.openNextLine();
                }
                continue;
            }
            currentLongList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"111\";\"222\";\"333\";\"777\";\"888\";\"999\"", currentLongList.toString());

        bufferedReader.close();
    }

    @Test
    void readWithMissedClosedQuote() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"222";"333"\r
                "444";"555";"666"\r
                "777";"888";"999
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final LongList longList = new LongList();

        for (int i = 1; batchScanner.hasNext(); i++) {
            final String str;
            if (i == 8) {
                Assertions.assertThrows(InvalidLineFormatException.class, batchScanner::next);
                continue;
            } else {
                str = batchScanner.next();
            }
            if (str == null) {
                batchScanner.openNextLine();
                i--;
                continue;
            }
            longList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"111\";\"222\";\"333\";\"444\";\"555\";\"666\";\"777\"", longList.toString());
        bufferedReader.close();
    }

    @Test
    void readWithThreeQuote() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"222";"333""444";"555";"666"\r
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final LongList longList = new LongList();
        for (int i = 1; batchScanner.hasNext(); i++) {
            final String str;
            if (i == 2) {
                Assertions.assertThrows(InvalidLineFormatException.class, batchScanner::next);
                continue;
            } else {
                str = batchScanner.next();
            }
            if (str == null) {
                batchScanner.openNextLine();
                i--;
                continue;
            }
            longList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"111\";\"777\";\"888\";\"999\"", longList.toString());
        bufferedReader.close();
    }

    @Test
    void unexpectedPositionOfLineSeparator() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"22\r
                "222";"333"\r
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final LongList longList = new LongList();
        for (int i = 1; batchScanner.hasNext(); i++) {
            final String str;
            if (i <= 1) {
                Assertions.assertThrows(InvalidLineFormatException.class, batchScanner::next);
                continue;
            } else {
                str = batchScanner.next();
            }
            if (str == null) {
                batchScanner.openNextLine();
                i--;
                continue;
            }
            longList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"222\";\"333\";\"777\";\"888\";\"999\"", longList.toString());
        bufferedReader.close();
    }

    @SuppressWarnings("DuplicateThrows")
    @Test
    void differentExceptionParseTest() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "1";"111";"222";"333333";"555"\r
                "2";"2987622"333";"333";"666"\r
                "3";"222";"333";"333";"777333"\r
                "4";"222";"333333""333";"888"\r
                "5";"222222";"333";333";"999"\r
                "8383"200000741652251"\r
                "6";"22332";"333";"3333";"555"\r
                "7";"22332";"333";"3333;"555"\r
                "8";"22332";"333";"3333";"555"\r
                "79855053897"83100000580443402";"200000133000191"\r
                "9";"22112";"333";"333";"22233"\r
                "79855053897"83100000580443402";"200000133000191"\r
                "10";"";""\r
                "";"";""\r
                "11";";""\r
                "12";"222";"333"\r
                "13";"222";""333"\r
                ""\r
                "14";;;"15"\r
                "15"\r
                "16";"";"12361346";"13461466
                """));
        // Correct lines: 1, 3, 6, 8, 9, 10, "";"";"", 12, "", 15

        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        LongList longList = new LongList();
        final List<LongList> arrayList = new ArrayList<>();
        while (batchScanner.hasNext()) {
            try {
                final String str = batchScanner.next();

                if (str == null) {
                    if (longList.size() != 0) {
                        arrayList.add(longList);
                    }
                    batchScanner.openNextLine();
                    longList = new LongList();
                    continue;
                }
                longList.add(str.isEmpty() ? 0 : Long.parseLong(str));
            } catch (InvalidLineFormatException e) {
                longList = new LongList();
            }
        }
        if (longList.size() != 0) {
            arrayList.add(longList);
        }
        for (LongList list : arrayList) {
            System.out.println(list);
        }

        batchScanner.close();
    }
}
