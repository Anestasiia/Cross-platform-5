package models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Booking {

    private String name;

    private int seat;

    private Flight flight;

    public Booking(String name,
                   int seat,
                   Flight flight){
        this.name = name;
        this.seat = seat;
        this.flight = flight;
    }

    @Override
    public String toString() {
        return getName() + ',' + getFlight().getNumber() + ',' + getFlight().getDate() + ',' + getSeat();
    }
}
