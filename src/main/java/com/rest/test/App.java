package com.rest.test;

//import com.github.psamsotha.jersey.properties.Prop;
//import com.github.psamsotha.jersey.properties.JerseyPropertiesFeature;
//import com.github.psamsotha.jersey.properties.Prop;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import com.wordnik.swagger.jaxrs.listing.ApiListingResource;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static final String JERSEY_SERVLET_NAME = "jersey-container-servlet";

    public static void main( String[] args ) throws Exception
    {

        // Specify which files to load. Configuration from both files will be merged.
        ConfigFilesProvider configFilesProvider = () -> Arrays.asList(Paths.get("app.properties"));

        // Use classpath as configuration store
        ConfigurationSource source = new ClasspathConfigurationSource(configFilesProvider);

        // Create provider
        ConfigurationProvider configProvider = new ConfigurationProviderBuilder()
                .withConfigurationSource(source)
                .build();

        ConfigData configData = configProvider.bind("server", ConfigData.class);
//        final ResourceConfig resourceConfig = new JerseyConfiguration();
//                .register(new DependencyInjectionBinder());

//        resourceConfig.register(JerseyPropertiesFeature.class);
//        resourceConfig.property(JerseyPropertiesFeature.RESOURCE_PATH, "app.properties");

//       // CORRECT
//       String port = configData.port();
//
//        if (port == null || port.isEmpty()) {
//            port = "8088";
//        }
//        Server jettyServer = new Server(Integer.valueOf(port));
//
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");
//
//        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//        jerseyServlet.setInitOrder(0);
//
//        // Tells the Jersey Servlet which REST service/class to load.
//        //"jersey.config.server.provider.classnames",
//
//        jerseyServlet.setInitParameter(
//            "javax.ws.rs.Application",
//            EntryPoint.class.getCanonicalName()
//        );
//
//        jettyServer.setHandler(context);
//
//        try {
//            jettyServer.start();
//            jettyServer.join();
//        } finally {
//            jettyServer.destroy();
//        }

        // Build the Swagger Bean.
//        String port = configData.port();
//        if (port == null || port.isEmpty()) {
//            port = "8088";
//        }

        buildSwagger(configData);

        Server jettyServer = new Server(Integer.valueOf(configData.port()));
        final ServletContextHandler contextRestApi = new ServletContextHandler(ServletContextHandler.SESSIONS);

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages( TestService.class.getPackage().getName(), ApiListingResource.class.getPackage().getName() );
        ServletContainer servletContainer = new ServletContainer( resourceConfig );
        final ServletHolder restApiHolder = new ServletHolder(servletContainer);
        restApiHolder.setInitOrder(0);
        restApiHolder.setInitParameter(
            "javax.ws.rs.Application",
            EntryPoint.class.getCanonicalName()
        );

        contextRestApi.setContextPath(".");
        contextRestApi.addServlet(restApiHolder, "/api/*");

//        final ServletHolder swaggerHolder = new ServletHolder( new DefaultServlet() );
//        final ServletContextHandler swagger = new ServletContextHandler();
//        swagger.setContextPath( "/swagger" );
//        swagger.addServlet( swaggerHolder, "/*" );
//        swagger.setBaseResource( Resource.newResource(App.class.getResource("/webapp")));
//        swagger.setWelcomeFiles(new String[] { "index.html" });

        final HandlerList handlers = new HandlerList();
//        handlers.addHandler( swagger );
        handlers.addHandler( buildSwaggerUI() );
        handlers.addHandler( contextRestApi );

        jettyServer.setHandler(handlers);
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }

        //// Way 2
//        JerseyConfiguration config = new JerseyConfiguration();
//
//        String port = System.getenv("server.port");
//        if (port == null || port.isEmpty()) {
//            port = "8089";
//        }
//
//        Server server = new Server(Integer.valueOf(port));
//        ServletContextHandler context = new ServletContextHandler(server, "/");
//
//        ServletHolder servlet = new ServletHolder(JERSEY_SERVLET_NAME,
//                new ServletContainer(config));
//        context.addServlet(servlet, "/api/*");
//
//        try {
//            server.start();
//            server.join();
//        } finally {
//            server.destroy();
//        }
    }

    private static void buildSwagger(ConfigData configData)
    {
        // This configures Swagger
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion( "1.0.0" );
        beanConfig.setResourcePackage( TestService.class.getPackage().getName() );
//        beanConfig.setSchemes(new String[] {
//                "http", "https"
//        });
//        String realPath = ServletConfig.getServletContext().getContextPath();
        beanConfig.setScan( true );
        beanConfig.setBasePath( configData.host() + ":" + configData.port() + "/api" );
        beanConfig.setDescription( "Entity Browser API to demonstrate Swagger with Jersey2 in an "
                + "embedded Jetty instance, with no web.xml or Spring MVC." );
        beanConfig.setTitle( "Entity Browser" );
    }

    private static ContextHandler buildSwaggerUI() throws Exception
    {
        final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
        swaggerUIResourceHandler.setResourceBase( App.class.getClassLoader().getResource( "webapp" ).toURI().toString() );
        final ContextHandler swaggerUIContext = new ContextHandler();
        swaggerUIContext.setContextPath( "/docs/" );
        swaggerUIContext.setHandler( swaggerUIResourceHandler );

        return swaggerUIContext;
    }
}
