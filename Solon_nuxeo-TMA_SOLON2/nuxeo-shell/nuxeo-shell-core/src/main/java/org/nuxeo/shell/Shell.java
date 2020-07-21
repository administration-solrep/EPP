/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General public abstract License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General public abstract License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import jline.ANSIBuffer;

import org.nuxeo.shell.cmds.ConfigurationCommands;
import org.nuxeo.shell.cmds.GlobalCommands;
import org.nuxeo.shell.cmds.Interactive;
import org.nuxeo.shell.cmds.Version;
import org.nuxeo.shell.fs.FileSystem;
import org.nuxeo.shell.impl.DefaultCompletorProvider;
import org.nuxeo.shell.impl.DefaultConsole;
import org.nuxeo.shell.impl.DefaultValueAdapter;
import org.nuxeo.shell.utils.StringUtils;

/**
 *
 * There is a single instance of the shell in the VM. To get it call
 * {@link Shell#get()}.
 *
 * parse args if no cmd attempt to read from stdin a list of cmds or from a
 * faile -f if cmd run it. A cmd line instance is parsing a single command.
 * parsed data is injected into the command and then the command is run. a cmd
 * type is providing the info on how a command is injected. top level params
 * are: -h help -u username -p password -f batch file - batch from stdin
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public final class Shell {

    /**
     * The shell instance
     */
    private static volatile Shell shell;

    public static Shell get() {
        Shell _shell = shell;
        if (_shell == null) {
            synchronized (Shell.class) {
                if (shell == null) {
                    shell = new Shell();
                    _shell = shell;
                }
            }
        }
        return _shell;
    }

    /**
     * Reset the shell instance. Useful for embedded shells like applets.
     */
    public static synchronized void reset() {
        shell = null;
    }

    protected List<ShellConfigurationListener> listeners;

    protected LinkedHashMap<String, String> mainArgs;

    protected CompositeCompletorProvider completorProvider;

    protected CompositeValueAdapter adapter;

    protected ShellConsole console;

    protected Map<String, CommandRegistry> cmds;

    protected CommandRegistry activeRegistry;

    protected Map<String, Object> ctx;

    protected Properties settings;

    protected Map<Class<?>, Object> ctxObjects;

    protected Map<Class<?>, ShellFeature> features;

    /**
     * A list with all version lines to be displayed when version command is executed.
     */
    protected List<String> versions;

    private Shell() {
        if (shell != null) {
            throw new ShellException("Shell already loaded");
        }
        shell = this;
        try {
            loadSettings();
        } catch (IOException e) {
            throw new ShellException("Failed to initialize shell", e);
        }
        listeners = new ArrayList<ShellConfigurationListener>();
        features = new HashMap<Class<?>, ShellFeature>();
        activeRegistry = GlobalCommands.INSTANCE;
        cmds = new HashMap<String, CommandRegistry>();
        ctx = new HashMap<String, Object>();
        ctxObjects = new HashMap<Class<?>, Object>();
        ctxObjects.put(Shell.class, this);
        adapter = new CompositeValueAdapter();
        console = createConsole();
        completorProvider = new CompositeCompletorProvider();
        versions = new ArrayList<String>();
        versions.add("Nuxeo Shell Version: " + Version.getShellVersion());
        addCompletorProvider(new DefaultCompletorProvider());
        addValueAdapter(new DefaultValueAdapter());
        addRegistry(GlobalCommands.INSTANCE);
        addRegistry(ConfigurationCommands.INSTANCE);
        loadFeatures();
    }

    public List<String> getVersions() {
        return versions;
    }

    public void addConfigurationListener(ShellConfigurationListener listener) {
        listeners.add(listener);
    }

    public void removeConfigurationChangeListener(
            ShellConfigurationListener listener) {
        listeners.remove(listener);
    }

    protected void loadSettings() throws IOException {
        settings = new Properties();
        getConfigDir().mkdirs();
        File file = getSettingsFile();
        if (file.isFile()) {
            FileReader reader = new FileReader(getSettingsFile());
            try {
                settings.load(reader);
            } finally {
                reader.close();
            }
        }
    }

    public Properties getSettings() {
        return settings;
    }

    public void setSetting(String name, String value) {
        try {
            File file = shell.getSettingsFile();
            shell.getSettings().put(name, value);
            FileWriter writer = new FileWriter(file);
            try {
                shell.getSettings().store(writer, "generated settings file");
                for (ShellConfigurationListener listener : listeners) {
                    listener.onConfigurationChange(name, value);
                }
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            throw new ShellException(e);
        }
    }

    public String getSetting(String key) {
        return settings.getProperty(key);
    }

    public String getSetting(String key, String defValue) {
        String v = settings.getProperty(key);
        return v == null ? defValue : v;
    }

    public boolean getBooleanSetting(String key, boolean defValue) {
        String v = settings.getProperty(key);
        return v == null ? defValue : Boolean.parseBoolean(v);
    }

    public File getConfigDir() {
        return new File(System.getProperty("user.home"), ".nxshell");
    }

    public File getSettingsFile() {
        return new File(getConfigDir(), "shell.properties");
    }

    public File getHistoryFile() {
        return new File(getConfigDir(), "history");
    }

    protected void loadFeatures() {
        ServiceLoader<ShellFeature> loader = ServiceLoader.load(
                ShellFeature.class, Shell.class.getClassLoader());
        Iterator<ShellFeature> it = loader.iterator();
        while (it.hasNext()) {
            addFeature(it.next());
        }
        // activate the default feature
        String ns = System.getProperty("shell");
        if (ns == null) {
            ns = getSetting("namespace");
        }
        if (ns != null) {
            CommandRegistry reg = getRegistry(ns);
            if (reg != null) {
                setActiveRegistry(ns);
                return;
            }
        }
        // activate the default built-in namespace
        setActiveRegistry(getDefaultNamespace());
    }

    protected String getDefaultNamespace() {
        if (getRegistry("remote") != null) {
            return "remote";
        }
        if (getRegistry("local") != null) {
            return "local";
        }
        return "global";
    }

    public LinkedHashMap<String, String> getMainArguments() {
        return mainArgs;
    }

    public void main(String[] args) throws Exception {
        mainArgs = collectArgs(args);
        String v = mainArgs.get("--version");
        if (v != null) {
            System.out.println(Version.getVersionMessage());
            return;
        }
        loadConfig();
        String path = mainArgs.get("-f");
        if (path != null) {
            FileInputStream in = new FileInputStream(new File(path));
            List<String> lines = null;
            try {
                lines = FileSystem.readAndMergeLines(in);
            } finally {
                in.close();
            }
            runBatch(lines);
        } else if (mainArgs.get("-e") != null) {
            String[] cmds = StringUtils.split(mainArgs.get("-e"), ';', true);
            runBatch(Arrays.asList(cmds));
        } else if (mainArgs.get("-") != null) { // run batch from stdin
            List<String> lines = FileSystem.readAndMergeLines(System.in);
            runBatch(lines);
        } else {
            run(Interactive.class.getAnnotation(Command.class).name());
        }
    }

    public LinkedHashMap<String, String> collectArgs(String[] args) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        if (args == null || args.length == 0) {
            return map;
        }
        String key = null;
        int k = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (key != null) {
                    map.put(key, "true");
                }
                key = args[i];
            } else if (key != null) {
                map.put(key, args[i]);
                key = null;
            } else {
                map.put("#" + (++k), args[i]);
                key = null;
            }
        }
        if (key != null) {
            map.put(key, "true");
        }
        return map;
    }

    public String[] parse(String cmdline) {
        return parse(cmdline.trim().toCharArray());
    }

    public String[] parse(char[] cbuf) {
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder buf = new StringBuilder();
        boolean esc = false;
        char quote = 0;
        for (int i = 0; i < cbuf.length; i++) {
            char c = cbuf[i];
            if (esc) {
                esc = false;
                buf.append(c);
                continue;
            }
            switch (c) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                if (quote != 0) {
                    buf.append(c);
                } else if (buf.length() > 0) {
                    result.add(buf.toString());
                    buf = new StringBuilder();
                }
                break;
            case '"':
                if (quote == '"') {
                    quote = 0;
                    result.add(buf.toString());
                    buf = new StringBuilder();
                } else if (buf.length() > 0) {
                    buf.append(c);
                } else {
                    quote = c;
                }
                break;
            case '\'':
                if (quote == '\'') {
                    quote = 0;
                    result.add(buf.toString());
                    buf = new StringBuilder();
                } else if (buf.length() > 0) {
                    buf.append(c);
                } else {
                    quote = c;
                }
                break;
            case '\\':
                esc = true;
                break;
            default:
                buf.append(c);
                break;
            }
        }
        if (buf.length() > 0) {
            result.add(buf.toString());
        }
        return result.toArray(new String[result.size()]);
    }

    protected ShellConsole createConsole() {
        return new DefaultConsole();
    }

    public void addValueAdapter(ValueAdapter adapter) {
        this.adapter.addAdapter(adapter);
    }

    public void removeValueAdapter(ValueAdapter adapter) {
        this.adapter.removeAdapter(adapter);
    }

    public void addCompletorProvider(CompletorProvider provider) {
        this.completorProvider.addProvider(provider);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContextObject(Class<T> type) {
        return (T) ctxObjects.get(type);
    }

    public <T> void putContextObject(Class<T> type, T instance) {
        ctxObjects.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T removeContextObject(Class<T> type) {
        return (T) ctxObjects.remove(type);
    }

    public CompletorProvider getCompletorProvider() {
        return completorProvider;
    }

    public void addRegistry(CommandRegistry reg) {
        cmds.put(reg.getName(), reg);
    }

    public CommandRegistry removeRegistry(String key) {
        return cmds.remove(key);
    }

    public CommandRegistry getRegistry(String name) {
        return cmds.get(name);
    }

    public CommandRegistry[] getRegistries() {
        return cmds.values().toArray(new CommandRegistry[cmds.size()]);
    }

    public String[] getRegistryNames() {
        CommandRegistry[] regs = getRegistries();
        String[] result = new String[regs.length];
        for (int i = 0; i < regs.length; i++) {
            result[i] = regs[i].getName();
        }
        return result;
    }

    public CommandRegistry getActiveRegistry() {
        return activeRegistry;
    }

    /**
     * Mark an already registered command registry as the active one.
     *
     * @param name
     * @return
     */
    public CommandRegistry setActiveRegistry(String name) {
        CommandRegistry old = activeRegistry;
        activeRegistry = getRegistry(name);
        if (activeRegistry == null) {
            activeRegistry = old;
            getConsole().println("No such namespace: " + name);
            return null;
        }
        return old;
    }

    public ShellConsole getConsole() {
        return console;
    }

    public void setConsole(ShellConsole console) {
        this.console = console;
    }

    public ValueAdapter getValueAdapter() {
        return adapter;
    }

    public Object getProperty(String key) {
        return ctx.get(key);
    }

    public Object getProperty(String key, Object defaultValue) {
        Object v = ctx.get(key);
        return v == null ? defaultValue : v;
    }

    public void setProperty(String key, Object value) {
        ctx.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return ctx;
    }

    public void runBatch(List<String> lines) throws ShellException {
        for (String line : lines) {
            run(parse(line));
        }
    }

    public void run(String cmdline) throws ShellException {
        run(parse(cmdline));
    }

    public void run(String... line) throws ShellException {
        Runnable cmd = newCommand(line);
        if (cmd != null) {
            run(cmd);
        }
    }

    public void run(Runnable cmd) throws ShellException {
        cmd.run();
    }

    public Runnable newCommand(String cmdline) throws ShellException {
        return newCommand(parse(cmdline));
    }

    public Runnable newCommand(String... line) throws ShellException {
        if (line.length == 0) {
            return null;
        }
        CommandType type = activeRegistry.getCommandType(line[0]);
        if (type == null) {
            throw new ShellException("Unknown command: " + line[0]);
        }
        return type.newInstance(this, line);
    }

    public void hello() throws IOException {
        InputStream in = Shell.class.getClassLoader().getResourceAsStream(
                "META-INF/hello.txt");
        if (in == null) {
            getConsole().println(
                    "Welcome to " + getClass().getSimpleName() + "!");
            getConsole().println("Type \"help\" for more information.");
        } else {
            try {
                String content = FileSystem.readContent(in);
                getConsole().println(content);
            } finally {
                in.close();
            }
        }
    }

    public void bye() {
        console.println("Bye.");
    }

    public ShellFeature[] getFeatures() {
        return features.values().toArray(new ShellFeature[features.size()]);
    }

    @SuppressWarnings("unchecked")
    public <T extends ShellFeature> T getFeature(Class<T> type) {
        return (T) features.get(type);
    }

    public void addFeature(ShellFeature feature) {
        if (features.containsKey(feature.getClass())) {
            throw new ShellException("Feature already registered: "
                    + feature.getClass());
        }
        feature.install(this);
        features.put(feature.getClass(), feature);
    }

    public ANSIBuffer newANSIBuffer() {
        boolean ansi = false;
        if (getConsole() instanceof Interactive) {
            ansi = ((Interactive) getConsole()).getConsole().getTerminal().isANSISupported();
        }
        ANSIBuffer buf = new ANSIBuffer();
        buf.setAnsiEnabled(ansi);
        return buf;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void loadConfig() throws IOException {
        File file = new File(System.getProperty("user.home"),
                ".nxshell/nxshell.properties");
        file.getParentFile().mkdirs();
        if (file.isFile()) {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(file);
            props.load(in);
            ctx.putAll((Map) props);
        }
    }

}
