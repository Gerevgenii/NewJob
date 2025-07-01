package batchScanner;

import doubleList.DoubleList;
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
                "111";"222";"333"
                "444";"555";"666"
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final DoubleList doubleList = new DoubleList();
        for (int i = 1; i < 10; i++) {
            final String str = batchScanner.next();
            if (str == null) {
                batchScanner.openNextLine();
                i--;
                continue;
            }
            doubleList.add(Long.parseLong(str));
        }
        Assertions.assertEquals("\"111.0\";\"222.0\";\"333.0\";\"444.0\";\"555.0\";\"666.0\";\"777.0\";\"888.0\";\"999.0\"", doubleList.toString());

        bufferedReader.close();
    }

    @Test
    void readWithDeleteLine() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"222";"333"
                "444;"555";"666"
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final DoubleList currentDoubleList = new DoubleList();
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
            currentDoubleList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"111.0\";\"222.0\";\"333.0\";\"777.0\";\"888.0\";\"999.0\"", currentDoubleList.toString());

        bufferedReader.close();
    }

    @Test
    void readWithMissedClosedQuote() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"222";"333"
                "444";"555";"666"
                "777";"888";"999
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final DoubleList doubleList = new DoubleList();

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
            doubleList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"111.0\";\"222.0\";\"333.0\";\"444.0\";\"555.0\";\"666.0\";\"777.0\"", doubleList.toString());
        bufferedReader.close();
    }

    @Test
    void readWithThreeQuote() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"222";"333""444";"555";"666"
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final DoubleList doubleList = new DoubleList();
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
            doubleList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"111.0\";\"777.0\";\"888.0\";\"999.0\"", doubleList.toString());
        bufferedReader.close();
    }

    @Test
    void unexpectedPositionOfLineSeparator() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "111";"22
                "222";"333"
                "777";"888";"999"
                """));
        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        final DoubleList doubleList = new DoubleList();
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
            doubleList.add(Integer.parseInt(str));
        }
        Assertions.assertEquals("\"222.0\";\"333.0\";\"777.0\";\"888.0\";\"999.0\"", doubleList.toString());
        bufferedReader.close();
    }

    @SuppressWarnings("DuplicateThrows")
    @Test
    void differentExceptionParseTest() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new StringReader("""
                "0";;;"1";
                "1";"111";"222";"333333";"555"
                "2";"2987622"333";"333";"666"
                "3";"222";"333";"333";"777333"
                "4";"222";"333333""333";"888"
                "5";"222222";"333";333";"999"
                "8383"200000741652251"
                "6";"22332";"333";"3333";"555"
                "7";"22332";"333";"3333;"555"
                "8";"22332";"333";"3333";"555"
                "79855053897"83100000580443402";"200000133000191"
                "9";"22112";"333";"333";"22233"
                "79855053897"83100000580443402";"200000133000191"
                "10";"";""
                "";"";""
                "11";";""
                "12";"222";"333"
                "13";"222";""333"
                ""
                "14";;;"15"
                "15"
                "16";"";"12361346";"13461466
                """));
        // Correct lines: 0, 1, 3, 6, 8, 9, 10, "";"";"", 12, "", 14, 15

        final BatchScanner batchScanner = new BatchScanner(bufferedReader);
        DoubleList doubleList = new DoubleList();
        final List<DoubleList> arrayList = new ArrayList<>();
        while (batchScanner.hasNext()) {
            try {
                final String str = batchScanner.next();

                if (str == null) {
                    if (doubleList.size() != 0) {
                        arrayList.add(doubleList);
                    }
                    batchScanner.openNextLine();
                    doubleList = new DoubleList();
                    continue;
                }
                doubleList.add(str.isEmpty() ? 0 : Long.parseLong(str));
            } catch (InvalidLineFormatException e) {
                doubleList = new DoubleList();
            }
        }
        if (doubleList.size() != 0) {
            arrayList.add(doubleList);
        }
        for (DoubleList list : arrayList) {
            System.out.println(list);
        }

        batchScanner.close();
    }
}
