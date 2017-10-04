
package edu.team3021.restaurant;

//import java.util.Scanner (scanner)

import java.util.Scanner;

public class Restaurant {
	
	public static void main (String args[]){
		
		//run print line; request resturant name
		
		System.out.println("Hello, please input the name of your restaurant.");
		
		//run scanner
		
		Scanner scanner = new Scanner( System.in );
		
		String restaurantName = scanner.nextLine();
		
		//close scanner
		
		scanner.close();
	
		System.out.println("Thank you, welcome to " + restaurantName);
	}
	
}
