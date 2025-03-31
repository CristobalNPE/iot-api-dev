package talento.futuro.iotapidev.utils;

import talento.futuro.iotapidev.dto.CompanyResponse;

import java.util.List;
import java.util.stream.IntStream;

import static talento.futuro.iotapidev.utils.TestUtils.generateApiKeyForTests;

public class CompanyTestDataFactory {


    public static CompanyResponse createCompanyResponse(int id, String name) {
        String apiKey = generateApiKeyForTests();
        return new CompanyResponse(id, name, apiKey);
    }

    public static List<CompanyResponse> createDefaultCompanyResponseList() {
        return List.of(
                createCompanyResponse(1, "Compañía Uno"),
                createCompanyResponse(2, "Compañía Dos"),
                createCompanyResponse(3, "Otra Empresa Minera")
        );
    }

    public static List<CompanyResponse> createCompanyResponseList(int count) {
        return IntStream.rangeClosed(1, count)
                        .mapToObj(i -> createCompanyResponse(i, "Compañía de Prueba " + i))
                        .toList();
    }
}
