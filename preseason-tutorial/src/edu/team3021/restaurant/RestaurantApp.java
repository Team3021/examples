
package edu.team3021.restaurant;

//import java.util.Scanner (scanner)

import java.util.Scanner;

public class RestaurantApp {

	public static void main (String args[]) {
		
		//run print line; request resturant name
		
		System.out.println("Hello, please input the name of your restaurant.");
		
		
		//run scanner
		Scanner scanner = new Scanner( System.in );
		
		String restaurantName = scanner.nextLine();
		
		//close scanner
		scanner.close();

		
		
		
		// create a restaurant object
		// Type <<variable>> = constructor method of a class
		Restaurant restaurant = new Restaurant();
		
		// giving a string object to the restaurant object
		restaurant.setName(restaurantName);
		
		System.out.println("Thank you, welcome to " + restaurantName);
		System.out.println("Thank you, welcome to " + restaurant.getName());
	}
	
}
