package hashset;

import java.util.HashSet;
import java.util.Set;

public class HashSetExample {

    public static void main(String[] args) {


        Set<String> dayOfWeek = new HashSet<>();

        //Adding new elements to the HashSet

        dayOfWeek.add("Monday");
        dayOfWeek.add("Tuesday");
        dayOfWeek.add("Wednesday");
        dayOfWeek.add("Thursday");
        dayOfWeek.add("Friday");
        dayOfWeek.add("Saturday");
        dayOfWeek.add("Sunday");

        // Adding duplicate elements will ignored
        dayOfWeek.add("Monday");
        System.out.println(dayOfWeek);
    }
}
