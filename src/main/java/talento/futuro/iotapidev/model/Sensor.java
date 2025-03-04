package talento.futuro.iotapidev.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensor")
@Getter
@Setter
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

    @Column(name = "sensor_category")
    private String category;

    @Column(name = "sensor_meta")
    private String meta;

    @Column(name = "sensor_api_key", nullable = false)
    private String apiKey;
}
