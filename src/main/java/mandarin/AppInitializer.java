package mandarin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class AppInitializer implements WebApplicationInitializer {

    private Logger logger = LoggerFactory.getLogger(AppInitializer.class);

    @Override
    public void onStartup(ServletContext servletCtx) {

        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext appCtx = new AnnotationConfigWebApplicationContext();
        appCtx.setServletContext(servletCtx);
        servletCtx.addListener(new ContextLoaderListener(appCtx));
        appCtx.register(AppConfig.class, WebConfig.class);
        appCtx.refresh();

        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(appCtx);
        ServletRegistration.Dynamic registration = servletCtx.addServlet("DispatcherServlet", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }
}