import java.util.Arrays;
import java.util.Scanner;

public class LuhnsV2 {

	public static void main(String[] args) {
	
		Scanner keybd = new Scanner(System.in);
		
		System.out.print("Enter your credit card number: ");
		
		String numStr = keybd.nextLine();
		
		

		
		int[] numArr = new int[numStr.length()];
		
		for(int i = 0; i < numStr.length(); i++) {
			
			numArr[i] = Integer.parseInt( numStr.substring(i, i+1) );
			
		}
		
		System.out.println( Arrays.toString(numArr) );
		
		int count = 0;
		int total = 0;
		
		for(int i = (numArr.length-1); i >=0; i--) {
		
			int temp = numArr[i];
			
			count++;
			
			if(count % 2 == 0) {
				temp *= 2;
				if(temp >= 10) {
					temp -= 9;						
				}
			} 			
			
			total += temp;
			
			
		}
		
		
		System.out.println("The checksum is " + total + ".");

		if(total % 10 == 0) {
		    System.out.println("This credit card number is valid.");
		} else {
		    System.out.println("This credit card number is not valid.");
		}
		
	}
		
}
