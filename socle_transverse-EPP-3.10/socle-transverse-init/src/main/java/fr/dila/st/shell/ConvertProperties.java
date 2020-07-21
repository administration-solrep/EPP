package fr.dila.st.shell;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.io.FileHandler;

public class ConvertProperties {

	private static final String CLI_OPT_FILE = "file";
	private static final String CLI_OPT_OUTPUT = "output";
	private static final String CLI_OPT_HELP = "help";

	public static void main(String[] args) {
		/* 
		 * On évite d'utiliser les Builder de commons-configuration2 de manière à ne pas avoir besoin de
		 * commons-beanutils
		 */
		int status = 0;

		Options options = new Options();
		options.addOption(Option.builder("f").longOpt(CLI_OPT_FILE).hasArg()
				.desc("Fichier de propriétés à convertir (plusieurs occurrences possibles)")
				.argName("PROPERTY_FILE").build());
		options.addOption(Option.builder("o").longOpt(CLI_OPT_OUTPUT).hasArg()
				.desc("Fichier de sortie (le fichier cible est écrasé); sortie standard si non précisé")
				.argName("OUTPUT").build());
		options.addOption(Option.builder("h").longOpt(CLI_OPT_HELP).hasArg(false).build());

		Writer writer = null;
		try {
			CommandLine commandLine;
			try {
				commandLine = new DefaultParser().parse(options, args);
			} catch (ParseException e) {
				throw new FatalException(String.format("Erreur de lecture des arguments: %s"), e);
			}

			if (commandLine.hasOption(CLI_OPT_HELP)) {
				printUsage(options);
			} else {
				CompositeConfiguration configuration = readConfiguration(commandLine.getOptionValues(CLI_OPT_FILE));
				writer = getOutputWriter(commandLine);
				List<String> validatedPropertyNames = validatePropertyNames(commandLine);
				StringBuilder sb = buildOutput(configuration, validatedPropertyNames);
				writeOutput(writer, sb);
			}
		} catch (FatalException e) {
			System.err.println(String.format("[FATAL] %s", e.getMessage()));
			if (e.getCause() != null) {
				System.err.println(String.format("[FATAL] cause: %s", e.getCause().getMessage()));
			}
			status = 1;
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					System.err.println(String.format("[WARN ] Erreur de fermeture des fichiers: %s", e.getMessage()));
				}
			}
		}

		System.exit(status);
	}

	private static void writeOutput(Writer writer, StringBuilder sb) throws FatalException {
		try {
			writer.write(sb.toString());
		} catch (IOException e) {
			throw new FatalException(String.format("Erreur d'écriture de %s", sb.toString()), e);
		}
	}

	private static StringBuilder buildOutput(CompositeConfiguration configuration,
			List<String> validatedPropertyNames) {
		StringBuilder sb = new StringBuilder();
		for (String property : validatedPropertyNames) {
			if (configuration.containsKey(property)) {
				String value = configuration.getString(property);
				if (value.contains("\n") || value.contains("\r")) {
					System.err.println(
							String.format("[WARN ] %s contient des retours à la ligne qui sont supprimés"));
					value.replaceAll("[\\n\\r]", "");
				}
				// escape single quote (close quote, output literal quote, reopen quote)
				value = value.replace("'", "'\\''");
				String key = property.toUpperCase().replace(".", "_").replace("-", "_");
				String line = String.format("%s='%s'\n", key, value);
				sb.append(line);
			}
		}
		sb.append("\n");
		return sb;
	}

	private static List<String> validatePropertyNames(CommandLine commandLine) {
		List<String> validatedPropertyNames = new ArrayList<String>();
		for (String property : commandLine.getArgs()) {
			if (!property.matches("[a-zA-Z.\\-]+")) {
				System.err.println(
						String.format("[WARN ] %s contient des caractères non autorisés; propriété ignorée",
								property));
			} else {
				validatedPropertyNames.add(property);
			}
		}
		return validatedPropertyNames;
	}

	private static void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(80);
		String header =
				"\n"
				+ "Convertis un ou plusieurs fichiers PROPERTY_FILE en fichier exploitable par un script"
				+ "shell. Les noms des propriétés Java sont converties en majuscule et les caractères .- sont"
				+ "remplacés par _"
				+ "\n"
				+ "Les variables sont interpolées (résolution récursive)."
				+ "\n\n";
		formatter.printHelp("convert-properties", header, options, "", true);
	}

	private static Writer getOutputWriter(CommandLine commandLine) throws FatalException {
		Writer writer = null;
		if (commandLine.hasOption(CLI_OPT_OUTPUT)) {
			String outputFile = commandLine.getOptionValue(CLI_OPT_OUTPUT);
			try {
				writer = new FileWriter(new File(outputFile));
			} catch (IOException e) {
				throw new FatalException(String.format("Erreur d'écriture du fichier de sortie %s", outputFile), e);
			}
		} else {
			writer = new OutputStreamWriter(System.out);
		}
		return writer;
	}

	private static CompositeConfiguration readConfiguration(String[] files) throws FatalException {
		CompositeConfiguration configuration = new CompositeConfiguration();
		if (files != null) {
			for (String propertiesFile : files) {
				if (new File(propertiesFile).exists()) {
					Reader fr = null;
					try {
						fr = new FileReader(new File(propertiesFile));
						PropertiesConfiguration fileConfiguration = new PropertiesConfiguration();
						fileConfiguration.read(fr);
						configuration.addConfiguration(fileConfiguration);
					} catch (Exception e) {
						throw new FatalException(String.format("Erreur de lecture du fichier %s"), e);
					} finally {
						if (fr != null) {
							try {
								fr.close();
							} catch (IOException e) {
								throw new FatalException(String.format("Erreur de fermeture du fichier %s"), e);
							}
						}
					}
				}
			}
		} else {
			InputStreamReader isr = new InputStreamReader(System.in);
			try {
				PropertiesConfiguration fileConfiguration = new PropertiesConfiguration();
				new FileHandler(fileConfiguration).load(isr);
				configuration.addConfiguration(fileConfiguration);
			} catch (Exception e) {
				throw new FatalException("Erreur de lecture de l'entrée standard", e);
			}
			// we do not have to close stdin
		}
		return configuration;
	}

	private static class FatalException extends Exception {
		private static final long serialVersionUID = -3695851782886939265L;

		@SuppressWarnings("unused")
		public FatalException() {
			super();
		}

		public FatalException(String message, Throwable cause) {
			super(message, cause);
		}

	}
}
