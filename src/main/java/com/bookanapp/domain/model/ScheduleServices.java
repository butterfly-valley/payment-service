package com.bookanapp.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleServices {

    private String description;
    private long duration;

    private Float price;
    private String taxId;
    private String taxExemption;
    private Boolean invoice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleServices)) return false;
        ScheduleServices that = (ScheduleServices) o;
        return Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(description);
    }
}
