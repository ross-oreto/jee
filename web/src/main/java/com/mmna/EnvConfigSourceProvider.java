package com.mmna;

import io.smallrye.config.source.yaml.YamlConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Yaml source provider that loads yaml config files based on the application.env property
 * If application.env system property that is used otherwise use the environment property of the same name.
 * If neither of those exist, just look for evn=dev
 * application.env value can be comma separated to supply multiple environment profiles.
 * Example if system property is set -Dapplication.evn=dev,test
 * META-INF/microprofile-config.dev.yaml and META-INF/microprofile-config.test.yaml will be loaded.
 * Also locations WEB-INF/classes/META-INF are considered with a lower ordinal value, same rules as YamlConfigSourceProvider
 */
public class EnvConfigSourceProvider implements ConfigSourceProvider {

    static Optional<ConfigSource> getConfigSource(ClassLoader classLoader, String resource, int ordinal) {
        try {
            InputStream stream = classLoader.getResourceAsStream(resource);
            if (stream != null)
                try (Closeable c = stream) {
                    return Optional.of(new YamlConfigSource(resource, stream, ordinal));
                }
        } catch (IOException ignored) { }
        return Optional.empty();
    }

    protected static List<ConfigSource> getConfigSources(String env, ClassLoader classLoader) {
        final List<ConfigSource> configSources = new ArrayList<>(2);
        getConfigSource(classLoader
                , String.format("META-INF/microprofile-config.%s.yaml", env)
                , ConfigSource.DEFAULT_ORDINAL + 30)
                .ifPresent(configSources::add);
        getConfigSource(classLoader
                , String.format("WEB-INF/classes/META-INF/microprofile-config.%s.yaml", env)
                , ConfigSource.DEFAULT_ORDINAL + 20)
                .ifPresent(configSources::add);
        return configSources;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        String appEnv = "application.env";
        String env = System.getProperty(appEnv, System.getenv(appEnv));
        if (env == null)
            env = "dev";

        return Collections.unmodifiableList(
                Arrays.stream(env.split(","))
                .map(it -> getConfigSources(it.trim(), classLoader)).flatMap(Collection::stream)
                .collect(Collectors.toList())
        );
    }
}
