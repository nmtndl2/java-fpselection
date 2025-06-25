package com.task.entities.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlateType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long plateTypeId;

    @Column(unique = true)
    private String typeName;
}
