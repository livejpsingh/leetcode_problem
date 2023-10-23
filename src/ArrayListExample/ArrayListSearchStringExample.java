package ArrayListExample;

import java.util.ArrayList;
import java.util.List;

public class ArrayListSearchStringExample {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Jitendra");
        names.add("Mahendra");
        names.add("Vivek");
        names.add("Ajeet");
        names.add("Inderjeet");
        names.add("Shubham");


        // check name avaialble in List
        System.out.println("Name os List contain \" Jitendra\"?:"+names.contains("Jitendra"));

        // find the index of the first occurence of an element in an ArrayList
        System.out.println("indexOf \"Manhendra\":"+names.indexOf("Mahendra"));
        System.out.println("indexOf \"Vivek\":"+names.indexOf("Vivek"));

        // find the index of the first occurence of an element in an ArrayList
        System.out.println("indexOf \"Inderjeet\":"+names.lastIndexOf("Inderjeet"));
        System.out.println("indexOf \"Shubham\":"+names.lastIndexOf("Shubham"));


    }
}
