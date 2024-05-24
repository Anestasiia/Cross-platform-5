package models;

import java.time.LocalDate;
import java.util.Date;

public class FlightInternal extends Flight {
    public FlightInternal(int number,
                          LocalDate date,
                          int duration,
                          int seats) {
        super(number, date, duration, seats);
    }

    @Override
    public String type() {
        return "Internal";
    }
}
