package ComparatorComparableExample;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparableExamaple {

    public static void main(String[] args) {

        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee(1011, "Jitendra", 99000, LocalDate.of(1986,9,28)));
        employees.add(new Employee(1002,"Mahendra",180000, LocalDate.of(1984,2,2)));
        employees.add(new Employee(1018,"Vivek",78000, LocalDate.of(1987,11,10)));

        System.out.println("Employees (Before Sorting): "+ employees);


        // This will use the `compareTo()` method of the `Employee` class to compare two employees and sort them.

        Collections.sort(employees);

        System.out.println("\nEmployees (after Sorting) : "+ employees);
    }


}
