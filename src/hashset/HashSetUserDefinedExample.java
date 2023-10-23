package hashset;

import java.util.HashSet;
import java.util.Set;

public class HashSetUserDefinedExample {

    public static void main(String[] args) {
        Set<Customer> customers = new HashSet<>();

        customers.add(new Customer(101,"Jitendra"));
        customers.add(new Customer(102,"Mahendra"));
        customers.add(new Customer(103,"Ajeet"));
        customers.add(new Customer(104,"Ramesh"));


        // Adding duplicate customer record
        customers.add(new Customer(101,"Jitendra"));

        System.out.println("\nCustomer Set :"+customers);

    }
}
