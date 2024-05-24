package models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter @Setter
public abstract class Flight {
    private int number;
    private LocalDate date;
    private int duration;
    private int seats;

    public Flight(int number,
                  LocalDate date,
                  int duration,
                  int seats){
        this.number = number;
        this.date = date;
        this.duration = duration;
        this.seats = seats;
    }

    public abstract String type();

    @Override
    public String toString() {
        return type() + ',' + getNumber() + ',' + getDate() + ',' + getDuration() + ',' + getSeats();
    }
}
