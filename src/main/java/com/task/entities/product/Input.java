package com.task.entities.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Input {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long inputId;

  private String clientName;

  private String clientRef;

  private String sludgeName;

  private String sludgeType;

  private double sludgeQty;

  private double drySolidParticle;

  private Double densityOfDrySolid;

  private double moistureContain;

  private String plateType;

  private int noOfBatch;
  private int noOfPress;
  private LocalTime washingT;
  private LocalTime sqOutletT;

  private Integer cusFeedRate;

  private List<String> pressSizes;

  private boolean cakeWashing;
  private boolean clothWashing;
}
