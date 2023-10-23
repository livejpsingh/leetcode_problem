package hashmap;

import java.util.HashMap;
import java.util.Map;

public class HasMapKeyRemoveExample {
    public static void main(String[] args) {
        Map<String, String> employeeEmployerMapping = new HashMap<>();
        employeeEmployerMapping.put("Jitendra","Impiger Technologies Pvt Ltd");
        employeeEmployerMapping.put("Mahendra","HCL Technologies Pvt Ltd");
        employeeEmployerMapping.put("Ajeet", "Blaclberry");
        employeeEmployerMapping.put("Inderjeet","Taaza Infotech Pvt Ltd");


        System.out.println("Employee Employer Mappping : "+ employeeEmployerMapping);

        //Remove a key from the HashMap
        // Ex - Unfortunately , JItendra Resigned from Impiger Technology . Let's remove him from the mapping

        String employee="Jitendra";
        String employer=employeeEmployerMapping.remove(employee);

        System.out.println("Resign Employee Mapping (" + employee+ "=>" +employer+" ) resigned from company");


        //remove a key from the HashMap only if it is mapped to given value
        //ex employee "JItendra" only if He is contract to empployeed with Impiger

        employer=employeeEmployerMapping.remove("Jitendra");
        if (employer==null){
            System.out.println("Looks like Jitendra is not employeed with anyone");
        }else{
            System.out.println("Remove Jitendra and his emplyer from the mapping");
        }



    }
}
