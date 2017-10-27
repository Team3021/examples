package edu.team3021.restaurant;


public class RestaurantApp {

	public static void main (String args[]) {
		
		RestuarantAppCommandLineInput input = new RestuarantAppCommandLineInput();
		
		String restaurantName = input.getUserInput("Hello, please input the name of your restaurant.");
		
		// create a restaurant object
		// Type <<variable>> = constructor method of a class
		Restaurant restaurant = new Restaurant();
		
		// giving a string object to the restaurant object
		restaurant.setName(restaurantName);
		
		System.out.println("Thank you, welcome to " + restaurantName);
		System.out.println("Thank you, welcome to " + restaurant.getName());
	}
	
}
