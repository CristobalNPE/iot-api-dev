package talento.futuro.iotapidev.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Location location;

    @Column(name = "sensor_name", nullable = false)
    private String name;

    @Column(name = "sensor_category", nullable = false)
    private String category;

    @Column(name = "sensor_meta", nullable = false)
    private String meta;

    @Column(name = "sensor_api_key", nullable = false)
    private String apiKey;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<SensorData> sensorData = new ArrayList<>();
}
