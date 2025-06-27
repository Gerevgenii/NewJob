package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;


@SuppressWarnings("DuplicateThrows")
class MainTest {

    @Test
    void mainTest() throws IOException, java.io.FileNotFoundException {
        Main.main(new String[]{"lng.txt", "output.txt"});
    }
}