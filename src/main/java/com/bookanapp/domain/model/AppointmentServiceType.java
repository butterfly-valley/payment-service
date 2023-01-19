package com.bookanapp.domain.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentServiceType {

    private String description;
    private long duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentServiceType)) return false;
        AppointmentServiceType that = (AppointmentServiceType) o;
        return Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(description);
    }
}
