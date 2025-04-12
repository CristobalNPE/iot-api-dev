package talento.futuro.iotapidev.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import talento.futuro.iotapidev.dto.CompanyRequest;
import talento.futuro.iotapidev.dto.CompanyResponse;
import talento.futuro.iotapidev.exception.DuplicatedException;
import talento.futuro.iotapidev.exception.NotFoundException;
import talento.futuro.iotapidev.mapper.CompanyMapper;
import talento.futuro.iotapidev.model.Company;
import talento.futuro.iotapidev.repository.CompanyRepository;
import talento.futuro.iotapidev.util.ApiKeyGenerator;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final ApiKeyGenerator apiKeyGenerator;

    public CompanyResponse createCompany(@Valid CompanyRequest request) {
    	validateRequest(request);
        Company company = Company.builder()
                                 .name(request.companyName())
                                 .apiKey(apiKeyGenerator.generateApiKey())
                                 .build();

        company = companyRepository.save(company);

        return companyMapper.toResponse(company);

    }

    public Page<CompanyResponse> getAll(Pageable pageable) {
        return companyRepository.findAll(pageable)
                                .map(companyMapper::toResponse);

    }

    public CompanyResponse getById(Integer id) {
        return companyRepository.findById(id)
                                .map(companyMapper::toResponse)
                                .orElseThrow(() -> new NotFoundException("Company",id));
    }

    public void deleteById(Integer id) {
        if (!companyRepository.existsById(id)) {
            throw new NotFoundException("Company",id);
        }
        companyRepository.deleteById(id);
    }

    public CompanyResponse updateCompany(Integer id, @Valid CompanyRequest request) {
        Company company = companyRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException("Company", id));

        company.setName(request.companyName());
        company = companyRepository.save(company);

        return companyMapper.toResponse(company);
    }
    
    private void validateRequest(@Valid CompanyRequest request) {
        if (companyRepository.existsByName(request.companyName())) {
            throw new DuplicatedException("Company", request.companyName());
        }
    }

}
