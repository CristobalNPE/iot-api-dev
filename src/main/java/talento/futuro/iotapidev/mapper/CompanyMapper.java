package talento.futuro.iotapidev.mapper;

import org.springframework.stereotype.Component;
import talento.futuro.iotapidev.dto.CompanyResponse;
import talento.futuro.iotapidev.model.Company;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getApiKey()
        );
    }
}
