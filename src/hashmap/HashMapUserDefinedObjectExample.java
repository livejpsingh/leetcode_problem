package hashmap;

import java.util.HashMap;
import java.util.Map;

public class HashMapUserDefinedObjectExample {

    public static void main(String[] args) {
        Map<Integer, Employee> employeeMap = new HashMap<>();

        employeeMap.put(1001,new Employee(1001,"Jitendra","Greater Noida"));
        employeeMap.put(1002,new Employee(1002,"Mahendra","Noida"));
        employeeMap.put(1003, new Employee(1003,"Vivek", "New Delhi"));


        System.out.println(employeeMap);

        System.out.println("KeySet"+employeeMap.entrySet());

        System.out.println("EntrySet"+employeeMap.keySet());

    }
}
