package application;

import java.util.List;

public class Agency {
	//private List<Agent> agents;
	private String city;
	private String agencyType;
	
	public Agency(String city, String agencyType) {
        this.city = city;
        this.agencyType = agencyType;
    }

    public String getCity() {
        return city;
    }

    public String getAgencyType() {
        return agencyType;
    }

    @Override
    public String toString() {
        return "Agency{" +
                "city='" + city + '\'' +
                ", agencyType='" + agencyType + '\'' +
                '}';
    }

}
