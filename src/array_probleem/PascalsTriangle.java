package array_probleem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pascal's Triangle II
 * Given an integer rowIndex, return the rowIndexth row of the Pascal's triangle.
 * Notice that the row index starts from 0.
 * In Pascal's triangle, each number is the sum of the two numbers directly above it.
 * Follow up: Could you optimize your algorithm to use only O(k) extra space?
 * Example 1:
 * Input: rowIndex = 3
 * Output: [1,3,3,1]
 * Example 2:
 * Input: rowIndex = 0
 * Output: [1]
 * Example 3:
 * Input: rowIndex = 1
 * Output: [1,1]
 * Constraints:
 * 0 <= rowIndex <= 33
 */
public class PascalsTriangle {
    public static void main(String[] args) throws Exception {
        System.out.println(new PascalsTriangle().getRow(3));
    }

    public List<Integer> getRow(int rowIndex) {
        int k = rowIndex;
        if (k == 0) return Arrays.asList(1);
        else if (k == 1) return Arrays.asList(1, 1);
        else if (k == 2) return Arrays.asList(1, 2, 1);
        List<Integer> result = new ArrayList<>();
        result.add(2);
        k = k - 2;
        int p, c;
        while (k-- > 0) {
            p = 1;
            int i = 0;
            for (int l = result.size(); i < l; i++) {
                c = result.get(i);
                result.set(i, p + c);
                p = c;
            }
            result.add(p + 1);
        }
        result.add(0, 1);
        result.add(1);
        return result;
    }
}