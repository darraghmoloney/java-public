import java.util.Scanner;

public class PrimeIt {

	public static void main(String[] args) {
	
		Scanner keybd = new Scanner(System.in);
		
		//Takes two numbers as the range of nums to check
		int firstNum = keybd.nextInt();
		int secondNum = keybd.nextInt();
		
		int smallNum = Math.min(firstNum, secondNum);
		int bigNum = (firstNum + secondNum) - smallNum;
		
		for(int i = smallNum; i <= bigNum; i++) {
			
			//Easier to assume prime is true and change
			//it later - if all 0 remainder tests FAIL,
			//number is prime
			boolean isPrime = true;
			
			//Optimization - only test up to the
			//square root of the number, inclusive
			//By defintion no divisor could be larger than
			//that
			for(int j = 2; j <= Math.sqrt(i); j++)	{
				
				//Check for 0 remainder & break out
				//to next number if true - saves time
				if(i % j == 0) {
					isPrime = false;
					break;	
				}
				
			}
			
			//Only print the prime numbers
			//Could also be put in an array, etc.
			if(isPrime) {
				System.out.print(i + " ");	
			}
			
		}
		
		System.out.println();
	
	
	}

}