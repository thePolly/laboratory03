package example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;

@org.springframework.context.annotation.Configuration
@EnableWebMvc
@ComponentScan
public class Configuration extends WebMvcConfigurerAdapter {
    @Bean
    public ViewResolver xsltViewResolver(){
        XsltViewResolver xsltViewResolver = new XsltViewResolver();
        xsltViewResolver.setPrefix("/WEB-INF/xsl/");
        xsltViewResolver.setSuffix(".xslt");
        return xsltViewResolver;
    }

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }
}
