package models;

import java.time.LocalDate;
import java.util.Date;

public class FlightBusiness extends Flight{
    public FlightBusiness(int number,
                          LocalDate date,
                          int duration,
                          int seats) {
        super(number, date, duration, seats);
    }

    @Override
    public String type() {
        return "Business";
    }
}
