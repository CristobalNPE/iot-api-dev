package talento.futuro.iotapidev.utils;

import talento.futuro.iotapidev.dto.LocationAdminRequest;
import talento.futuro.iotapidev.dto.LocationRequest;
import talento.futuro.iotapidev.dto.LocationResponse;

import java.util.List;

public class LocationTestDataFactory {


    private static final String DEFAULT_COUNTRY = "País de Ejemplo";
    private static final String DEFAULT_CITY = "Ciudad Ejemplo";
    private static final String DEFAULT_META = "Meta: Oficina Principal";

    public static LocationAdminRequest createDefaultLocationAdminRequest(Integer companyId, String locationName) {
        return new LocationAdminRequest(
                companyId,
                locationName,
                DEFAULT_COUNTRY,
                DEFAULT_CITY,
                DEFAULT_META
        );
    }

    public static LocationRequest createDefaultLocationRequest(String locationName) {
        return new LocationRequest(
                locationName,
                DEFAULT_COUNTRY,
                DEFAULT_CITY,
                DEFAULT_META
        );
    }

    public static LocationResponse createDefaultLocationResponse(Integer id, String locationName) {

        return new LocationResponse(
                id,
                locationName,
                DEFAULT_COUNTRY,
                DEFAULT_CITY,
                DEFAULT_META
        );

    }

    public static LocationResponse createLocationResponseFromRequest(Integer newId, LocationAdminRequest request) {
        return new LocationResponse(
                newId,
                request.name(),
                request.country(),
                request.city(),
                request.meta()
        );
    }

    public static LocationResponse createLocationResponseFromRequest(Integer newId, LocationRequest request) {
        return new LocationResponse(
                newId,
                request.name(),
                request.country(),
                request.city(),
                request.meta()
        );
    }

    public static List<LocationResponse> createDefaultLocationResponseList() {
        return List.of(
                createDefaultLocationResponse(1, "Ubicación Uno"),
                createDefaultLocationResponse(2, "Ubicación Dos"),
                createDefaultLocationResponse(3, "Otra Ubicación")
        );
    }


}
