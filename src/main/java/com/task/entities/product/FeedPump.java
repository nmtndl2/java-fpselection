package com.task.entities.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feed_pump")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedPump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "press_size", nullable = false)
    private String pressSize;

    @OneToMany(mappedBy = "feedPump", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChamberRange> chamberRanges = new ArrayList<>();
}
