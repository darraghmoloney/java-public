package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileRead {

    public static ArrayList<String> read(String filepath) {

        String nextLine = new String();
        ArrayList<String> textArray = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            while((nextLine = br.readLine()) != null) {
                textArray.add(nextLine);
            }
        }
        catch (FileNotFoundException fnf) {
            System.out.println("File " + filepath + "not found");
        }
        catch (IOException ioe) {
            System.out.println("IO Exception reading the file at " + filepath);
            ioe.printStackTrace();
        }

        return textArray;
    }

}
