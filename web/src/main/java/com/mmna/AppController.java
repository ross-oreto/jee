package com.mmna;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Singleton
@Path("/")
public class AppController {
    Config config = ConfigProvider.getConfig();

    @GET
    public String sayHello() {
        return "Hello World.";
    }
}
