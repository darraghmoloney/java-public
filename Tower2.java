/**	This program solves the overhanging blocks problem
*	(also known as the tower of Lire) where a stack
*	of objects is made such that it has a certain lean
*	over the side of a table. 
*
*	This program calculates the minimum height a tower would 
*	need to be in order to have a user-entered overhang by 
*	calculating the number of objects to stack in a loop and 
*	multiplying by the given width of the object to get the 
*	overhang, and then multiplying the number of objects by 
*	the thickness of each object to get the tower height.
*/


import java.util.Scanner;

public class Tower2 {
	
	public static void main(String[] args) {
		
		Scanner keybd = new Scanner(System.in);
		
		System.out.println("Enter the radius of the coin (mm, decimal number): ");
		double radius = keybd.nextDouble();
		
		System.out.println("Enter the thickness of the coin (mm, decimal number): ");
		double thickness = keybd.nextDouble();		
		
		System.out.println("Enter the desired overhang of the tower (mm, decimal number): ");
		double desiredOverhang = keybd.nextDouble();
		
		
		
		double diameter = radius * 2;
		
		double numBlocks;
		double currentOverhang = 0.0;
		
		//this loop will be very slow for large numbers.
		//an optimum solution would involve calculating the harmonic number
		//of a given number n
		for(numBlocks = 1.0; currentOverhang < desiredOverhang; numBlocks++) {
		
			double tempFactor = (1 / (2 * numBlocks)) * diameter;
			currentOverhang += tempFactor;
			
		}
		
		//System.out.println(currentOverhang);
		//System.out.println(numBlocks-1);
		
		double height = (numBlocks-1) * thickness;
		
		long heightLong = (long) height;
		System.out.println("The minimum height of your tower is " + heightLong);
		
	}
	
}
