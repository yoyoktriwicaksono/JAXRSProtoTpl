package com.rest.test;

//import com.github.psamsotha.jersey.properties.JerseyPropertiesFeature;
//import com.github.psamsotha.jersey.properties.Prop;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by Yoyok_T on 27/08/2018.
 */
public class JerseyConfiguration extends ResourceConfig {
    public JerseyConfiguration() {
        //packages("com.rest.test");
//        property("app.properties");
//        register(JerseyPropertiesFeature.class);
//        property(JerseyPropertiesFeature.RESOURCE_PATH, "app.properties");
    }

//    @Prop("server.port")
//    private String port;
//
//    public String getPort() {
//        return this.port;
//    }

}
