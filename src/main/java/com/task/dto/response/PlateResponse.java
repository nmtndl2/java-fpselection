package com.task.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlateResponse {

  private long plateId;

  private String pressSize;

  private String plateType;

  private int volume;

  private double filtrationArea;

  private int cakeThk;

  private int finalCakeThk;
}
