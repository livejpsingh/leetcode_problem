package hashmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashMapEntryKeySetValueExample {

    public static void main(String[] args) {

        Map<String, String> countryISOCodeMapping = new HashMap<>();

        countryISOCodeMapping.put("India","IN");
        countryISOCodeMapping.put("United States of America", "US");
        countryISOCodeMapping.put("Russia","RU");
        countryISOCodeMapping.put("Japan","JP");
        countryISOCodeMapping.put("China","CN");


        // HashMap's entry set

        Set<Map.Entry<String, String>> countryIsocodeEntries = countryISOCodeMapping.entrySet();
        System.out.println("CountryIsoCode entries : "+ countryIsocodeEntries);

        //HashMap's ket set
        Set<String> countries = countryISOCodeMapping.keySet();
        System.out.println("countries: "+countries);

        // HshMap's Values
        Collection<String> isoCodes =countryISOCodeMapping.values();
        System.out.println("isocodes: "+isoCodes);

    }
}
