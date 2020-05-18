package com.mmna;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.rythmengine.RythmEngine;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Singleton
@Provider
@Path("/")
public class AppController implements ContainerRequestFilter {
    public static final Locale defaultLocale = new Locale("en-US");

    public static Locale toLocale(String s) {
        if (s == null || s.isEmpty()) return defaultLocale;
        String[] codes = s.split("[-_]");
        return codes.length > 1 ? new Locale(codes[0], codes[1].substring(0, 2).toUpperCase()) : new Locale(codes[0]);
    }

    protected Config config = ConfigProvider.getConfig();
    protected RythmEngine rythmEngine;

    public AppController() {
        Map<String, Object> rythmConfig = new HashMap<>();
        rythmConfig.put("rythm.home.template"
                , Paths.get(".", config.getValue("rythm.home.template", String[].class)).toFile());
        rythmConfig.put("rythm.engine.mode", config.getValue("rythm.engine.mode", String.class));
        rythmEngine = new RythmEngine(rythmConfig);
    }

    @GET
    public String sayHello(@Context Request request) {
        return rythmEngine.render("helloworld.html", "!");
    }

    /**
     * Filter method called before a request has been dispatched to a resource.
     *
     * <p>
     * Filters in the filter chain are ordered according to their {@code javax.annotation.Priority}
     * class-level annotation value.
     * If a request filter produces a response by calling {@link ContainerRequestContext#abortWith}
     * method, the execution of the (either pre-match or post-match) request filter
     * chain is stopped and the response is passed to the corresponding response
     * filter chain (either pre-match or post-match). For example, a pre-match
     * caching filter may produce a response in this way, which would effectively
     * skip any post-match request filters as well as post-match response filters.
     * Note however that a responses produced in this manner would still be processed
     * by the pre-match response filter chain.
     * </p>
     *
     * @param requestContext request context.
     * @throws IOException if an I/O exception occurs.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> query = requestContext.getUriInfo().getQueryParameters();
        String langName = config.getValue("lang.query", String.class);
        String acceptLang = requestContext.getHeaderString("Accept-Language");
        Locale locale = query.containsKey(langName)
                ? toLocale(query.getFirst(langName))
                : toLocale(acceptLang);
        rythmEngine.prepare(locale);
    }
}
