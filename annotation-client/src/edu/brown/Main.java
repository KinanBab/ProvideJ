package edu.brown;

public class Main {
    public static void main(String[] args) {
        // samples/data1.json
        Json1 data1 = Json1.DATA;
        System.out.println();
        System.out.println("Data1");
        System.out.println("id: " + data1.getId());
        System.out.println("name: " + data1.getName());
        System.out.println("height: " + data1.getHeight());

        // samples/data2.json
        Json2 data2 = Json2.DATA;
        System.out.println();
        System.out.println("Data2");
        System.out.println("id: " + data2.getId());
        System.out.println("name: " + data2.getName());
        System.out.println("Program:");
        System.out.println("\tdegree:" + data2.getProgram().getDegree());
        System.out.println("\tdepartment:" + data2.getProgram().getDepartment());
        System.out.println("\tyear:" + data2.getProgram().getYear());

        // samples/data3.json
        Json3 data3 = Json3.DATA;
        System.out.println();
        System.out.println("name: " + data3.getName());
        System.out.println("interests:");
        for (String interest : data3.getInterests()) {
            System.out.println("\t" + interest);
        }
        System.out.println("courses:");
        for (Json3.Courses course : data3.getCourses()) {
            System.out.println("\tname: " + course.getName() + ", grade: " + course.getGrade());
        }
    }
}