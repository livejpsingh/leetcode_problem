package ArrayListExample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayListNumItetratorRemoveExample {

    public static void main(String[] args) {

        List<Integer> numList = new ArrayList<>();

        numList.add(13);
        numList.add(24);
        numList.add(36);
        numList.add(54);

        Iterator<Integer> numInterator =  numList.iterator();

        while (numInterator.hasNext()){
            Integer number= numInterator.next();
            if (number % 2 !=0){
                numInterator.remove();
            }
        }

        System.out.println(numList);




    }
}
