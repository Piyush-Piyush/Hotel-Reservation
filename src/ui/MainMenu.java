package ui;

import model.Customer;
import model.IRoom;
import model.Reservation;
import service.CustomerService;
import service.ReservationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MainMenu {

    private static final CustomerService customerService = CustomerService.getInstance();
    private static final ReservationService reservationService = ReservationService.getInstance();

    static void displayMenu() {
        System.out.println("1. Find and reserve a room");
        System.out.println("2. See my reservations");
        System.out.println("3. Create an account");
        System.out.println("4. Admin");
        System.out.println("5. Exit");
    }

    static void createAccount(Scanner sc) {
        while (true) {
            try {
                System.out.println("Please enter your first name:");
                String firstName = sc.nextLine();
                if(firstName.isEmpty()) {
                    throw new IllegalArgumentException("First Name cannot be empty. Please try again.");
                }

                System.out.println("Please enter your last name:");
                String lastName = sc.nextLine();

                System.out.println("Please enter your email address:");
                String email;

                while(true) {
                    try {
                        email = sc.nextLine();
                        if (email.isEmpty()) {
                            throw new IllegalArgumentException("First Name or Email cannot be empty. Please try again.");
                        }
                        String emailPattern = "^(.+)@(.+).(.+)$";
                        Pattern pattern = Pattern.compile(emailPattern);
                        if(!pattern.matcher(email).matches()){
                            throw new IllegalArgumentException("Invalid email address. Please provide a valid email address.");
                        }
                        break;
                    }
                    catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }

                }


                customerService.addCustomer(email, firstName, lastName);
                System.out.println("Account created successfully!");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }


    private static Date parseDate(Scanner sc) throws ParseException {
        while (true) {
            try {
                String input = sc.nextLine().trim();
                if (!input.matches("^(0[1-9]|1[0-2])/([0-2][0-9]|3[01])/\\d{4}$")) {
                    throw new ParseException("Invalid date format.", 0);
                }
                return new SimpleDateFormat("MM/dd/yyyy").parse(input);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter the date in MM/DD/YYYY format.");
            }
        }
    }

    static void findAndReserveRoom(Scanner sc) {
        while (true) {
            try {
                System.out.println("Please enter your check-in date (MM/DD/YYYY):");
                Date checkIn;

                // Get today's date set to midnight
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.HOUR_OF_DAY, 0);
                myCalendar.set(Calendar.MINUTE, 0);
                myCalendar.set(Calendar.SECOND, 0);
                myCalendar.set(Calendar.MILLISECOND, 0);
                Date today = myCalendar.getTime();

                while (true) {
                    try {
                        checkIn = parseDate(sc);

                        if (checkIn.before(today)) {
                            System.out.println("Check-in date cannot be before today. Please enter a valid date:");
                        } else {
                            break; // Exit the loop if the date is valid
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please enter the date in MM/DD/YYYY format.");
                    }
                }

                System.out.println("Please enter your check-out date (MM/DD/YYYY):");
                Date checkOut;

                while (true) {
                    try {
                        checkOut = parseDate(sc);

                        if (!checkOut.after(checkIn)) {
                            System.out.println("Check-out date must be after the check-in date. Please try again.");
                        } else {
                            break; // Exit the loop if the check-out date is valid
                        }
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please enter the date in MM/DD/YYYY format.");
                    }
                }

                Collection<IRoom> availableRooms = reservationService.findRooms(checkIn, checkOut);
                if (availableRooms.isEmpty()) {
                    handleAlternativeRooms(sc, checkIn, checkOut);
                } else {
                    displayRooms(availableRooms);
                    reserveRoom(sc, checkIn, checkOut, availableRooms);
                }
                break;
            } catch (Exception e) {
                System.out.println("Unexpected error while booking the room. Please try again.");
            }
        }
    }


    private static void handleAlternativeRooms(Scanner sc, Date checkIn, Date checkOut) {
        Collection<IRoom> alternativeRooms = reservationService.findOtherRecommendedRooms(checkIn, checkOut);
        if (alternativeRooms.isEmpty()) {
            System.out.println("Unfortunately, rooms not available for the alternative days.");
        } else {
            System.out.println("Rooms available on alternative dates:");
            displayRooms(alternativeRooms);
            reserveRoom(sc, checkIn, checkOut, alternativeRooms);
        }
    }

    private static void displayRooms(Collection<IRoom> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
        } else {
            for (IRoom room : rooms) {
                System.out.println(room);
            }
        }
    }

    private static void reserveRoom(Scanner sc, Date checkIn, Date checkOut, Collection<IRoom> rooms) {
        System.out.println("Do you already have an account? (Yes/No)");
        if (!sc.nextLine().trim().equalsIgnoreCase("Yes")) {
            System.out.println("Please create an account to proceed.");
            createAccount(sc);
        }

        System.out.println("Provide your email address for reservation:");
        String customerEmail = sc.nextLine().trim();

        Customer customer = customerService.getCustomer(customerEmail);
        if (customer == null) {
            System.out.println("No account found for the provided email. Please check and try again.");
            return;
        }

        System.out.println("Enter the room number you'd like to reserve:");
        String roomNumber = sc.nextLine().trim();

        if (rooms.stream().noneMatch(room -> room.getRoomNumber().equals(roomNumber))) {
            System.out.println("The selected room number is not available. Please choose from the available options.");
            return;
        }

        IRoom room = reservationService.getARoom(roomNumber);
        Reservation reservation = reservationService.reserveARoom(customer, room, checkIn, checkOut);
        System.out.println("Booking successful! Here are your reservation details:\n" + reservation);
    }

    static void displayMyReservations(Scanner sc) {
        System.out.println("Please enter your email address (e.g., name@domain.com):");
        String customerEmail = sc.nextLine().trim();

        if (!customerEmail.contains("@")) {
            System.out.println("Invalid email format. Please ensure your email is correct.");
            return;
        }

        Customer customer = customerService.getCustomer(customerEmail);
        if (customer == null) {
            System.out.println("No account found with the provided email.");
            return;
        }

        Collection<Reservation> reservations = reservationService.getCustomerReservations(customer);
        if (reservations.isEmpty()) {
            System.out.println("You currently have no reservations.");
        } else {
            reservations.forEach(System.out::println);
        }
    }

    public static void mainMenu(Scanner sc) {
        boolean exit = false;
        System.out.println("Welcome to the main menu!");
        while (!exit) {
            displayMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> findAndReserveRoom(sc);
                case "2" -> displayMyReservations(sc);
                case "3" -> createAccount(sc);
                case "4" -> AdminMenu.adminMenu(sc);
                case "5" -> exit = true;
                default -> System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }
    }


}
