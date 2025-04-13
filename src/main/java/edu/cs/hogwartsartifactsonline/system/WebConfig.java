package edu.cs.hogwartsartifactsonline.system;

import edu.cs.hogwartsartifactsonline.security.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.jwtInterceptor)
                .addPathPatterns("/api/v1/**"); // Apply interceptor to all API endpoints
//                .excludePathPatterns("/api/v1/login", "/api/v1/register"); // Exclude login and register endpoints
    }
}
