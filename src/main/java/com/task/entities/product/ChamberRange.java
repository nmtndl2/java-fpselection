package com.task.entities.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chamber_range")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChamberRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "range_label", nullable = false)
    private String rangeLabel; // Like "0-30", "31-70"

    @Column(name = "flow_rate", nullable = false)
    private int flowRate;  // Flow rate value

    @ManyToOne
    @JoinColumn(name = "feed_pump_id", nullable = false)
    private FeedPump feedPump;
}
