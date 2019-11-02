/**	This program takes in an array of numbers
*	and uses a recursive method to find the
*	greatest common demoninator of all of them
*	using Euclid's algorithm (but with division).
*	
*	- Euclid's algorithm works by dividing the big
*	number by the smaller one. 
*
*	- If there is a remainder, the smaller number becomes
*	the new big number, the remainder becomes the new
*	small number, and the big number is divided by the
*	small one again.
*
*	- This process repeats until there is no remainder.
*
*	- When there is no remainder, the two numbers
*	greatest common divisor was found and this number
*	is returned.
*
*	This process is perfect for recursion - the division
*	continues being sent into a method unless the remainder 
*	is zero.
*/

public class EuclidGCD {
	
	public static void main(String[] args) {
		
		int[] arr = {24, 46, 156, 369};
		int gcd = 1;
		
		//stop at 1 from the end, as this
		//loop processes pairs of values
		//and needs one number following the current
		//one
		for(int i = 0; i < arr.length-1; i++) {
			gcd = recursiveGCD(arr[i], arr[i+1]);	
		}
	
		System.out.println( gcd );
	
	}
	
	public static int recursiveGCD(int a, int b) {
	
		//must find out which number is bigger
		//for algorithm to work
		int bigNum = (a > b) ? a : b;
		int smallNum = (bigNum == b) ? a : b;
		
		//base case - no remainder means
		//a clean division and the smaller
		//number(divisor) can be returned as
		//the result
		if(bigNum % smallNum == 0) {
			return smallNum;
		}
		
		//the big num becomes the original small num.
		//the other num is the remainder of
		//initial bigNum divided by smallNum		
		int temp = smallNum;
		smallNum = bigNum % smallNum;
		bigNum = temp;
		
		//repeat the process while remainder is not 0
		return recursiveGCD(smallNum, bigNum);
	
	
	}

}