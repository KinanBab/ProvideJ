package edu.brown;

import edu.brown.providej.runtime.ProvideJUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Test {
    public static final Object[] OBJECTS = {
            Json1.DATA, Json2.DATA, Json3.DATA, Json4.DATA, Json5.DATA, Json6.DATA, Json7.DATA, Json8.DATA, Json9.DATA,
            Json10.DATA, Json11.DATA, Json12.DATA, Json13.DATA
    };

    public static void main(String[] args) throws IOException {
        String prefixDirectory = args.length == 0 ? "" : args[0];
        try {
            for (int i = 0; i < Test.OBJECTS.length; i++) {
                Object obj = Test.OBJECTS[i];
                String outputFilePath = prefixDirectory + "file" + (i + 1) + ".json";
                File outputFile = new File(outputFilePath);
                FileWriter writer = new FileWriter(outputFile);
                writer.write(ProvideJUtil.toJSON(obj));
                writer.close();
            }
        } catch (IOException ex) {
            System.err.println("Could not open output file");
            ex.printStackTrace();
            throw ex;
        }
    }
}
