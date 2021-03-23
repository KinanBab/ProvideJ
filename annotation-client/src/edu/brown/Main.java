package edu.brown;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello");
        TestProvider test = new TestProvider();
        test.setId(0);
        test.setName("Kinan");
        System.out.println("Test: " + test.getId() + ", " + test.getName());
    }
}