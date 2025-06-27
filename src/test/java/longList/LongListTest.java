package longList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LongListTest {
    @Test
    void addTest() {
        final LongList longList = new LongList();
        for (int i = 0; i < 10; i++) {
            longList.add(i);
        }
        for (int i = 0; i < longList.size(); i++) {
            Assertions.assertEquals(i, longList.get(i));
        }
    }

    @Test
    void getWithException() {
        final LongList longList = new LongList(new long[]{
                1, 2, 3, 4, 5
        });
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> longList.get(5));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> longList.get(-1));
        Assertions.assertEquals(1, longList.get(0));
    }

    @Test
    void toStringCorrectFormat() {
        final LongList longList = new LongList(new long[]{
                1, 2, 3, 4, 5
        });
        Assertions.assertEquals("\"1\";\"2\";\"3\";\"4\";\"5\"", longList.toString());
    }

}
