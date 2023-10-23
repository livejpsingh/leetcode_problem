package ComparatorComparableExample;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparatorExample {

    public static void main(String[] args) {

        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee(1010,"Jitendra",100000.00, LocalDate.of(2002,7,26)));
        employees.add(new Employee(1003,"Mahendra",93000,LocalDate.of(2005,12,22)));
        employees.add(new Employee(1012,"Suman",134000,LocalDate.of(2004,12,12)));

        System.out.println("Employees : "+ employees);

        //sort employees by name
        Collections.sort(employees, Comparator.comparing(Employee::getName));
        System.out.println("\nEmployee (Sorted by Name): "+ employees);


        //Sort employee by salary
        Collections.sort(employees,Comparator.comparing(Employee::getSalary));
        System.out.println("\nEmployee (Sorted by Salary) :"+employees);



        //Sort employees by JoiningDate
        Collections.sort(employees,Comparator.comparing(Employee::getJoiningDate));
        System.out.println("\nEmployee (Sorted by Joining Date) : "+employees);


        //Sort employees by Name in descending order
        Collections.sort(employees,Comparator.comparing(Employee::getName).reversed());

        //Chaining mutiple Comparators
        //sort by Salary . If Salalry is same then sort by Name

        Collections.sort(employees,Comparator.comparingDouble(Employee::getSalary).thenComparing(Employee::getName));
        System.out.println("\nEmployee (Sorted by Salary and Name ");

    }
}
