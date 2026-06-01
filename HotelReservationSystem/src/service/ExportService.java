package service;

import model.Reservation;
import model.Room;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportService {
    public static void exportReservationsToCSV(List<Reservation> list, List<Room> rooms) {
        try (FileWriter writer = new FileWriter("monthly_report.csv")) {
            writer.write("ID,RoomNumber,Guest,CheckIn,CheckOut,Nights,TotalAmount,Status\n");
            for (Reservation res : list) {
                Room room = rooms.stream().filter(r -> r.getRoomNumber().equals(res.getRoomNumber())).findFirst().orElse(null);
                double price = (room != null) ? room.getPricePerNight() : 0.0;
                writer.write(String.format("%s,%s,%s,%s,%s,%d,%.2f,%s\n",
                        res.getId(), res.getRoomNumber(), res.getGuestName(), res.getCheckInDate(),
                        res.getCheckOutDate(), res.getNumberOfNights(), res.getTotalAmount(price), res.getStatus()));
            }
        } catch (IOException ex) {
            System.err.println("CSV export execution failed: " + ex.getMessage());
        }
    }
}