/** This program creates a user-defined String stack class
*   which takes in a series of Directions as Strings.
*
*   It then returns the directions to get home.
*   It reverses the directions by popping them
*   off the stack, checking their value and changing it
*   to its opposite. For example, if you went North to
*   get there, you should go South to get back.
*
*   There is also a "Go Back" option, which will make
*   the previous direction be ignored.
*/

import java.util.Scanner;

/** Simple String stack which can be used to
*   easily reverse the order of inputted information.
*/
class StringStack {
    
    //This stack uses an array so the max size
    //of the stack must be known too to 
    //create an object.
    private String[] stack;
    private int top;
    private int maxSize;
    
    public StringStack(int maxSize) {
        this.maxSize = maxSize;
        top = -1;
        stack = new String[maxSize];
    }
    
    boolean isEmpty() {
        return top == -1;
    }
    
    boolean isFull() {
        return top == maxSize-1;
    }
    
    String peek() {
        if( !isEmpty() ) {
            return stack[top];
        }
        return "";
    }

    boolean push(String newString) {
        if( !isFull() ) {
            top++;
            stack[top] = newString;
            return true;
        }   
        return false;
    }
    
    boolean pop() {
        if( !isEmpty() ) {
            top--;
            return true;
        }
        return false;
    }
    
    
    public static String reverseDirections(String direction) {
        String revDir = "";
        
        String tempDir = direction.toLowerCase();
        
        switch(tempDir) {
            case "go north":
                revDir = "Go South \u2193";
                break;
            case "go south":
                revDir = "Go North \u2191";
                break;
            case "go east":
                revDir = "Go West \u2190";
                break;
            case "go west":
                revDir = "Go East \u2192";
                break;
            default:
                revDir = "Stand there and scratch your head, bewilderedly. ಠಿ_ಠ";
        }
        
        return revDir;

    }
    
    public static void printMenu() {
        System.out.println("-----------------------------------------------------");
        System.out.println("Enter the directions.\n");
        System.out.println("Type 'Go North', 'Go South', 'Go East' or 'Go West'.");
        System.out.println("Type 'Go Back' to cancel the previous instruction.");
        System.out.println();
        
    }

}


public class DirectionStack {
    
    public static Scanner keybd = new Scanner(System.in);

    public static void main(String[] args) {
        
        System.out.print( "Enter the number of directions: ");
        int numInstructions = Integer.parseInt(keybd.nextLine() );
        
        
        StringStack directions = new StringStack(numInstructions);
        
        StringStack.printMenu();
       
        
        for(int i = 0; i < numInstructions; i++) {
            
            System.out.print("Enter direction " + (i+1) + " of " + numInstructions + ": ");
            String nextInstruction = keybd.nextLine();
            
            if( nextInstruction.equalsIgnoreCase("Go Back") ) {
                directions.pop();   
            } else {
                directions.push(nextInstruction);
            }
            
        }
     
        
        System.out.println();
        
        System.out.println("-----------------------------------------------------");
        System.out.println("Here are your directions to return home: \n");
        
        while( !(directions.isEmpty()) ) {
            String temp = directions.peek();
            System.out.println( StringStack.reverseDirections(temp) );
            directions.pop();            
        }
        System.out.println("You made it! I hope... ");
    }

}
