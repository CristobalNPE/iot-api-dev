package talento.futuro.iotapidev.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_name", nullable = false)
    private String name;

    @Column(name = "company_api_key", nullable = false)
    private String apiKey;

}
