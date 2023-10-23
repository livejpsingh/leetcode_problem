package hashmap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HashMapIterateOverMapExample {
    public static void main(String[] args) {

        Map<String,Double> employeeSalary = new HashMap<>();
        employeeSalary.put("Jitendra",76000.00);
        employeeSalary.put("Mahendra",120000.00);
        employeeSalary.put("Vivek",135000.00);
        employeeSalary.put("Ajeet",95000.00);

        System.out.println("====Interating over a HashMap using Java 8 forEach and Lambda ===");
        employeeSalary.forEach((employee, salary) -> {
            System.out.println(employee+" => "+salary);
        });


        System.out.println("\n=========== Iterating over the HashMap's entrySet using iterator()===");
        Set<Map.Entry<String,Double>> employeeSalaryEntries = employeeSalary.entrySet();
        Iterator<Map.Entry<String,Double>> employeeSalaryIterator = employeeSalaryEntries.iterator();
        while (employeeSalaryIterator.hasNext()){
            Map.Entry<String, Double> entry = employeeSalaryIterator.next();
            System.out.println(entry.getKey() + " => "+ entry.getValue() );
        }

        System.out.println("\n==============  Iterating over the HashMap's entrySet using Java 8 forEach and lanbda =====");
        employeeSalary.entrySet().forEach(
                entry -> {
                    System.out.println(entry.getKey()+ "=> "+entry.getValue());
                });

        System.out.println("\n===== Iterating over HashMap's entrySet using simple for-each loop======");
        for (Map.Entry<String, Double> entry : employeeSalary.entrySet()) {
            System.out.println(entry.getKey()+"=>"+entry.getValue());
        }

        System.out.println("\n====Iterating over HashMap's keySet ==");
        employeeSalary.keySet().forEach(
                employee->{
                    System.out.println(employee+"=>"+employeeSalary.get(employee));
                }
        );



    }
}
