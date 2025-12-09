package com.monitoring.client.config

import com.monitoring.client.interceptor.ApiTrackingInterceptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(MonitoringProperties::class)
class MonitoringAutoConfiguration(
    private val apiTrackingInterceptor: ApiTrackingInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(apiTrackingInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/actuator/**", "/error")
    }
}
