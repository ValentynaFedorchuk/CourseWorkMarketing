package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdvertisingService {
    private String title;
    private double price;
    private String description;
    private TypeOfService typeOfService;
    private Agency agency;

    public AdvertisingService(String title, double price, String description, TypeOfService typeOfService, Agency agency) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.typeOfService = typeOfService;
        this.agency = agency;
    }

    public static List<AdvertisingService> loadFromDatabase(String databaseUrl) {
        List<AdvertisingService> services = new ArrayList<>();

        String query = "SELECT AdvertisingService.title, AdvertisingService.price, AdvertisingService.description, AdvertisingService.type_of_service,  Agency.city, Agency.agency_type FROM AdvertisingService INNER JOIN Agency ON AdvertisingService.agency_id = Agency.id";

        try (Connection connection = DriverManager.getConnection(databaseUrl);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                double price = resultSet.getDouble("price");
                String description = resultSet.getString("description");
                String typeOfServiceStr = resultSet.getString("type_of_service");
                String city = resultSet.getString("city");
                String agencyType = resultSet.getString("agency_type");
                Agency agency = new Agency(city, agencyType);
                TypeOfService typeOfService = TypeOfService.valueOf(typeOfServiceStr);
                AdvertisingService service = new AdvertisingService(title, price, description, typeOfService, agency);
                services.add(service);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return services;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public TypeOfService getTypeOfService() {
        return typeOfService;
    }

    public Agency getAgency() {
        return agency;
    }
}
