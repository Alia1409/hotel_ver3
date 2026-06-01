package persistence;

import model.Room;
import model.Reservation;
import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class StorageManager {
	private static final String ROOMS_FILE = "rooms.json";
	private static final String RESERVATIONS_FILE = "reservations.json";
	private static final java.time.format.DateTimeFormatter dateForm = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // --- ROOM INVENTORY I/O PROCESSING ---
	public List<Room> loadRooms() {
        List<Room> list = new java.util.ArrayList<>();
        File file = new File(ROOMS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String roomNumber = "", type = "", status = "", description = "";
            int floor = 0;
            double price = 0.0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{") || line.startsWith("[") || line.startsWith("]") || line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("}")) {
                    if (!roomNumber.isEmpty()) {
                        list.add(new Room(roomNumber, type, floor, price, status, description));
                    }
                    roomNumber = ""; type = ""; status = ""; description = "";
                    floor = 0; price = 0.0;
                    continue;
                }

                // Parse key-value pairs manually
                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;
                String key = parts[0].replace("\"", "").trim();
                String val = parts[1].replace("\"", "").replace(",", "").trim();

                switch (key) {
                    case "roomNumber": roomNumber = val; break;
                    case "type": type = val; break;
                    case "floor": floor = Integer.parseInt(val); break;
                    case "pricePerNight": price = Double.parseDouble(val); break;
                    case "status": status = val; break;
                    case "description": description = val; break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading JSON rooms: " + e.getMessage());
        }
        return list;
    }

    public List<Reservation> loadReservations() {
        List<Reservation> list = new java.util.ArrayList<>();
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String id = "", roomNumber = "", guestName = "", email = "", phone = "", status = "", specialNotes = "";
            LocalDate checkIn = null, checkOut = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{") || line.startsWith("[") || line.startsWith("]") || line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("}")) {
                    if (!id.isEmpty()) {
                        list.add(new Reservation(id, roomNumber, guestName, email, phone, checkIn, checkOut, status, specialNotes));
                    }
                    id = ""; roomNumber = ""; guestName = ""; email = ""; phone = ""; status = ""; specialNotes = "";
                    checkIn = null; checkOut = null;
                    continue;
                }

                // Parse key-value pairs manually
                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;
                String key = parts[0].replace("\"", "").trim();
                String val = parts[1].replace("\"", "").replace(",", "").trim();

                switch (key) {
                    case "id": id = val; break;
                    case "roomNumber": roomNumber = val; break;
                    case "guestName": guestName = val; break;
                    case "email": email = val; break;
                    case "phone": phone = val; break;
                    case "checkInDate": checkIn = LocalDate.parse(val, dateForm); break;
                    case "checkOutDate": checkOut = LocalDate.parse(val, dateForm); break;
                    case "status": status = val; break;
                    case "specialNotes": specialNotes = val; break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading JSON reservations: " + e.getMessage());
        }
        return list;
    }

 // --- SAVE ROOMS AS JSON ---
    public void saveRooms(List<Room> rooms) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            pw.println("[");
            for (int i = 0; i < rooms.size(); i++) {
                Room r = rooms.get(i);
                pw.println("  {");
                pw.println("    \"roomNumber\": \"" + r.getRoomNumber() + "\",");
                pw.println("    \"type\": \"" + r.getType() + "\",");
                pw.println("    \"floor\": " + r.getFloor() + ",");
                pw.println("    \"pricePerNight\": " + r.getPricePerNight() + ",");
                pw.println("    \"status\": \"" + r.getStatus() + "\",");
                pw.println("    \"description\": \"" + r.getDescription() + "\"");
                pw.print("  }" + (i < rooms.size() - 1 ? "," : ""));
                pw.println();
            }
            pw.println("]");
        } catch (IOException e) {
            System.out.println("Error saving JSON rooms: " + e.getMessage());
        }
    }

 // --- SAVE RESERVATIONS AS JSON ---
    public void saveReservations(List<Reservation> reservations) {
        // FIXED: Changed RERESERVATIONS_FILE to RESERVATIONS_FILE
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
            pw.println("[");
            for (int i = 0; i < reservations.size(); i++) {
                Reservation r = reservations.get(i);
                pw.println("  {");
                pw.println("    \"id\": \"" + r.getId() + "\",");
                pw.println("    \"roomNumber\": \"" + r.getRoomNumber() + "\",");
                pw.println("    \"guestName\": \"" + r.getGuestName() + "\",");
                pw.println("    \"email\": \"" + r.getEmail() + "\",");
                pw.println("    \"phone\": \"" + r.getPhone() + "\",");
                pw.println("    \"checkInDate\": \"" + r.getCheckInDate().format(dateForm) + "\",");
                pw.println("    \"checkOutDate\": \"" + r.getCheckOutDate().format(dateForm) + "\",");
                pw.println("    \"status\": \"" + r.getStatus() + "\",");
                pw.println("    \"specialNotes\": \"" + r.getSpecialNotes() + "\"");
                pw.print("  }" + (i < reservations.size() - 1 ? "," : ""));
                pw.println();
            }
            pw.println("]");
        } catch (IOException e) {
            System.out.println("Error saving JSON reservations: " + e.getMessage());
        }
    }
    
}