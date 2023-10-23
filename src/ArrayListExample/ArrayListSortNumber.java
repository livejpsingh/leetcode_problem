package ArrayListExample;

import java.util.ArrayList;
import java.util.Collections;

public class ArrayListSortNumber {

    public static void main(String[] args) {

        ArrayList list = new ArrayList();


        list.add(10);
        list.add(5);
        list.add(35);
        list.add(55);
        list.add(25);

        // Size of list
        System.out.println("Size of Number of List"+list.size());

        // before sorting of list
        System.out.println( "Before soring list of number"+list);
        Collections.sort(list);
        // after sorting of list
        System.out.println("after sorting list of number are"+list );

    }
}
