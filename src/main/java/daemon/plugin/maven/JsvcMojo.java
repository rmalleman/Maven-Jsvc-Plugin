package daemon.plugin.maven;

import daemon.plugin.config.ApplicationConfiguration;
import daemon.plugin.config.DaemonConfiguration;
import daemon.plugin.config.InitializationScriptConfiguration;
import daemon.plugin.config.TargetSystemConfiguration;
import daemon.plugin.script.InitializationScript;
import daemon.plugin.script.InitializationScriptFactory;
import daemon.plugin.script.InitializationScriptWriter;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Currently only the linux file system is supported.  An entry would looks like the following snippet with default values:
 * <pre>
 *   <build>
 *     <plugins>
 *       <plugin>
 *       <artifactId>daemon-plugin</artifactId>
 *       <groupId>jsvc-plugin</groupId>
 *       ...
 *       <configuration>
 *             <application>
 *               <name>${project.artifactId}</name>
 *               <version>${project.version}</version>
 *               <mainClass>${project.groupId}.Main</mainClass>
 *               <scriptName>${project.artifactId}</scriptName>
 *             </application>
 *             <targetSystem>
 *               <javaHome>/usr/lib/jvm/java-6-sun</javaHome>
 *               <applicationJarLocation>/var/lib/${project.artifactId}</applicationJarLocation>
 *               <daemonJarLocation>/usr/lib</daemonJarLocation>
 *             </targetSystem>
 *             <daemon>
 *               <user>root</user>
 *               <delay>20</delay>
 *             </daemon>
 *       </configuration>
 *     </plugins>
 *   </build>
 * </pre>
 */

/**
 * @goal daemonize
 * @requiresDependencyResolution compile
 * @execute phase="compile"
 */
public class JsvcMojo extends AbstractMojo {

    private static final String COMMONS_DAEMON_ARTIFACT_ID = "commons-daemon";
    private static final String COMMONS_DAEMON_GROUP_ID = "commons-daemon";
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File projectBuildDirectory;

    /**
     * @parameter expression="${application}"
     */
    private ApplicationConfiguration applicationConfiguration;

    /**
     * @parameter expression="${targetSystem}"
     */
    private TargetSystemConfiguration targetSystemConfiguration;

    /**
     * @parameter expression="${targetSystem}"
     */
    private DaemonConfiguration daemonConfiguration;

    private static final String ENVIRONMENT = "linux";


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Map<String, Artifact> artifactMap = mavenProject.getArtifactMap();
        final Artifact artifact = artifactMap.get(COMMONS_DAEMON_GROUP_ID + ":" + COMMONS_DAEMON_ARTIFACT_ID);

        if (null == artifact)
            throw new MojoExecutionException("Could not retrieve commons-daemon artifact");

        InitializationScriptConfiguration initializationScriptConfiguration = null;
        try {
            final File file = artifact.getFile();
            final String daemonJarFileName = file.getName();

            new InitializationScriptConfiguration(applicationConfiguration, daemonConfiguration,
                    targetSystemConfiguration,
                    daemonJarFileName);

            FileUtils.copyFileToDirectory(file, projectBuildDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    String.format("Failed to copy artifact [%s] to [%s]", artifact, projectBuildDirectory), e);
        }

        final StringTemplate stringTemplate = loadInitScriptTemplate();

        final InitializationScript initializationScript = InitializationScriptFactory
                .createInitializationScript(stringTemplate, initializationScriptConfiguration);

        final InitializationScriptWriter initializationScriptWriter =
                new InitializationScriptWriter(initializationScript);
        try {
            initializationScriptWriter.writeToExecutableFile(projectBuildDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException(
                    String.format("Could not create [%s] in [%s]", applicationConfiguration.getScriptName(),
                            projectBuildDirectory), e);
        }
    }

    private StringTemplate loadInitScriptTemplate() {
        String resourceName = "init-script_" + ENVIRONMENT + ".st";
        try {
            InputStream inputStream = new ClassPathResource(resourceName).getInputStream();
            return new StringTemplate(IOUtils.toString(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
