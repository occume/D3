package org.d3.session.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityClassLoad;
import org.apache.catalina.startup.Bootstrap;
import org.apache.catalina.startup.CatalinaProperties;
import org.apache.catalina.startup.ClassLoaderFactory;
import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.apache.catalina.startup.ClassLoaderFactory.RepositoryType;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class Bootstrap0 {

    private static final Log log = LogFactory.getLog(Bootstrap0.class);

    /**
     * Daemon object used by main.
     */
    private static Bootstrap0 daemon = null;

    private static final File catalinaBaseFile;
    private static final File catalinaHomeFile;

    private static final Pattern PATH_PATTERN = Pattern.compile("(\".*?\")|(([^,])*)");

    static {
        // Will always be non-null
        String userDir = System.getProperty("user.dir");
        
        userDir = "D:\\server\\apache-tomcat-8.0.21";
        // Home first
        String home = System.getProperty(Globals.CATALINA_HOME_PROP);
        File homeFile = null;

        if (home != null) {
            File f = new File(home);
            try {
                homeFile = f.getCanonicalFile();
            } catch (IOException ioe) {
                homeFile = f.getAbsoluteFile();
            }
        }

        if (homeFile == null) {
            // First fall-back. See if current directory is a bin directory
            // in a normal Tomcat install
            File bootstrapJar = new File(userDir, "bootstrap.jar");
            if (bootstrapJar.exists()) {
                File f = new File(userDir, "..");
                try {
                    homeFile = f.getCanonicalFile();
                } catch (IOException ioe) {
                    homeFile = f.getAbsoluteFile();
                }
            }
        }

        if (homeFile == null) {
            // Second fall-back. Use current directory
            File f = new File(userDir);
            try {
                homeFile = f.getCanonicalFile();
            } catch (IOException ioe) {
                homeFile = f.getAbsoluteFile();
            }
        }
        System.out.println(homeFile);
        catalinaHomeFile = homeFile;
        System.setProperty(
                Globals.CATALINA_HOME_PROP, catalinaHomeFile.getPath());

        // Then base
        String base = System.getProperty(Globals.CATALINA_BASE_PROP);
        if (base == null) {
            catalinaBaseFile = catalinaHomeFile;
        } else {
            File baseFile = new File(base);
            try {
                baseFile = baseFile.getCanonicalFile();
            } catch (IOException ioe) {
                baseFile = baseFile.getAbsoluteFile();
            }
            catalinaBaseFile = baseFile;
        }
        System.out.println(catalinaBaseFile);
        System.setProperty(
                Globals.CATALINA_BASE_PROP, catalinaBaseFile.getPath());
    }

    // -------------------------------------------------------------- Variables


    /**
     * Daemon reference.
     */
    private Object catalinaDaemon = null;


    protected ClassLoader commonLoader = null;
    protected ClassLoader catalinaLoader = null;
    protected ClassLoader sharedLoader = null;


    // -------------------------------------------------------- Private Methods


    private void initClassLoaders() {
        try {
            commonLoader = createClassLoader("common", null);
            if( commonLoader == null ) {
                // no config file, default to this loader - we might be in a 'single' env.
                commonLoader=this.getClass().getClassLoader();
            }
            catalinaLoader = createClassLoader("server", commonLoader);
            sharedLoader = createClassLoader("shared", commonLoader);
        } catch (Throwable t) {
            handleThrowable(t);
            log.error("Class loader creation threw exception", t);
            System.exit(1);
        }
    }


    private ClassLoader createClassLoader(String name, ClassLoader parent)
        throws Exception {

        String value = CatalinaProperties.getProperty(name + ".loader");
        if ((value == null) || (value.equals("")))
            return parent;

        value = replace(value);

        List<Repository> repositories = new ArrayList<>();

        String[] repositoryPaths = getPaths(value);
        
        for (String repository : repositoryPaths) {
            // Check for a JAR URL repository
            try {
                @SuppressWarnings("unused")
                URL url = new URL(repository);
                System.out.println(url);
                repositories.add(
                        new Repository(repository, RepositoryType.URL));
                continue;
            } catch (MalformedURLException e) {
            	e.printStackTrace();
                // Ignore
            }

            // Local repository
           
            if (repository.endsWith("*.jar")) {
                repository = repository.substring
                    (0, repository.length() - "*.jar".length());
                repositories.add(
                        new Repository(repository, RepositoryType.GLOB));
            } else if (repository.endsWith(".jar")) {
                repositories.add(
                        new Repository(repository, RepositoryType.JAR));
            } else {
                repositories.add(
                        new Repository(repository, RepositoryType.DIR));
            }
            
        }
        Set<URL> set = new LinkedHashSet<>();
        for (Repository repository : repositories)  {
            if (repository.getType() == RepositoryType.URL) {
                URL url = new URL(repository.getLocation());
                if (log.isDebugEnabled())
                    log.debug("  Including URL " + url);
                set.add(url);
            } else if (repository.getType() == RepositoryType.DIR) {
                File directory = new File(repository.getLocation());
                directory = directory.getCanonicalFile();
                if (!validateFile(directory, RepositoryType.DIR)) {
                    continue;
                }
                URL url = directory.toURI().toURL();
                if (log.isDebugEnabled())
                    log.debug("  Including directory " + url);
                set.add(url);
            } else if (repository.getType() == RepositoryType.JAR) {
                File file=new File(repository.getLocation());
                file = file.getCanonicalFile();
                if (!validateFile(file, RepositoryType.JAR)) {
                    continue;
                }
                URL url = file.toURI().toURL();
                if (log.isDebugEnabled())
                    log.debug("  Including jar file " + url);
                set.add(url);
            } else if (repository.getType() == RepositoryType.GLOB) {
                File directory=new File(repository.getLocation());
                directory = directory.getCanonicalFile();
                if (!validateFile(directory, RepositoryType.GLOB)) {
                    continue;
                }
                if (log.isDebugEnabled())
                    log.debug("  Including directory glob "
                        + directory.getAbsolutePath());
                String filenames[] = directory.list();
                for (int j = 0; j < filenames.length; j++) {
                    String filename = filenames[j].toLowerCase(Locale.ENGLISH);
                    if (!filename.endsWith(".jar"))
                        continue;
                    File file = new File(directory, filenames[j]);
                    file = file.getCanonicalFile();
                    if (!validateFile(file, RepositoryType.JAR)) {
                        continue;
                    }
                    if (log.isDebugEnabled())
                        log.debug("    Including glob jar file "
                            + file.getAbsolutePath());
                    URL url = file.toURI().toURL();
                    set.add(url);
                }
            }
        }
        System.out.println(set.size());
        return ClassLoaderFactory.createClassLoader(repositories, parent);
    }
    private static boolean validateFile(File file,
            RepositoryType type) throws IOException {
        if (RepositoryType.DIR == type || RepositoryType.GLOB == type) {
            if (!file.exists() || !file.isDirectory() || !file.canRead()) {
                String msg = "Problem with directory [" + file +
                        "], exists: [" + file.exists() +
                        "], isDirectory: [" + file.isDirectory() +
                        "], canRead: [" + file.canRead() + "]";

                File home = new File (Bootstrap.getCatalinaHome());
                home = home.getCanonicalFile();
                File base = new File (Bootstrap.getCatalinaBase());
                base = base.getCanonicalFile();
                File defaultValue = new File(base, "lib");

                // Existence of ${catalina.base}/lib directory is optional.
                // Hide the warning if Tomcat runs with separate catalina.home
                // and catalina.base and that directory is absent.
                if (!home.getPath().equals(base.getPath())
                        && file.getPath().equals(defaultValue.getPath())
                        && !file.exists()) {
                    log.debug(msg);
                } else {
                    log.warn(msg);
                }
                return false;
            }
        } else if (RepositoryType.JAR == type) {
            if (!file.exists() || !file.canRead()) {
                log.warn("Problem with JAR file [" + file +
                        "], exists: [" + file.exists() +
                        "], canRead: [" + file.canRead() + "]");
                return false;
            }
        }
        return true;
    }

    /**
     * System property replacement in the given string.
     *
     * @param str The original string
     * @return the modified string
     */
    protected String replace(String str) {
        // Implementation is copied from ClassLoaderLogManager.replace(),
        // but added special processing for catalina.home and catalina.base.
        String result = str;
        int pos_start = str.indexOf("${");
        if (pos_start >= 0) {
            StringBuilder builder = new StringBuilder();
            int pos_end = -1;
            while (pos_start >= 0) {
                builder.append(str, pos_end + 1, pos_start);
                pos_end = str.indexOf('}', pos_start + 2);
                if (pos_end < 0) {
                    pos_end = pos_start - 1;
                    break;
                }
                String propName = str.substring(pos_start + 2, pos_end);
                String replacement;
                if (propName.length() == 0) {
                    replacement = null;
                } else if (Globals.CATALINA_HOME_PROP.equals(propName)) {
                    replacement = getCatalinaHome();
                } else if (Globals.CATALINA_BASE_PROP.equals(propName)) {
                    replacement = getCatalinaBase();
                } else {
                    replacement = System.getProperty(propName);
                }
                if (replacement != null) {
                    builder.append(replacement);
                } else {
                    builder.append(str, pos_start, pos_end + 1);
                }
                pos_start = str.indexOf("${", pos_end + 1);
            }
            builder.append(str, pos_end + 1, str.length());
            result = builder.toString();
        }
        return result;
    }


    /**
     * Initialize daemon.
     */
    public void init() throws Exception {

        initClassLoaders();

        Thread.currentThread().setContextClassLoader(catalinaLoader);

        SecurityClassLoad.securityClassLoad(catalinaLoader);

        // Load our startup class and call its process() method
        if (log.isDebugEnabled())
            log.debug("Loading startup class");
        Class<?> startupClass =
            catalinaLoader.loadClass
            ("org.apache.catalina.startup.Catalina");
        Object startupInstance = startupClass.newInstance();

        // Set the shared extensions class loader
        if (log.isDebugEnabled())
            log.debug("Setting startup class properties");
        String methodName = "setParentClassLoader";
        Class<?> paramTypes[] = new Class[1];
        paramTypes[0] = Class.forName("java.lang.ClassLoader");
        Object paramValues[] = new Object[1];
        paramValues[0] = sharedLoader;
        Method method =
            startupInstance.getClass().getMethod(methodName, paramTypes);
        method.invoke(startupInstance, paramValues);

        catalinaDaemon = startupInstance;

    }


    /**
     * Load daemon.
     */
    private void load(String[] arguments)
        throws Exception {

        // Call the load() method
        String methodName = "load";
        Object param[];
        Class<?> paramTypes[];
        if (arguments==null || arguments.length==0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[1];
            paramTypes[0] = arguments.getClass();
            param = new Object[1];
            param[0] = arguments;
        }
        Method method =
            catalinaDaemon.getClass().getMethod(methodName, paramTypes);
        if (log.isDebugEnabled())
            log.debug("Calling startup class " + method);
        method.invoke(catalinaDaemon, param);

    }


    /**
     * getServer() for configtest
     */
    private Object getServer() throws Exception {

        String methodName = "getServer";
        Method method =
            catalinaDaemon.getClass().getMethod(methodName);
        return method.invoke(catalinaDaemon);

    }


    // ----------------------------------------------------------- Main Program


    /**
     * Load the Catalina daemon.
     */
    public void init(String[] arguments)
        throws Exception {

        init();
        load(arguments);

    }


    /**
     * Start the Catalina daemon.
     */
    public void start()
        throws Exception {
        if( catalinaDaemon==null ) init();

        Method method = catalinaDaemon.getClass().getMethod("start", (Class [] )null);
        method.invoke(catalinaDaemon, (Object [])null);

    }


    /**
     * Stop the Catalina Daemon.
     */
    public void stop()
        throws Exception {

        Method method = catalinaDaemon.getClass().getMethod("stop", (Class [] ) null);
        method.invoke(catalinaDaemon, (Object [] ) null);

    }


    /**
     * Stop the standalone server.
     */
    public void stopServer()
        throws Exception {

        Method method =
            catalinaDaemon.getClass().getMethod("stopServer", (Class []) null);
        method.invoke(catalinaDaemon, (Object []) null);

    }


   /**
     * Stop the standalone server.
     */
    public void stopServer(String[] arguments)
        throws Exception {

        Object param[];
        Class<?> paramTypes[];
        if (arguments==null || arguments.length==0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[1];
            paramTypes[0] = arguments.getClass();
            param = new Object[1];
            param[0] = arguments;
        }
        Method method =
            catalinaDaemon.getClass().getMethod("stopServer", paramTypes);
        method.invoke(catalinaDaemon, param);

    }


    /**
     * Set flag.
     */
    public void setAwait(boolean await)
        throws Exception {

        Class<?> paramTypes[] = new Class[1];
        paramTypes[0] = Boolean.TYPE;
        Object paramValues[] = new Object[1];
        paramValues[0] = Boolean.valueOf(await);
        Method method =
            catalinaDaemon.getClass().getMethod("setAwait", paramTypes);
        method.invoke(catalinaDaemon, paramValues);

    }

    public boolean getAwait()
        throws Exception
    {
        Class<?> paramTypes[] = new Class[0];
        Object paramValues[] = new Object[0];
        Method method =
            catalinaDaemon.getClass().getMethod("getAwait", paramTypes);
        Boolean b=(Boolean)method.invoke(catalinaDaemon, paramValues);
        return b.booleanValue();
    }


    /**
     * Destroy the Catalina Daemon.
     */
    public void destroy() {

        // FIXME

    }


    /**
     * Main method and entry point when starting Tomcat via the provided
     * scripts.
     *
     * @param args Command line arguments to be processed
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception {
    	Bootstrap0 b0 = new Bootstrap0();
    	b0.createClassLoader("common", null);
    }


    /**
     * Obtain the name of configured home (binary) directory. Note that home and
     * base may be the same (and are by default).
     */
    public static String getCatalinaHome() {
        return catalinaHomeFile.getPath();
    }


    /**
     * Obtain the name of the configured base (instance) directory. Note that
     * home and base may be the same (and are by default). If this is not set
     * the value returned by {@link #getCatalinaHome()} will be used.
     */
    public static String getCatalinaBase() {
        return catalinaBaseFile.getPath();
    }


    /**
     * Obtain the configured home (binary) directory. Note that home and
     * base may be the same (and are by default).
     */
    public static File getCatalinaHomeFile() {
        return catalinaHomeFile;
    }


    /**
     * Obtain the configured base (instance) directory. Note that
     * home and base may be the same (and are by default). If this is not set
     * the value returned by {@link #getCatalinaHomeFile()} will be used.
     */
    public static File getCatalinaBaseFile() {
        return catalinaBaseFile;
    }


    // Copied from ExceptionUtils since that class is not visible during start
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently swallowed
    }


    // Protected for unit testing
    protected static String[] getPaths(String value) {

        List<String> result = new ArrayList<>();
        Matcher matcher = PATH_PATTERN.matcher(value);

        while (matcher.find()) {
            String path = value.substring(matcher.start(), matcher.end());

            path = path.trim();
            if (path.length() == 0) {
                continue;
            }

            char first = path.charAt(0);
            char last = path.charAt(path.length() - 1);

            if (first == '"' && last == '"' && path.length() > 1) {
                path = path.substring(1, path.length() - 1);
                path = path.trim();
                if (path.length() == 0) {
                    continue;
                }
            } else if (path.contains("\"")) {
                // Unbalanced quotes
                // Too early to use standard i18n support. The class path hasn't
                // been configured.
                throw new IllegalArgumentException(
                        "The double quote [\"] character only be used to quote paths. It must " +
                        "not appear in a path. This loader path is not valid: [" + value + "]");
            } else {
                // Not quoted - NO-OP
            }

            result.add(path);
        }
        return result.toArray(new String[result.size()]);
    }
}