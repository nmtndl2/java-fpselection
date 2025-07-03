package com.task.entities.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqCalcFR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double flowRate;

    @ManyToOne
    @JoinColumn(name = "sq_pump_id")
    @ToString.Exclude
    @JsonBackReference
    private SqPump sqPump;
}
