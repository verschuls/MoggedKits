package me.verschuls.tren;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import lombok.NoArgsConstructor;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@NoArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class MoggedLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolveLibraries(classpathBuilder).stream()
                .map(DefaultArtifact::new)
                .forEach(artifact -> resolver.addDependency(new Dependency(artifact, null)));
        resolver.addRepository(new RemoteRepository.Builder("maven", "default", getMavenUrl()).build());

        classpathBuilder.addLibrary(resolver);
    }

    @NotNull
    private static String getMavenUrl() {
        return Stream.of(
                System.getenv("PAPER_DEFAULT_CENTRAL_REPOSITORY"),
                System.getProperty("org.bukkit.plugin.java.LibraryLoader.centralURL"),
                "https://maven-central.storage-download.googleapis.com/maven2"
        ).filter(Objects::nonNull).findFirst().orElseThrow(IllegalStateException::new);
    }

    @NotNull
    private static List<String> resolveLibraries(@NotNull PluginClasspathBuilder classpathBuilder) {
        try (InputStream in = MoggedLoader.class.getClassLoader().getResourceAsStream("libraries.txt")) {
            return new BufferedReader(new InputStreamReader(in)).lines().toList();
        } catch (Throwable e) {
            classpathBuilder.getContext().getLogger().error("Failed to resolve libraries", e);
        }
        return List.of();
    }

}