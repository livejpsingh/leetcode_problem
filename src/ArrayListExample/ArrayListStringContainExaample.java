package ArrayListExample;

import java.util.ArrayList;

public class ArrayListStringContainExaample {
    public static void main (String args[]){

        ArrayList arrayList = new ArrayList<>();
        arrayList.add("Jitendra");
        arrayList.add("Mahendra");
        arrayList.add("Suman");
        arrayList.add("Vivek");
        arrayList.add("Viaks");
        arrayList.add("Ratan");


        boolean checkName = arrayList.contains("Jitendra");

        if (checkName){
            System.out.println(arrayList.get(0)+" is Available in List");
        }
        else {
            System.out.println(arrayList.get(0)+" is not available in List");
        }

        checkName= arrayList.contains("Alokit");

        if(checkName){
            System.out.println("Alokit is available in List");
        }
        else {
            System.out.println("Alokit is not available in List");
        }


    }
}
