/**
	Program to get an integer input from the user, and display it on a single line
	in a digital clock style String printed format.
	
	@version 2019-09-17-2035

*/

import java.util.Scanner;

public class DigitalClock{

	public static void main(String[] args) {
		
		Scanner keybd = new Scanner(System.in);
		
		System.out.print("Enter an integer: ");
		
		/**
			The number entered is taken in as an int (ignoring whitespace but also preceding zeroes)
		*/
		int numEntered = keybd.nextInt();
		
		/**
			Number is converted to a string to access the front elements easily (this avoids convoluted
			methods to get the first digit with multiple modulo 10 operations, etc)
		*/
		String numString = "" + numEntered;
		
	
		/**
			Turning the number string into an array of individual digits to process separately for
			printing
		*/
		int[] numList = new int[(numString.length())];		
		for (int i = 0; i < numString.length(); i++) {
			int tempInt = Integer.parseInt( numString.substring(i,i+1) );			
			numList[i] = tempInt;		
		}
		
		/**
			Creating Strings to hold first, second etc lines of DIFFERENT numbers together
		*/
		String firstLine = "";
		String secondLine = "";
		String thirdLine = "";
		String fourthLine = "";
		String fifthLine = "";
		
		/**
			Loop through the number array, call the method to get the digital print output for each number
			and concatenate it to a string for whole line output later.
		*/
		for (int i = 0; i < numString.length(); i++) {

			String temp = getNumLines(numList[i]);

			/**
				Splits the temporary single-line digital number string into separate lines and concatenates them
				with their respective line full list of numbers print string.
			*/
			firstLine += (" " + temp.substring(0, 4) );
			secondLine += (" " + temp.substring(4, 8) );
			thirdLine += (" " + temp.substring(8, 12) );
			fourthLine += (" " + temp.substring(12, 16) );
			fifthLine += (" " + temp.substring(16, 20) );

		}
			
		/**
			Output the joined-up strings of each elements first line, second line and so on
		*/
		System.out.println(firstLine);
		System.out.println(secondLine);
		System.out.println(thirdLine);
		System.out.println(fourthLine);
		System.out.println(fifthLine);
		
		
		
	
	}
	
	
	/**
		Creates a digital clock style console output String from a given number.
		
		@param num The integer number to convert to a String digital clock style output.
		@return String The number output in a single line. Will not print properly without printNumLines method.
	*/
	public static String getNumLines(int num) {
		
		
		/**
			Contains all the components of a digital clock face.
			These can be combined, line by line, to represent
			each unique number digit from 0 to 9.
		*/
		String bar = 		" -- ";
		String bothSides = 	"|  |";
		String leftSide = 	"|   ";
		String rightSide =	"   |";
		String blank =		"    ";
	
		//the temporary String to return, later containing all the lines
		//of the display in sequential order
		String lines = "";
		
		
		//print line 1
		if(num == 1 ||num == 4) {
			lines += blank;
		} else {
			lines += bar;
		}
		
		//print line 2
		if(num == 0 || num == 4 || num == 8 || num == 9 ) {
			lines += bothSides;
		} else if (num == 1 || num == 2 || num == 3 || num == 7) {
			lines += rightSide;	
		} else {
			lines += leftSide;
		}
		
		//print line 3
		if(num == 0 || num == 1 || num == 7) {
			lines += blank;	
		} else {
			lines += bar;
		}
		
		//print line 4
		if (num == 2) {
			lines += leftSide;
		} else if (num == 0 || num == 6 || num == 8) {
			lines += bothSides;	
		} else {
			lines += rightSide;	
		}
		
		//print line 5
		if (num == 1 || num == 4 || num == 7) {
			lines += blank;	
		} else {
			lines += bar;
		}
		
		return lines;
	}

	
	/**
		Stand-alone method to print out the number lines string.
		
		@param numLines The single-line String digital clock style representation of a number provided by getNumLines()
	*/
	public static void printNumLines(String numLines) {
		
		System.out.println( numLines.substring(0, 4) );
		System.out.println( numLines.substring(4, 8) );
		System.out.println( numLines.substring(8, 12) );
		System.out.println( numLines.substring(12, 16) );
		System.out.println( numLines.substring(16, 20) );
		
	}


}