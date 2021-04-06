package edu.brown;

import edu.brown.providej.runtime.ProvideJUtil;
import edu.brown.providej.runtime.types.Nullable;

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
        for (Json3.Json3__courses course : data3.getCourses()) {
            System.out.println("\tname: " + course.getName() + ", grade: " + course.getGrade());
        }

        // Print all JSONs.
        System.out.println("");
        System.out.println(ProvideJUtil.toJSON(Json1.DATA));
        System.out.println(ProvideJUtil.toJSON(Json2.DATA));
        System.out.println(ProvideJUtil.toJSON(Json3.DATA));
        System.out.println(ProvideJUtil.toJSON(Json4.DATA));
        System.out.println(ProvideJUtil.toJSON(Json5.DATA));
        System.out.println(ProvideJUtil.toJSON(Json6.DATA));
        System.out.println(ProvideJUtil.toJSON(Json7.DATA));
        System.out.println(ProvideJUtil.toJSON(Json8.DATA));
        System.out.println(ProvideJUtil.toJSON(Json9.DATA));
        System.out.println(ProvideJUtil.toJSON(Json10.DATA));
    }
}