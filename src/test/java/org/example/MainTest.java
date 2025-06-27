package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;


class MainTest {

    @Test
    void mainTest() throws IOException {
        Main.main(new String[]{"testFile.txt"});
    }
}