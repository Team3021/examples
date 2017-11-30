package edu.team3021.restaurant;


public class RestaurantApp {

	public static void main (String args[]) {
		
		RestuarantAppCommandLineInput input = new RestuarantAppCommandLineInput();
		
		System.out.println("Hello");
		
		// create a restaurant object
		// Type <<variable>> = constructor method of a class
		Restaurant restaurant = new Restaurant();
		
		// Get the restaurant name from user
		String restaurantName = input.getUserInput("Please input the name of your restaurant:");
		restaurant.setName(restaurantName);
		
		// Get the restaurant owner's name from user
		String ownersName = input.getUserInput("Please input the owner's name of the restaurant.");
		restaurant.setOwnersName(ownersName);
		
		System.out.println("Thank you, welcome to " + restaurant.getName());
		System.out.println("Restaurant owner is: " + restaurant.getOwnersName());
		
		ResturantAppOrder order = new ResturantAppOrder();
		order.OrderCreator();
		order.getOrder();
		
		ResturantOrderTotal orderTotal = new ResturantOrderTotal();
		orderTotal.findTotal(order.getOrderList());
		
	}
	
}
