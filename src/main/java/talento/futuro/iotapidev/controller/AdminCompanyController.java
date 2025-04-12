package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.constants.ApiBase;
import talento.futuro.iotapidev.constants.ApiPath;
import talento.futuro.iotapidev.dto.CompanyRequest;
import talento.futuro.iotapidev.dto.CompanyResponse;
import talento.futuro.iotapidev.service.CompanyService;

@RestController
@RequestMapping(ApiBase.V1 + ApiPath.ADMIN + ApiPath.COMPANY)
@RequiredArgsConstructor
public class AdminCompanyController {

    private final CompanyService companyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse createCompany(@RequestBody @Valid CompanyRequest request) {

        return companyService.createCompany(request);
    }

    @GetMapping
    public Page<CompanyResponse> getAllCompanies(Pageable pageable) {
        return companyService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public CompanyResponse getCompanyById(@PathVariable(value = "id") Integer id) {
        return companyService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable(value = "id") Integer id) {
        companyService.deleteById(id);
    }

    @PutMapping("/{id}")
    public CompanyResponse updateCompany(@PathVariable(value = "id") Integer id,
                                         @RequestBody @Valid CompanyRequest request) {
        return companyService.updateCompany(id, request);
    }

}
