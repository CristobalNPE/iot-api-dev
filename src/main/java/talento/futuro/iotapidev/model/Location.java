package talento.futuro.iotapidev.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Company company;

    @Column(name = "location_name", nullable = false)
    private String name;

    @Column(name = "location_country", nullable = false)
    private String country;

    @Column(name = "location_city", nullable = false)
    private String city;

    @Column(name = "location_meta", nullable = false)
    private String meta;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors = new ArrayList<>();

}
