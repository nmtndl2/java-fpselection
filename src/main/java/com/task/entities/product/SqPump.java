package com.task.entities.product;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqPump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String pressSize;

    private int sqInletWater;

    private int sqMaxTMin;

    @OneToMany(mappedBy = "sqPump", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SqCalcFR> flowRates = new ArrayList<>();
}