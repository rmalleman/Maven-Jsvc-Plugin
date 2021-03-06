package daemon.plugin.script;

import daemon.plugin.config.ApplicationConfiguration;
import daemon.plugin.config.DaemonConfiguration;
import daemon.plugin.config.InitializationScriptConfiguration;
import daemon.plugin.config.TargetSystemConfiguration;
import org.antlr.stringtemplate.StringTemplate;

public class InitializationScriptFactory {
    public static InitializationScript createInitializationScript(StringTemplate template, InitializationScriptConfiguration initializationScriptConfiguration){
        DaemonConfiguration daemonConfiguration = initializationScriptConfiguration.getDaemonConfiguration();
        final ApplicationConfiguration applicationConfiguration =
                initializationScriptConfiguration.getApplicationConfiguration();
        final TargetSystemConfiguration targetSystemConfiguration =
                initializationScriptConfiguration.getTargetSystemConfiguration();
        filterTemplate(template, daemonConfiguration, applicationConfiguration, targetSystemConfiguration);

        return new InitializationScript(applicationConfiguration.getScriptName(), template.toString());

    }

    private static void filterTemplate(StringTemplate template, DaemonConfiguration daemonConfiguration,
                                ApplicationConfiguration applicationConfiguration,
                                TargetSystemConfiguration targetSystemConfiguration) {
        template.setAttribute(InitializationScript.USER, daemonConfiguration.getDaemonUser());
        template.setAttribute(InitializationScript.APPLICATION_NAME, applicationConfiguration.getApplicationName());
        template.setAttribute(InitializationScript.TARGET_SYSTEM_APPLICATION_LOCATION, targetSystemConfiguration.getApplicationJarLocation());
        template.setAttribute(InitializationScript.APPLICATION_VERSION, applicationConfiguration.getApplicationVersion());
        template.setAttribute(InitializationScript.TARGET_SYSTEM_DAEMON_JAR_LOCATION, targetSystemConfiguration.getDaemonJarLocation());
        template.setAttribute(InitializationScript.DAEMON_JAR_NAME, applicationConfiguration.getApplicationVersion());
        template.setAttribute(InitializationScript.DELAY, daemonConfiguration.getDelayInMillis());
        template.setAttribute(InitializationScript.TARGET_SYSTEM_JAVA_HOME, targetSystemConfiguration.getJavaHomeDirectory());
    }
}
