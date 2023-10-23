package hashmap;

import java.util.HashMap;
import java.util.Map;

public class HashmapAccessKeyExample {

    public static void main(String[] args) {

        Map<String, String> userCityMapping = new HashMap<>();

        // check if a HashMap is empty
        System.out.println("is userCityMapping emplty ? : "+ userCityMapping.isEmpty());

        userCityMapping.put("Jitendra","Greater Noida");
        userCityMapping.put("Mahendra","New Delhi");
        userCityMapping.put("Ajeet","Noida");


        System.out.println("userCityMapping HashMap : "+ userCityMapping);

        // find the size of a HashMap

        System.out.println("We have the city information of "+ userCityMapping.size()+"users");

        String userName ="Ajeet";

        // check if a key exists in the HashMap

        if(userCityMapping.containsKey(userName)){
            //get the value assigned to a given key in the HashMAp

            String city = userCityMapping.get(userName);
            System.out.println(userName+" lives in "+ city);
        } else {
            System.out.println("City details not found for user "+userName);
        }


        // Modify the value assigned to an existing key
        userCityMapping.put(userName,"Gurgaon");
        System.out.println(userName+" moved to a new city "+ userCityMapping.get(userName)+", New userCityMapping :"+ userCityMapping);

        //the get() method return `null` if the specidifeied  key was not fund in the HashMap
        System.out.println("Jitendra's city: "+ userCityMapping.get("Jitendra"));

    }
}
