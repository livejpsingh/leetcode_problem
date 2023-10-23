package hashset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HashSetFromCollectionExample {
    public static void main(String[] args) {
        List<Integer> numberDivisibleBy5 = new ArrayList<>();
        numberDivisibleBy5.add(5);
        numberDivisibleBy5.add(10);
        numberDivisibleBy5.add(15);
        numberDivisibleBy5.add(20);
        numberDivisibleBy5.add(25);
        numberDivisibleBy5.add(30);

        List<Integer> numberDivisibleBy3 = new ArrayList<>();
        numberDivisibleBy3.add(3);
        numberDivisibleBy3.add(6);
        numberDivisibleBy3.add(9);
        numberDivisibleBy3.add(12);
        numberDivisibleBy3.add(15);


        // Creating a HashSet from another Collection (ArrayList)

        Set<Integer> numberDivisibleBy5or3 = new HashSet<>(numberDivisibleBy5);

        // Adding all the elements from an exisitng collection to HashSet
        numberDivisibleBy5or3.addAll(numberDivisibleBy3);

        System.out.println(numberDivisibleBy5or3);
        System.out.println("\n Size of Set is "+numberDivisibleBy5or3.size());
    }
}
