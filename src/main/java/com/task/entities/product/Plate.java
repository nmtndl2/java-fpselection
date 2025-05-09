package com.task.entities.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long plateId;

    private String pressSize;

    private String plateType;

    private int volume;

    private double filtrationArea;

    private int cakeThk;

    private int finalCakeThk;
}