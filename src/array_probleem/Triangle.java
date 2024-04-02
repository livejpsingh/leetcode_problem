package array_probleem;

import static java.lang.System.out;

public class Triangle {

    public static void main(String[] args) {
        Triangle triangle = new Triangle();
        triangle.printTriangle(5);
    }

    //write function for  print triangle
    public void printTriangle(int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                out.print("* ");
            }
            out.println();
        }
    }
}
