package org.springframework.data.orient.commons.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.commons.web.OrientSourceHandlerArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class OrientWebConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new OrientSourceHandlerArgumentResolver());
    }
}
