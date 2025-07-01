package doubleList;

import java.util.Arrays;

/**
 * This List is an "implementation" of an ArrayList for memory optimization. Double -> double
 */
public class DoubleList {
    private double[] array;
    private int count = 0;

    /**
     * Convert array double[] to DoubleList
     * @param array input array of double numbers
     */
    public DoubleList(double[] array) {
        this.array = array;
        this.count = array.length;
    }

    /**
     * Default constructor for DoubleList. The default capacity is 1.
     */
    public DoubleList() {
        this.array = new double[1];
    }

    /**
     * Adds an element to the array, the depreciation estimate is O(1).
     * When an element is added, then it is checked whether it is possible to add it "for free".
     * If not, increase the size by 2 times.
     *
     * @param value input double number.
     */
    public void add(double value) {
        if (count == array.length) {
            array = Arrays.copyOf(array, array.length * 2);
        }
        array[count] = value;
        count++;
    }

    /**
     * The function returns the element by index or
     * throws an {@link IndexOutOfBoundsException} if the index goes beyond the boundaries of the array.
     *
     * @param i the index whose element we are trying to get
     * @return double number by index
     * @throws IndexOutOfBoundsException if input index is incorrect. Not in range [0, doubleList.size() - 1].
     */
    public double get(int i) {
        if ((i >= count) || (i < 0)) {
            throw new IndexOutOfBoundsException(i);
        } else {
            return array[i];
        }
    }

    /**
     * Override toString() for current class. Returns a string with special format.
     * @return a string in the format: "a_1";"a_2";"a_3";...;"a_n"
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count - 1; i++) {
            stringBuilder.append("\"").append(array[i] == 0 ? "" : array[i]).append("\";");
        }
        if (count != 0) {
            stringBuilder.append("\"").append(array[count - 1] == 0 ? "" : array[count - 1]).append("\"");
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the number of elements in the given array. Not the capacity!
     *
     * @return array length
     */
    public int size() {
        return count;
    }
}
