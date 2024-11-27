package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MyApp extends Application {
    private static final String DATABASE_URL = "jdbc:sqlite:C:/sqlite/marketing.db";

    @Override
    public void start(Stage primaryStage) {
        showMainWindow(primaryStage);
    }

    private void showMainWindow(Stage stage) {
        stage.setTitle("Welcome!");

        Label welcomeLabel = new Label("Welcome to the Advertising Agency!");
        Button viewLogInButton = new Button("Log in");
        Button viewNotLogInButton = new Button("Continue without log in");

        VBox root = new VBox(10, welcomeLabel, viewLogInButton, viewNotLogInButton);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(root, 300, 200);

        stage.setScene(scene);
        stage.show();

        viewLogInButton.setOnAction(e -> {
            stage.close(); 
            showLogInWindow();
        });
        viewNotLogInButton.setOnAction(e -> {
            stage.close(); 
            showAgencyInfoWindow(); 
        });

    }

    private void showLogInWindow() {
        Stage logInStage = new Stage();
        logInStage.setTitle("User Authentication");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        Label messageLabel = new Label();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);
        gridPane.add(messageLabel, 1, 3);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            Customer customer = authenticateUser(username, password);
            if (customer != null) {
                messageLabel.setText("Login successful!");
                logInStage.close(); 
                showCustomerWindow(customer);
            } else {
                messageLabel.setText("Invalid username or password!");
            }
        });

        Scene scene = new Scene(gridPane, 400, 200);
        logInStage.setScene(scene);
        logInStage.show();
    }

    private Customer authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT full_name, company_name, email, tel FROM Customers WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getString("full_name"),
                    rs.getString("company_name"),
                    rs.getString("email"),
                    rs.getString("tel")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }


    private void showCustomerWindow(Customer customer) {
        Stage customerStage = new Stage();
        customerStage.setTitle("Welcome Back!");
        Label welcomeLabel = new Label("Welcome, " + customer.getFullName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button viewPriceButton = new Button("View Price List");
        Button viewInformButton = new Button("View My Information");
        /////ще не реалізовано
        Button viewContactsButton = new Button("View My Contracts");
        Button logoutButton = new Button("Logout");
        viewPriceButton.setOnAction(e -> {
            customerStage.close(); 
            showPriceListWindow(customerStage); 
        });
        viewInformButton.setOnAction(e -> {
            customerStage.close(); 
            showCustomerIngormationWindow(customer);
        });
        logoutButton.setOnAction(e -> {
            customerStage.close(); 
            Stage primaryStage = new Stage();
            start(primaryStage); 
        });

        VBox root = new VBox(10, welcomeLabel, viewPriceButton, viewInformButton, viewContactsButton, logoutButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(root, 400, 200);
        customerStage.setScene(scene);
        customerStage.show();
    }



    private void showPriceListWindow(Stage previousStage) {
        Stage priceListStage = new Stage();
        priceListStage.setTitle("Price List");

        Label titleLabel = new Label("Price List of Advertising Services");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10;");

        List<AdvertisingService> services = AdvertisingService.loadFromDatabase(DATABASE_URL);

        VBox serviceList = new VBox(10);
        serviceList.setPadding(new Insets(10));

        for (AdvertisingService service : services) {
            HBox serviceBox = new HBox(10);
            serviceBox.setPadding(new Insets(10));
            serviceBox.setStyle(
                "-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; " +
                "-fx-background-color: #f9f9f9; -fx-padding: 10;"
            );

            VBox leftBox = new VBox(5);
            Label serviceTitleLabel = new Label(service.getTitle());
            serviceTitleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            Label serviceTypeLabel = new Label("Type: " + service.getTypeOfService());
            serviceTypeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
            leftBox.getChildren().addAll(serviceTitleLabel, serviceTypeLabel);

            VBox rightBox = new VBox(5);
            Label servicePriceLabel = new Label(String.format("Price: %.2f UAH", service.getPrice()));
            servicePriceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2a9d8f;");
            Label agencyDetailsLabel = new Label(
                "Agency: " + service.getAgency().getCity() + " (" + service.getAgency().getAgencyType() + ")"
            );
            agencyDetailsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
            rightBox.getChildren().addAll(servicePriceLabel, agencyDetailsLabel);

            serviceBox.getChildren().addAll(leftBox, rightBox);
            serviceBox.setHgrow(leftBox, Priority.ALWAYS);

            serviceList.getChildren().add(serviceBox);
        }

        ScrollPane scrollPane = new ScrollPane(serviceList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Button backButton = new Button("Back");
        backButton.setStyle(
            "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-color: #2a9d8f; -fx-text-fill: white;"
        );
        backButton.setOnAction(e -> {
            priceListStage.close(); 
            if (previousStage != null) {
                previousStage.show(); 
            }
        });

        VBox root = new VBox(20, titleLabel, scrollPane, backButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center; -fx-background-color: #ffffff;");

        Scene scene = new Scene(root, 800, 600);
        priceListStage.setScene(scene);

        if (previousStage != null) {
            previousStage.hide();
        }
        priceListStage.show();
    }



    private void showAgencyInfoWindow() {
        Stage agencyInfoStage = new Stage();
        agencyInfoStage.setTitle("About Our Advertising Agency");

        Label titleLabel = new Label("About Our Advertising Agency");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label(
        	    "Our company provides comprehensive advertising services, with contracts with transport agencies, " +
        	    "metro systems, and municipal authorities for placing ads on vehicles and city streets. " +
        	    "We also have agreements for air time on radio and TV. " +
        	    "Our network includes several agencies in different cities, where local agencies can sign contracts on behalf of " +
        	    "the company. We also have designers who create the visual appearance of advertisements."
        	);

        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        Button viewPriceButton = new Button("View Price List");
        Button signInButton = new Button("Sign In");
        Button backButton = new Button("Back");

        viewPriceButton.setOnAction(e -> {
            agencyInfoStage.close();
            showPriceListWindow(agencyInfoStage); 
        });

        //////кнопка лише для вигляду, треба зробити реалізацію реєстрації
        /*signInButton.setOnAction(e -> {
            agencyInfoStage.close();
            showLogInWindow();
        });*/

        backButton.setOnAction(e -> {
            agencyInfoStage.close(); 
            Stage primaryStage = new Stage();
            start(primaryStage); 
        });

        VBox root = new VBox(10, titleLabel, descriptionLabel, viewPriceButton, signInButton, backButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(root, 600, 400);
        agencyInfoStage.setScene(scene);
        agencyInfoStage.show();
    }

    private void showCustomerIngormationWindow(Customer customer) {
        Stage dealsStage = new Stage();
        dealsStage.setTitle("My Personal Information");

        Label header = new Label("Personal Information for " + customer.getFullName());
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label dealsInfo = new Label("Company: " + customer.getCompanyName() + "\n" +
                                    "Email: " + customer.getEmailAddress() + "\n" +
                                    "Tel: " + customer.getTelephoneNumber());

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            dealsStage.close();
            showCustomerWindow(customer);
        });

        VBox root = new VBox(10, header, dealsInfo, backButton);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(root, 400, 200);
        dealsStage.setScene(scene);
        dealsStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
