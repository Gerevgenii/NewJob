package scanner;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Current class implements Closable.
 * It is designed to read information from a string piece by piece.
 * Also, the incoming lines must conform to a specific format.
 */
public class BatchScanner implements Closeable {
    private final Reader reader;
    private String nextWord;
    private StringBuilder currentSB = new StringBuilder();
    private boolean isCompletedLine = false;

    /**
     * Constructor of the {@link BatchScanner} class.
     * Initializes variables and tries to read a piece of string from the incoming {@link Reader}.
     *
     * @param reader The incoming {@link Reader} from which we will get the values.
     *
     * @throws IOException if you can't access the file
     * @throws InvalidLineFormatException If the first element in the incoming line has an incorrect format.
     */
    public BatchScanner(Reader reader) throws IOException {
        this.reader = reader;

        final String nextPart = nextPart();
        if (nextPart == null) {
            throw new IOException("Reader haven`t data");
        }
        currentSB.append(nextPart);

        nextWord = getNextWord();
    }

    /**
     * This function reads 10 bytes from the {@link Reader} and returns them as a string.
     *
     * @return The next 10 bytes of the incoming file
     * @throws IOException If it was not possible to access the {@link Reader}
     */
    private String nextPart() throws IOException {
        final CharBuffer charBuffer = CharBuffer.allocate(10);
        if (reader.read(charBuffer) == -1) {
            return null;
        }
        return charBuffer.flip().toString();
    }

    /**
     * This function is designed to remove the first element of the array and smear the remaining ones.
     * I need this function for an array obtained by splitting a string.
     * <pre>
     * Example: \n
     * string: "12"\r\n"1212"\r\n"2314" \n
     * array: ["12", "1212", "2314"]
     * </pre>
     *
     * @param parts an array of split lines
     * @return a string inside the {@link StringBuilder} class
     */
    private StringBuilder deleteFirstAndMerge(String[] parts) {
        if (parts.length == 2) {
            return new StringBuilder(parts[1]);
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            result.append(parts[i]);
            if (i != parts.length - 1)
                result.append(System.lineSeparator());
        }

        return result;
    }

    /**
     * This function removes the incorrect line.
     *
     * @throws IOException if a new chunk could not be read from the {@link Reader}.
     */
    private void clearInvalidLine() throws IOException {
        final String lastPart = nextPart();
        isCompletedLine = true;
        if (currentSB.toString().contains("\n") || currentSB.toString().contains("\r") || currentSB.toString().contains("\r\n")) {
            if (lastPart != null) {
                currentSB.append(lastPart);
            }
            currentSB = deleteFirstAndMerge(currentSB.toString().split("\\r?\\n|\\r"));
            nextWord = null;
            return;
        }
        if (lastPart != null) {
            currentSB.append(lastPart);
            clearInvalidLine();
        }
        nextWord = null;
    }

    /**
     * closes {@link Reader}.
     *
     * @throws IOException if it was not possible to access the {@link Reader}.
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Checks if there is a next word to read.
     *
     * @return if there is the following word and false otherwise
     */
    public boolean hasNext() {
        return nextWord != null || isCompletedLine;
    }

    /**
     * Opens the next line for reading
     *
     * @throws IOException if it was not possible to access the {@link Reader}.
     */
    public void openNextLine() throws IOException {
        isCompletedLine = false;
        nextWord = getNextWord();
    }

    /**
     * Returns the next value that was read from the {@link Reader}.
     *
     * @return the next value
     * @throws IOException if it was not possible to access the {@link Reader}.
     * @throws InvalidLineFormatException if the string is incorrect
     */
    public String next() throws IOException {
        if (isCompletedLine) {
            final String word = nextWord;
            nextWord = null;
            return word;
        }
        final String word = nextWord;

        nextWord = getNextWord();
        return word;
    }

    /**
     * Returns the following parsed value.
     * I need this function to read the next word after the current one.
     *
     * @return the next value
     * @throws IOException if it was not possible to access the {@link Reader}.
     * @throws InvalidLineFormatException if the string is incorrect
     */
    private String getNextWord() throws IOException, InvalidLineFormatException {
        int count = 0;
        int index = 0;
        final StringBuilder wordSB = new StringBuilder();
        if (index == currentSB.length()) {
            final String nextPart = nextPart();
            if (nextPart == null) {
                if (currentSB.isEmpty()) {
                    return null;
                } else {
                    currentSB = new StringBuilder(currentSB.substring(index));
                    final String s = wordSB.toString();
                    wordSB.setLength(0);
                    return s;
                }
            }
            currentSB.append(nextPart);
        }
        char ch = currentSB.charAt(index);
        while (ch != ';') {

            index++;
            if (index == currentSB.length()) {
                final String nextPart = nextPart();
                if (nextPart == null) {
                    if (currentSB.isEmpty()) {
                        return null;
                    } else {
                        if ((ch == '\r' || ch == '\n') && count != 2) {
                            nextWord = null;
                            throw new InvalidLineFormatException("Current line must have special format.");
                        }
                        currentSB = new StringBuilder(currentSB.substring(index));
                        final String s = wordSB.toString();
                        wordSB.setLength(0);
                        return s;
                    }
                }
                currentSB.append(nextPart);
            }

            if ((ch == '\r' || ch == '\n') && count != 2) {
                clearInvalidLine();
                throw new InvalidLineFormatException("Current line must have special format.");
            } else if (ch == '\r' || ch == '\n') {
                clearInvalidLine();
                return wordSB.toString();
            }
            if (count == 2) {
                clearInvalidLine();
                throw new InvalidLineFormatException("Current line must have special format.");
            }
            if (ch == '\"') {
                count++;
                ch = currentSB.charAt(index);
                continue;
            }
            wordSB.append(ch);
            ch = currentSB.charAt(index);
        }
        if (count != 2) {
            clearInvalidLine();
            throw new InvalidLineFormatException("Current line must have special format.");
        } else {
            currentSB = new StringBuilder(currentSB.substring(index + 1));
            return wordSB.toString();
        }
    }
}