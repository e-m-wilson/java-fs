public class Hello {

    public static void main(String[] args){

      

        class Person {
            String name;
        }
        Person a = new Person();
        a.name = "Alice";

        Person b = a;

        System.out.println(a == b);

        Person c = new Person(); 
        Person d = new Person(); 
        System.out.println(c == d);
        System.out.println(c.equals(d)); 

        double price = 19.98765;

        System.out.printf("Price: $%.2f%n", price);

    }
}