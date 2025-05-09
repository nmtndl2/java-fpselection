package com.task.entities.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private SqPump sqPump;
}
