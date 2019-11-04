/**	This program uses a Monte Carlo simulation to calculate
*	the number of times a triangle can be created, given
*	a piece of spaghetti broken in two random places, with
*	three pieces being made.
*
*	To make it more interesting, a certain percentage of
*	the longest piece in this program may be "nibbled" off.
*/

import java.util.Scanner;

public class MonteSpag {

	public static void main(String[] args) {
	
		Scanner keybd = new Scanner(System.in);
		
		int nibbleInt = keybd.nextInt();
		double nibblePC = (double) nibbleInt / 100.0;
		
		System.out.println(nibblePC);
		
		//arbitrary number of times to run monte carlo
		//randomization simulation
		int numOfChecks = 1000000;
		
		//count number of valid triangles
		int isValidTriangle = 0;
		
		//The Monte Carlo simulation
		//Run repeated checks with randomly generated
		//numbers within the range and find the
		//percentage of valid triangles.
		for(int i = 0; i < numOfChecks; i++) {
					
			double break1 = Math.random();
			double break2 = Math.random();
			
			double length = 1.0;
			
			//first piece is the smallest point
			// - first break
			//		~~SPAGHETTIVISION~~
			//		=====|--------|---
			double piece1 = Math.min(break1, break2);			
			
			//second piece is the next point,
			//minus the first piece
			//		~~SPAGHETTIVISION~~
			//		-----|========|---
			double piece2 = Math.max(break1, break2) - piece1;
			
			//third piece is remaining left over
			//		~~SPAGHETTIVISION~~
			//		-----|--------|===
			double piece3 = 1.0 - piece1 - piece2;
			
			//need to find the longest side
			double longPiece = Math.max(piece1, Math.max(piece2, piece3));
			
			double otherTwoSides = 1.0 - longPiece;
			
			//Taking a nibble from the long piece will
			//*always* make it more likely a triangle
			//will be made, so this is the only condition
			//needed to be checked.
			//A corollary to this is that the MAX nibble
			//is the most advantageous.
			//So the longPiece loses that percentage
			//of its length here.
			longPiece = longPiece - (longPiece * nibblePC);
			
			
			if(otherTwoSides > longPiece ) {
				isValidTriangle++;	
			}
		
		}
		
		//the percent of valid trianges made, as a double for division
		double pcValidTriangles = (isValidTriangle * 100.0) / numOfChecks;
		System.out.println(pcValidTriangles);
		
		//converted back to an int for cleaner output,
		//and rounded for accuracy
		int validTriangles = (int) (Math.round(pcValidTriangles));
			
		System.out.println(validTriangles);
	
	
	}
	
}