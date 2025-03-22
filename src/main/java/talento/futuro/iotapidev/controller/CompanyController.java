package talento.futuro.iotapidev.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import talento.futuro.iotapidev.dto.CompanyRequest;
import talento.futuro.iotapidev.dto.CompanyResponse;
import talento.futuro.iotapidev.security.ApiKeyAuthentication;
import talento.futuro.iotapidev.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public CompanyResponse createCompany(@RequestBody @Valid CompanyRequest request) {

        return companyService.createCompany(request);
    }

    @GetMapping
    public List<CompanyResponse> getAllCompanies() {
        return companyService.getAll();
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
