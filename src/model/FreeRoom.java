package model;

public class FreeRoom extends Room {
    public FreeRoom(String roomNumber, Double roomPrice, RoomType enumeration) {
        super(roomNumber, 0.0, enumeration);
    }

    @Override
    public String toString() {
        return " Free Room Number: " + roomNumber + "Free Room Price: " + roomPrice + "Free Room Type: " + enumeration;
    }

}
