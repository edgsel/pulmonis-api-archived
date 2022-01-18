package com.pulmonis.pulmonisapi

import java.util.TimeZone
import javax.annotation.PostConstruct
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.context.i18n.LocaleContextHolder
import java.util.Locale
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties

@SpringBootApplication
open class PulmonisApiApplication : SpringBootServletInitializer(), WebMvcConfigurer {
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(PulmonisApiApplication::class.java)
    }

    @PostConstruct
    fun setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Bean
    open fun localeChangeInterceptor(): LocaleChangeInterceptor {
        return LocaleChangeInterceptor().apply {
            paramName = "lang"
        }
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }

    @Bean
    open fun localeResolver(mvcProperties: WebMvcProperties): AcceptHeaderLocaleResolver? {
        val localeResolver: AcceptHeaderLocaleResolver = object : AcceptHeaderLocaleResolver() {
            override fun setLocale(request: HttpServletRequest, response: HttpServletResponse?, locale: Locale?) {
                LocaleContextHolder.setLocale(locale)
            }
        }
        localeResolver.defaultLocale = mvcProperties.locale
        return localeResolver
    }

    @Bean
    open fun messageSource(): MessageSource {
        return ReloadableResourceBundleMessageSource().apply {
            setCacheSeconds(10)
            setBasename("classpath:messages")
            setDefaultEncoding("UTF-8")
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(PulmonisApiApplication::class.java, *args)
        }
    }
}
