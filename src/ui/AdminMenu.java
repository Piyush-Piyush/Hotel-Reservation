package ui;

import model.*;
import service.CustomerService;
import service.ReservationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import model.Customer;

import static service.ReservationService.reservationService;

public class AdminMenu {
    private static final String password = "123";

    static void displayMenu() {
        System.out.println("1. See all customers");
        System.out.println("2. See all rooms");
        System.out.println("3. See all reservations");
        System.out.println("4. Add a room");
        System.out.println("5. Back to main menu");
    }

    static void displayAllCustomers() {
        CustomerService customerService = CustomerService.getInstance();
        Collection<Customer> customers = customerService.getAllCustomers();
        if(customers.isEmpty()) {
            System.out.println("No customers details to display.");
            return;
        }
        System.out.println("Displaying all customers");
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    static void displayAllRooms() {
        ReservationService reservationService = ReservationService.getInstance();
        Collection<IRoom> rooms = reservationService.getAllRooms();
        if(rooms.isEmpty()) {
            System.out.println("No room available.");
            return;
        }
        for (IRoom room : rooms) {
            System.out.println(room);
        }
    }

    static void displayAllReservations() {
        ReservationService reservationService = ReservationService.getInstance();
        reservationService.printAllReservations();
    }

    static void addRoom(Scanner sc) {
        List<IRoom> rooms = new ArrayList<>();
        Collection<IRoom> allRooms = reservationService.getAllRooms();

        while (true) {
            String roomNumber;
            while (true) {
                System.out.println("Enter room number:");
                roomNumber = sc.nextLine().trim();

                if (!roomNumber.isEmpty()) {
                    break;
                } else {
                    System.out.println("Room number cannot be empty. Please enter a valid room number.");
                }
            }

            boolean roomExists = false;
            for(IRoom room : allRooms) {
                if(room.getRoomNumber().equals(roomNumber)) {
                    roomExists = true;
                    System.out.println("Room number already exists. Try again.");
                    break;
                }
            }
            if(roomExists) {
                continue;
            }
            double roomPrice;
            while (true) {
                try {
                    System.out.println("Enter room price:");
                    roomPrice = Double.parseDouble(sc.nextLine());
                    if (roomPrice < 0) {
                        System.out.println("Price can't be negative.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter correct price.");
                }
            }

            System.out.println("Enter room type: 1 - Single bed, 2 - Double bed");
            RoomType roomType;
            String value = sc.nextLine();
            if (value.equals("1")) {
                roomType = RoomType.SINGLE;
            } else if (value.equals("2")) {
                roomType = RoomType.DOUBLE;
            } else {
                System.out.println("Invalid room type, please try again.");
                continue;
            }

            IRoom room = new Room(roomNumber, roomPrice, roomType);
            ReservationService reservationService = ReservationService.getInstance();
            reservationService.addRoom(room);

            System.out.println("Room added successfully! Do you want to add another room? (yes/no)");
            String response;

            while (true) {
                try {
                    response = sc.nextLine().trim().toLowerCase();
                    if (response.equals("yes") || response.equals("no")) {
                        break;
                    } else {
                        System.out.println("Invalid response. Please enter 'yes' or 'no'.");
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred. Please try again.");
                }
            }
            boolean addAnother = response.equals("yes");
            if (!addAnother) {
                break;
            }
        }
        System.out.println("Rooms added successfully!");
    }


    public static void adminMenu(Scanner sc) {
        System.out.println("Enter the admin password: (PASSWORD = 123)");

        String adminPassword = sc.nextLine();
        if(!adminPassword.equals(password)) {
            System.out.println("Invalid password. Please try again.");
            return;
        }
        System.out.println("Welcome to the admin menu!");
        CustomerService customerService = CustomerService.getInstance();
        ReservationService reservationService = ReservationService.getInstance();

        boolean exit = false;
        while (!exit) {
            displayMenu();
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    displayAllCustomers();
                    break;
                case "2":
                    System.out.println("Displaying all rooms");
                    displayAllRooms();
                    break;
                case "3":
                    System.out.println("Displaying all reservations");
                    displayAllReservations();
                    break;
                case "4":
                    addRoom(sc);
                    break;
                case "5":
                    System.out.println("Exiting admin menu");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again");
                    break;
            }
        }
    }

}
