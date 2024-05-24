package view;

import models.Booking;
import models.Flight;
import models.FlightBusiness;
import models.FlightInternal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class View extends JFrame {
    private final DefaultListModel<Booking> bookingListModel;
    private final DefaultListModel<Flight> flightListModel;
    private final JList<Booking> bookingList;
    private final JList<Flight> flightList;

    public View() {
        setTitle("Extract");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 400);
        setLocationRelativeTo(null);

        bookingListModel = new DefaultListModel<>();
        flightListModel = new DefaultListModel<>();
        bookingList = new JList<>(bookingListModel);
        flightList = new JList<>(flightListModel);

        JScrollPane bookingScrollPane = new JScrollPane(bookingList);
        JScrollPane flightScrollPane = new JScrollPane(flightList);

        JButton PostBookingButton = new JButton("Add booking");
        PostBookingButton.addActionListener(_ -> addBooking());

        JButton PostFlightButton = new JButton("Add Flight");
        PostFlightButton.addActionListener(_ -> addFlight());

        JButton PutBookingButton = new JButton("Change flight");
        PutBookingButton.addActionListener(_ -> changeBooking());

        JButton DeleteBookingButton = new JButton("Delete booking");
        DeleteBookingButton.addActionListener(_ -> deleteBooking());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        buttonPanel.add(PostBookingButton);
        buttonPanel.add(PostFlightButton);
        buttonPanel.add(PutBookingButton);
        buttonPanel.add(DeleteBookingButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(bookingScrollPane, BorderLayout.CENTER);
        getContentPane().add(flightScrollPane, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveDataToFile();
            }
        });

        readDataFromFile();
    }

    private void addFlight() {
        String[] types = {"Business", "Internal"};
        String selectedFlightType = (String) JOptionPane.showInputDialog(this, "Select flight type",
                "Add flight", JOptionPane.PLAIN_MESSAGE, null, types, types[0]);

        int number;

        do {
            try {
                number = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of flight: "));
                break;
            } catch (Exception _) {
                System.err.println("Number in wrong format");
                return;
            }
        } while (true);

        LocalDate date;

        try {    do{
            date = LocalDate.parse(JOptionPane.showInputDialog(this, "Enter date of flight (yyyy-MM-dd): "),                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if( -1 < Period.between(date, LocalDate.now()).getYears() && Period.between(date, LocalDate.now()).getYears() < 2 ){            break;
            }    }while (true);
        } catch (Exception _) {    System.err.println("Date in wrong format");
            return;}

        int duration = 0;

        do {
            try {
                duration = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter duration of flight(1..15): "));
            } catch (Exception _) {
                System.err.println("Duration entered wrong");
                return;
            }
        } while (duration <= 1 || duration >= 15);

        int seats = 0;

        do {
            try {
                seats = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter amount of seats of flight (30..100): "));
            } catch (Exception _) {
                System.err.println("Amount of seats entered wrong");
                return;
            }
        } while (seats <= 30 || seats >= 100);


        Flight flight = switch (selectedFlightType) {
            case "Business" -> new FlightBusiness(number, date, duration, seats);
            case "Internal" -> new FlightInternal(number, date, duration, seats);
            default -> null;
        };

        flightListModel.addElement(flight);
    }

    private void addBooking() {

        if (flightListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no flights.", "Add booking", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Flight> options = new ArrayList<>();

        for (int i = 0; i < flightListModel.size(); i++) {
            options.add(flightListModel.getElementAt(i));
        }

        List<String> types = new ArrayList<>();

        for (Flight flight : options) {
            types.add(flight.type());
        }

        Set<String> set1 = new LinkedHashSet<>(types);

        types.clear();
        types.addAll(set1);

        String selectedType = null;
        try{
            selectedType = (String) JOptionPane.showInputDialog(this, "Select university",
                "Select type of flight", JOptionPane.PLAIN_MESSAGE, null, types.toArray(), types.toArray()[0]);}catch (Exception _){}

        List<LocalDate> dates = new ArrayList<>();

        for (Flight flight : options) {
            if (flight.type().equals(selectedType)) {
                dates.add(flight.getDate());
            }
        }

        Set<LocalDate> set2 = new LinkedHashSet<>(dates);

        dates.clear();
        dates.addAll(set2);

        LocalDate selectedDate = null;
        try{    selectedDate = (LocalDate) JOptionPane.showInputDialog(this, "Select date",
                "Select date of flight", JOptionPane.PLAIN_MESSAGE, null, dates.toArray(), dates.toArray()[0]);}catch (Exception _){}

        List<Integer> numbers = new ArrayList<>();

        for (Flight flight : options) {
            if (flight.type().equals(selectedType) && flight.getDate().equals(selectedDate)) {
                numbers.add(flight.getNumber());
            }
        }

        Set<Integer> set3 = new LinkedHashSet<>(numbers);

        numbers.clear();
        numbers.addAll(set3);

        int selectedNumber = 0;
        try {    selectedNumber = (int) JOptionPane.showInputDialog(this, "Select number of flight",
                "Select number of flight", JOptionPane.PLAIN_MESSAGE, null, numbers.toArray(), numbers.toArray()[0]);}catch (Exception _){}

        Flight flight = null;

        for (int i = 0; i < flightListModel.size(); i++) {
            if (flightListModel.getElementAt(i).type().equals(selectedType)
                    && flightListModel.getElementAt(i).getNumber() == selectedNumber
                    && flightListModel.getElementAt(i).getDate().equals(selectedDate)) {
                flight = flightListModel.getElementAt(i);
                break;
            }
        }

        assert flight != null;

        List<Integer> taken_seats = new ArrayList<>();

        for (int i = 0; i < bookingListModel.size(); i++) {
            if (bookingListModel.getElementAt(i).getFlight().getNumber() == flight.getNumber()
                    && bookingListModel.getElementAt(i).getFlight().type().equals(flight.type())
                    && bookingListModel.getElementAt(i).getFlight().getDate().equals(flight.getDate())) {
                taken_seats.add(bookingListModel.getElementAt(i).getSeat());
            }
        }

        List<Integer> seats = new ArrayList<>();

        for (int i = 0; i < flight.getSeats(); i++) {
            seats.add(i + 1);
        }

        seats.removeAll(taken_seats);

        if(seats.isEmpty()){
            JOptionPane.showMessageDialog(this, "No available seats.", "Add booking", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedSeat = 0;
        try {    selectedSeat = (int) JOptionPane.showInputDialog(this, "Select seat",
                "Select seat of flight", JOptionPane.PLAIN_MESSAGE, null, seats.toArray(), seats.toArray()[0]);}catch (Exception _){}
//        int selectedSeat = (int) JOptionPane.showInputDialog(this, "Select seat",
//                "Select seat of flight", JOptionPane.PLAIN_MESSAGE, null, seats.toArray(), seats.toArray()[0]);

        String name;

        do {
            name = JOptionPane.showInputDialog(this, "Enter name: ");
            if (name == null) {
                JOptionPane.showMessageDialog(this, "Name cannot be unspecified.", "Add booking", JOptionPane.WARNING_MESSAGE);
            } else {
                break;
            }
        } while (true);

        Booking newBooking = new Booking(name, selectedSeat, flight);

        bookingListModel.addElement(newBooking);
    }

    private void deleteBooking() {
        try {
            Booking booking = bookingList.getSelectedValue();
            if (booking == null) {
                JOptionPane.showMessageDialog(this, "Please select a booking.",
                        "Booking deletion", JOptionPane.WARNING_MESSAGE);
                return;
            }

            bookingListModel.removeElement(booking);

            JOptionPane.showMessageDialog(this, "Booking successfully deleted.",
                    "Booking deletion", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception _) {
            System.err.println("Something went wrong while deleting booking");
        }

        bookingList.revalidate();
        bookingList.repaint();
    }

    private void changeBooking() {
        Booking booking = bookingList.getSelectedValue();
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Please select a booking.",
                    "Change booking", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Flight> options = new ArrayList<>();

        for (int i = 0; i < flightListModel.size(); i++) {
            options.add(flightListModel.getElementAt(i));
        }

        List<String> types = new ArrayList<>();

        for (Flight flight : options) {
            types.add(flight.type());
        }

        Set<String> set1 = new LinkedHashSet<>(types);

        types.clear();
        types.addAll(set1);

        String selectedType = (String) JOptionPane.showInputDialog(this, "Select university",
                "Select type of flight", JOptionPane.PLAIN_MESSAGE, null, types.toArray(), types.toArray()[0]);

        List<LocalDate> dates = new ArrayList<>();

        for (Flight flight : options) {
            if (flight.type().equals(selectedType)) {
                dates.add(flight.getDate());
            }
        }

        Set<LocalDate> set2 = new LinkedHashSet<>(dates);

        dates.clear();
        dates.addAll(set2);

        LocalDate selectedDate = (LocalDate) JOptionPane.showInputDialog(this, "Select date",
                "Select date of flight", JOptionPane.PLAIN_MESSAGE, null, dates.toArray(), dates.toArray()[0]);

        List<Integer> numbers = new ArrayList<>();

        for (Flight flight : options) {
            if (flight.type().equals(selectedType)
                    && flight.getDate().equals(selectedDate)
                    && !(booking.getFlight().getNumber() == flight.getNumber()
                    && booking.getFlight().getDate().equals(flight.getDate()))) {
                numbers.add(flight.getNumber());
            }
        }

        if (numbers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no other flights in provided date.", "Add booking", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<Integer> set3 = new LinkedHashSet<>(numbers);

        numbers.clear();
        numbers.addAll(set3);

        int selectedNumber = (int) JOptionPane.showInputDialog(this, "Select number of flight",
                "Select number of flight", JOptionPane.PLAIN_MESSAGE, null, numbers.toArray(), numbers.toArray()[0]);

        Flight flight = null;

        for (int i = 0; i < flightListModel.size(); i++) {
            if (flightListModel.getElementAt(i).type().equals(selectedType)
                    && flightListModel.getElementAt(i).getNumber() == selectedNumber
                    && flightListModel.getElementAt(i).getDate().equals(selectedDate)) {
                flight = flightListModel.getElementAt(i);
                break;
            }
        }

        assert flight != null;

        List<Integer> taken_seats = new ArrayList<>();

        for (int i = 0; i < bookingListModel.size(); i++) {
            if (bookingListModel.getElementAt(i).getFlight().getNumber() == flight.getNumber()
                    && bookingListModel.getElementAt(i).getFlight().type().equals(flight.type())
                    && bookingListModel.getElementAt(i).getFlight().getDate().equals(flight.getDate())) {
                taken_seats.add(bookingListModel.getElementAt(i).getSeat());
            }
        }

        List<Integer> seats = new ArrayList<>();

        for (int i = 0; i < flight.getSeats(); i++) {
            seats.add(i + 1);
        }

        seats.removeAll(taken_seats);

        if(seats.isEmpty()){
            JOptionPane.showMessageDialog(this, "No available seats.", "Add booking", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedSeat = (int) JOptionPane.showInputDialog(this, "Select seat",
                "Select seat of flight", JOptionPane.PLAIN_MESSAGE, null, seats.toArray(), seats.toArray()[0]);

        booking.setSeat(selectedSeat);
        booking.setFlight(flight);

        bookingList.revalidate();
        bookingList.repaint();
    }

    private void saveDataToFile() {
        String fileFlight = "flight.csv";
        String fileBooking = "booking.csv";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileFlight));

            for (int i = 0; i < flightListModel.size(); i++) {
                writer.write(flightListModel.getElementAt(i).toString());
                writer.newLine();
            }

            writer.close();
            System.out.println(STR."Data saved to file \{fileFlight}");
        } catch (IOException e) {
            System.err.println(STR."An error occurred while saving data to file\{fileFlight}");
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileBooking));

            for (int i = 0; i < bookingListModel.size(); i++) {
                writer.write(bookingListModel.getElementAt(i).toString());
                writer.newLine();
            }

            writer.close();
            System.out.println(STR."Data saved to file \{fileBooking}");
        } catch (IOException e) {
            System.err.println(STR."An error occurred while saving data to file\{fileBooking}");
        }
    }

    private void readDataFromFile() {
        flightListModel.clear();
        bookingListModel.clear();

        String fileFlight = "flight.csv";
        String fileBooking = "booking.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileFlight))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                int number = Integer.parseInt(parts[1]);
                LocalDate date = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                int duration = Integer.parseInt(parts[3]);
                int seats = Integer.parseInt(parts[4]);

                Flight flight = switch (parts[0]) {
                    case "Business" -> new FlightBusiness(number, date, duration, seats);
                    case "Internal" -> new FlightInternal(number, date, duration, seats);
                    default -> null;
                };

                flightListModel.addElement(flight);
            }

            System.out.println(STR."Data loaded from file \{fileFlight}");

            flightList.revalidate();
            flightList.repaint();
        } catch (IOException e) {
            System.err.println(STR."An error occured while loading data from file \{fileFlight}");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileBooking))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String name = parts[0];
                int number = Integer.parseInt(parts[1]);
                LocalDate date = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                int seat = Integer.parseInt(parts[3]);

                Flight flight;

                for (int i = 0; i < flightListModel.size(); i++) {
                    if (flightListModel.getElementAt(i).getNumber() == number && flightListModel.getElementAt(i).getDate().equals(date)) {
                        flight = flightListModel.getElementAt(i);
                        bookingListModel.addElement(new Booking(name, seat, flight));
                        break;
                    }
                }
            }
            System.out.println(STR."Data loaded from file \{fileBooking}");
            bookingList.revalidate();
            bookingList.repaint();
        } catch (IOException e) {
            System.err.println(STR."An error occured while loading data from file \{fileBooking}");
        }
    }
}
