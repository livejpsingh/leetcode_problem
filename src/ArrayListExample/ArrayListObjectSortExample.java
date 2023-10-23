package ArrayListExample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Person{
    private  String name;
    private  Integer age;

    public Person(String name, Integer age){
        this.name=name;
        this.age=age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

public class ArrayListObjectSortExample {

    public static void main(String[] args) {
        List<Person> list= new ArrayList<>();

        list.add(new Person("Jitendra", 35));
        list.add(new Person("Mahendra",34));
        list.add(new Person("Vivek", 25));

        System.out.println(list);

        list.sort((list1,list2)->{
            return list1.getAge()-list2.getAge();
                }
        );

        list.sort(Comparator.comparingInt(Person::getAge));

        System.out.println("Sorted List by age "+list);

        Collections.sort(list, Comparator.comparing(Person::getName));

        System.out.println("Sorted LIst by Name "+list);

    }
}
