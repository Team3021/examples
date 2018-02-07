package edu.team3021.restaurant;

import java.util.Scanner;

public class RestuarantAppCommandLineInput {
	
	// member attributes
	Scanner scanner = new Scanner( System.in );
	
	// member methods
	
	String getUserInput(String question) {
		System.out.println(question);
		
		String answer = scanner.nextLine();
		
		return answer;
	}

	
	// TODO Add a way to close the scanner
}
