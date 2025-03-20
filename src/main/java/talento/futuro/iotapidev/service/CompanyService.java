package talento.futuro.iotapidev.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.CompanyRequest;
import talento.futuro.iotapidev.dto.CompanyResponse;
import talento.futuro.iotapidev.exception.CompanyNotFoundException;
import talento.futuro.iotapidev.mapper.CompanyMapper;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.repository.CompanyRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyResponse createCompany(@Valid CompanyRequest request) {

        String apiKey = UUID.randomUUID().toString().replace("-", "");
        Company company = new Company(null, request.companyName(), apiKey);
        company = companyRepository.save(company);

        return companyMapper.toResponse(company);

    }

    public List<CompanyResponse> getAll() {
        return companyRepository.findAll().stream()
                                .map(companyMapper::toResponse)
                                .toList();
    }

    public CompanyResponse getById(Integer id) {
        return companyRepository.findById(id)
                                .map(companyMapper::toResponse)
                                .orElseThrow(() -> new CompanyNotFoundException(id));
    }

    public void deleteById(Integer id) {
        if (!companyRepository.existsById(id)) {
            throw new CompanyNotFoundException(id);
        }
        companyRepository.deleteById(id);
    }

    public CompanyResponse updateCompany(Integer id, @Valid CompanyRequest request) {
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new CompanyNotFoundException(id));

        company.setName(request.companyName());
        company = companyRepository.save(company);

        return companyMapper.toResponse(company);
    }

}
