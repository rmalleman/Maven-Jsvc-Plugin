package daemon.plugin.config;

import java.io.File;

public final class TargetSystemConfiguration {

    /**
     * @parameter
     *      expression="${javaHome}"
     *      default-value="/usr/lib/jvm/java-6-sun"
     * @readonly
     */
    private File javaHomeDirectory;


    /**
     * @parameter
     *      expression="${applicationJarLocation}"
     *      default-value="/var/lib/${project.artifactId}"
     * @readonly
     */
    private File applicationJarLocation;


    /**
     * @parameter
     *      expression="${daemonJarLocation}"
     *      default-value="/usr/lib"
     * @readonly
     */
    private File daemonJarLocation;

    /**
     * @parameter
     *      expression="${initializationScriptLocation}"
     *      default-value="/etc/init.d/${project.artifactId}"
     * @readonly
     */
    private File initializationScriptLocation;

    public File getJavaHomeDirectory() {
        return javaHomeDirectory;
    }

    public File getApplicationJarLocation() {
        return applicationJarLocation;
    }

    public File getDaemonJarLocation() {
        return daemonJarLocation;
    }

    public File getInitializationScriptLocation() {
        return initializationScriptLocation;
    }
}
