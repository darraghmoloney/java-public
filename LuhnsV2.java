/**  This program uses the famous Luhn's Algorithm
*    to determine if a credit card number is valid
*    or not.
*
*    The algorithm works by calculating the sum of
*    all odd digits from the right and adding them
*    to the sum of doubled even digits (the 
*    doubled digit is reduced to a single number
*    if it is greater than 10 before being added).
*
*    If the total sum of single and doubled digits
*    is a multiple of ten, the credit card number 
*    is valid.
*/

import java.util.Arrays;
import java.util.Scanner;

public class LuhnsV2 {

	public static void main(String[] args) {
	
		Scanner keybd = new Scanner(System.in);
		
		System.out.print("Enter your credit card number: ");
		
		//number is taken in as a string to allow
		//unlimited length and to use string length
		//method to create an array of the correct
		//size for the number
		String numStr = keybd.nextLine();
			

		//putting numbers into array for easy processing
		//and to preserve the original number
		int[] numArr = new int[numStr.length()];
		
		for(int i = 0; i < numStr.length(); i++) {
			
			numArr[i] = Integer.parseInt( numStr.substring(i, i+1) );
			
		}
		
		System.out.println( Arrays.toString(numArr) );
		
		//this counter variable tells us whether
		//the currently being checked number is an
		//odd or even one from the right.
		//odd numbers are added as is, even
		//numbers are doubled and reduced to a single
		//digit.
		int count = 0;
		int total = 0;
		
		//this loop starts at the end of the array
		//and moves to the front - this is necessary
		//for algorithm to work correctly
		for(int i = (numArr.length-1); i >=0; i--) {
		
			//take current array number as temp var
			int temp = numArr[i];
			
			count++;
			
			//even number check
			if(count % 2 == 0) {
				temp *= 2;
				
				//reduce nums over 10 to a
				//single digit
				if(temp >= 10) {
					temp -= 9;	//this is the same as adding
							//the 2 individual digits together										
				}
			} 			
			
			//add current number to running total
			total += temp;
			
			
		}
		
		
		System.out.println("The checksum is " + total + ".");

		//total sum must end in 0 to be a valid number
		// - that is, it is a direct multiple of 10
		//and has no remainder when divided by it
		if(total % 10 == 0) {
		    System.out.println("This credit card number is valid.");
		} else {
		    System.out.println("This credit card number is not valid.");
		}
		
	}
		
}
