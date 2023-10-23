package ArrayListExample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ArrayListSyncronizedExample {

    public static void main(String[] args) {

        List<Integer> safeArrayList = Collections.synchronizedList(new ArrayList<>());

        safeArrayList.add(1);
        safeArrayList.add(2);
        safeArrayList.add(3);


        // Create a thread pool of size 10

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // create a Runnable task that increments each element of the ArrayList by one

        Runnable task=()->{
            incrementArrayList(safeArrayList);
        };

        // SUbmit the task to the executor service 100 times
        // All the tasks will modify the ArrayLIst concurrently
        for (int i=0;i<100;i++){
            executorService.submit(task);
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private static void incrementArrayList(List<Integer> safeArrayList) {

        synchronized (safeArrayList){
            for ( int i=0;i<safeArrayList.size();i++){
                Integer value =safeArrayList.get(i);
                safeArrayList.set(i,value+1);
            }
        }
    }
}
