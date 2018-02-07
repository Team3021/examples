package edu.team3021.restaurant;

import java.util.ArrayList;

public class ResturantAppOrder {

	ArrayList<String> list = new ArrayList<String>();
	RestuarantAppCommandLineInput cli = new RestuarantAppCommandLineInput();

	public void OrderCreator() {
		String order = "order";
		while(!(order.equals("0"))) {
			order = cli.getUserInput("what would you like to order? 0 to exit.");
			if (!(order.equals("0"))) {	list.add(order);
			System.out.println(order); 
			}		
		}
	}
	public void getOrder() {
		for(String a:list) {
			System.out.println(a);
		}
	}
	
	public ArrayList<String> getOrderList(){
		return list;
	}
}


