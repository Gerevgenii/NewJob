package doubleList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleListTest {
    @Test
    void addTest() {
        final DoubleList doubleList = new DoubleList();
        for (int i = 0; i < 10; i++) {
            doubleList.add(i);
        }
        for (int i = 0; i < doubleList.size(); i++) {
            Assertions.assertEquals(i, doubleList.get(i));
        }
    }

    @Test
    void getWithException() {
        final DoubleList doubleList = new DoubleList(new double[]{
                1, 2, 3, 4, 5
        });
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> doubleList.get(5));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> doubleList.get(-1));
        Assertions.assertEquals(1, doubleList.get(0));
    }

    @Test
    void toStringCorrectFormat() {
        final DoubleList doubleList = new DoubleList(new double[]{
                1, 2, 3, 4, 5
        });
        Assertions.assertEquals("\"1.0\";\"2.0\";\"3.0\";\"4.0\";\"5.0\"", doubleList.toString());
    }

}
