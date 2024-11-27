package application;

public class Customer {
	private String fullName;
	private String companyName;
	private String emailAddress;
	private String telephoneNumber;
	public Customer(String fullName, String companyName, String emailAddress, String telephoneNumber) {
		super();
		this.fullName = fullName;
		this.companyName = companyName;
		this.emailAddress = emailAddress;
		this.telephoneNumber = telephoneNumber;
	}
	public String getFullName() {
		return fullName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	
	
}
