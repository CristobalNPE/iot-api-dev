package talento.futuro.iotapidev.utils;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockCompanySecurityContextFactory.class)
public @interface WithMockCompany {

    int companyId() default 1;

    String apiKey() default "";

}
