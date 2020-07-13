package com.company;
import java.io.*;

public class FileIO {

    public String[] load(String file) {
        File aFile = new File(file);
        StringBuffer contents = new StringBuffer();
        BufferedReader input = null;

        try {
            input = new BufferedReader( new FileReader(aFile) );
            String line = null;
            while( (line = input.readLine()) != null ) {
                contents.append(line);
                contents.append( System.getProperty("line.separator") );
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println("File not found");
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.out.println("IO Exception");
            ex.printStackTrace();
        }
        finally {
            try{
                if(input != null) {
                    input.close();
                }
            }
            catch(IOException ex){
                System.out.println("IO Exception");
                ex.printStackTrace();
            }
        }
        String[] array = contents.toString().split("\n");
        for(String s: array) {
            s.trim();
        }
        return array;
    }

    public void save(String file, String[] array) {

        File aFile = new File(file);
        Writer output = null;
        try {
            output = new BufferedWriter( new FileWriter(aFile) );
            for(int i=0;i<array.length;i++){
                output.write( array[i] );
                output.write(System.getProperty("line.separator"));
            }
        }
        catch(FileNotFoundException fx) {
            System.out.println("File not found");
            fx.printStackTrace();
        }
        catch (IOException ix) {
            System.out.println("IO Exception");
            ix.printStackTrace();
        }
        finally {
            try {
                if (output != null) output.close();
            }
            catch (IOException ix) {
                System.out.println("IO Exception attempting to close file");
                ix.printStackTrace();
            }
        }
    }

}
