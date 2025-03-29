package model;

public class Room implements IRoom {
    protected String roomNumber;
    protected Double roomPrice;
    protected RoomType enumeration;

    public Room(String roomNumber, Double roomPrice, RoomType enumeration) {
        this.roomNumber = roomNumber;
        this.roomPrice = roomPrice;
        this.enumeration = enumeration;
    }

    @Override
    public String toString() {
        return "Room number: " + roomNumber + " Room price: " + roomPrice + " Room type: " + enumeration;
    }

    @Override
    public String getRoomNumber() {
        return roomNumber;
    }

    @Override
    public Double getRoomPrice() {
        return roomPrice;
    }

    @Override
    public RoomType getRoomType() {
        return enumeration;
    }

    public boolean isFree() {
        return roomPrice == 0.0;
    }

}
