package test;

import gatewayServer.request_process_sevice.Factory.Factory;

import java.util.function.Function;

abstract class Fruit{
   abstract void print();
    static void staticPrint(){
        System.out.println("static RedApple");
    }
    static RedApple staticApple(){
        return new RedApple("ds");
    }
}

class Apple extends Fruit {

    public Apple(String s) {

    }
    @Override
    void print() {
        System.out.println("Apple");
    }
}

class Peach extends Fruit {

    public Peach(String s) {

    }
    @Override
    void print() {
        System.out.println("Peach");
    }
}

class RedApple extends Apple {

    public RedApple(String s) {
        super(s);

    }
    @Override
    void print() {
        System.out.println("RedApple");
    }

    static void staticPrint(){
        System.out.println("static RedApple");

    }
}


class FactoryTest {
    Factory<String, String, Fruit> frootFactory = new Factory<String, String, Fruit>();


    @org.junit.jupiter.api.Test
    void create() {

        Function<String ,Fruit> appleFunc = (s)-> new Apple(s);
        Function<String ,Fruit> peachFunc = (s)-> new Peach(s);
        Function<String ,Fruit> redAppleFunc = (s)-> new RedApple(s);

        frootFactory.add("Apple", appleFunc);
        frootFactory.add("Peach", peachFunc);
        frootFactory.add("RedApple", redAppleFunc);

        Fruit apple =  frootFactory.create("Apple", "sdffds");
        Fruit peach =  frootFactory.create("Peach", "sdfds");
        Fruit redApple =  frootFactory.create("RedApple", "dfsf");

        apple.print();
        peach.print();
        redApple.print();

       //
        // Function<String ,Fruit> staticRedAppleFunc = (o)-> Fruit.staticPrint(o);
        Function<String ,Fruit> staticRedAppleFunc = (s)-> Fruit.staticApple();

        frootFactory.add("new apple", redAppleFunc);

        Fruit some =  frootFactory.create("new apple", "ffdfds");

        some.print();

    }


}