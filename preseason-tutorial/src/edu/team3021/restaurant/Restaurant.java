
package edu.team3021.restaurant;

public class Restaurant {
	
	// member attributes
	private String name;
	private String ownersName;

	public String getName() {
		return name;
	}

	public void setName(String nameArg) {
		// storing the reference to the String object
		this.name = nameArg;
	}
	
	public String getOwnersName() {
		return ownersName;
	}

	public void setOwnersName(String ownersName) {
		this.ownersName = ownersName;
	}
	
}
