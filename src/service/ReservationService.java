package service;

import model.Customer;
import model.IRoom;
import model.Reservation;
import java.util.*;


public class ReservationService    {

    private static final int bufferDays = 7;

    public static ReservationService reservationService = new ReservationService();


    Map<String, IRoom> rooms = new HashMap<String, IRoom>();

    Map<String, List<Reservation>> reservations = new HashMap<String, List<Reservation>>();

    private ReservationService() {}

    public static ReservationService getInstance() {
        return reservationService;
    }

    public void addRoom(IRoom room) {
        rooms.put(room.getRoomNumber(), room);
    }



    public IRoom getARoom (String roomId) {
        return rooms.get(roomId);
    }

    private boolean isRoomAvailable(IRoom room, Date checkInDate, Date checkOutDate) {
        for (List<Reservation> reservationsForCustomer : reservations.values()) {
            for (Reservation reservation : reservationsForCustomer) {
                if (reservation.getRoom().getRoomNumber().equals(room.getRoomNumber()) &&
                        !(checkOutDate.before(reservation.getCheckInDate()) || checkInDate.after(reservation.getCheckOutDate()))) {
                    return false; // Room is already booked
                }
            }
        }
        return true; // Room is available
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        if (!isRoomAvailable(room, checkInDate, checkOutDate)) {
            throw new IllegalStateException("This room is already booked for the selected dates");
        }

        Reservation reservation = new Reservation(customer, room, checkInDate, checkOutDate);
        List<Reservation> reservationsForCustomer = reservations.computeIfAbsent(customer.getEmail(), k -> new ArrayList<Reservation>());
        reservationsForCustomer.add(reservation);
        return reservation;
    }

    public Collection<Reservation> getAllReservations() {
        List<Reservation> allReservations = new ArrayList<Reservation>();
        for(List<Reservation> reservationsForCustomer : reservations.values()) {
            allReservations.addAll(reservationsForCustomer);
        }
        return allReservations;
    }

    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        List<IRoom> vacantRooms = new ArrayList<IRoom>(rooms.values());
        for (Collection<Reservation> reservationsForCustomer : reservations.values()) {
            for (Reservation reservation : reservationsForCustomer) {
                if (!(checkOutDate.before(reservation.getCheckInDate()) || checkInDate.after(reservation.getCheckOutDate()))) {
                    vacantRooms.remove(reservation.getRoom());
                }
            }
        }
        return vacantRooms;
    }

    public Collection<Reservation> getCustomerReservations(Customer customer) {
        List<Reservation> customerReservations = new ArrayList<Reservation>();
        for (String customerEmail : reservations.keySet()) {
            if (customerEmail.equals(customer.getEmail())) {
                customerReservations.addAll(reservations.get(customerEmail));
            }
        }
        return customerReservations;
    }

    public void printAllReservations () {
            Collection<Reservation> allReservations = getAllReservations();
            if(allReservations.isEmpty()) {
                System.out.println("No reservations to display.");
            } else {
                for (Reservation reservation : allReservations) {
                    System.out.println(reservation);
                }
            }
        }

    public Collection<IRoom> getAllRooms () {
            return rooms.values();
        }

    public Collection<IRoom> findOtherRecommendedRooms(Date checkInDate, Date checkOutDate) {
        Collection<IRoom> availableRooms = reservationService.findRooms(checkInDate, checkOutDate);
        if (!availableRooms.isEmpty()) {
            return availableRooms;
        }

        Date newCheckInDate = addBufferDays(checkInDate);
        Date newCheckOutDate = addBufferDays(checkOutDate);

        System.out.println("NewCheckInDate: " + newCheckInDate);
        System.out.println("NewCheckOutDate: " + newCheckOutDate);

        Collection<IRoom> recommendedRooms = reservationService.findRooms(newCheckInDate, newCheckOutDate);

        return recommendedRooms.isEmpty() ? Collections.emptyList() : recommendedRooms;
    }

    public static Date addBufferDays(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, bufferDays);
        return calendar.getTime();
    }



}