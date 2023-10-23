package ArrayListExample;

import java.util.ArrayList;

public class ArrayListNumContainExample {

    public static void main(String args[]){

        ArrayList<Integer> arr= new ArrayList<>();

        arr.add(10);
        arr.add(20);
        arr.add(30);
        arr.add(40);
        arr.add(50);

        boolean ans = arr.contains(20);

        if (ans){
            System.out.println("20 is avaiable in List");
        }
        else {
            System.out.println("20 is not avaiable in List");
        }

        ans= arr.contains(60);

        if (ans)
        {
            System.out.println(60+"is available in list");
        }
        else {
            System.out.println(60+"is does not conatian in list");
        }

    }
}
