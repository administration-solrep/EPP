package fr.dila.st.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;

/**
 * Utilitaires sur les fichiers compressés.
 * 
 * @author jtremeaux
 */
public final class ZipUtil {

	private static final Log		LOG		= LogFactory.getLog(ZipUtil.class);
	private static final STLogger	LOGGER	= STLogFactory.getLog(ZipUtil.class);

	/**
	 * Utility class
	 */
	private ZipUtil() {
		// do nothing
	}

	/**
	 * Décompresse un fichier unique de l'archive. Si l'archive contient plusieurs fichier, lève une exception.
	 * L'archive peut contenir des répertoires, dans ce cas le premier fichier rencontré n'importe où dans
	 * l'arborescence est celui pris en compte.
	 * 
	 * @param inputStream
	 *            Flux entrant de l'archive Zip
	 * @return Flux du fichier dézippé
	 * @throws ClientException
	 */
	public static ByteArrayOutputStream unzipSingleFile(InputStream inputStream) throws ClientException {
		ZipInputStream zipInputStream = null;
		ByteArrayOutputStream outputStream = null;
		try {
			zipInputStream = new ZipInputStream(inputStream);
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipEntry != null) {
				if (!zipEntry.isDirectory()) {
					if (outputStream != null) {
						throw new ClientException("L'archive ZIP doit contenir un seul fichier");
					}
					// ABI : audit de sécurité 2017. Vérification pour attaque via path traversal
					if (zipEntry.getName().contains("..") || zipEntry.getName().contains("/")
							|| zipEntry.getName().contains("\\")) {
						throw new ClientException(
								"Sécurité : le fichier du zip possède un nom invalide ( '/','..','\\')");
					}
					outputStream = new ByteArrayOutputStream();
					FileUtils.copy(zipInputStream, outputStream);
				}
				zipEntry = zipInputStream.getNextEntry();
			}
		} catch (IOException e) {
			throw new ClientException("Erreur de lecture de l'archive ZIP", e);
		} finally {
			if (zipInputStream != null) {
				try {
					zipInputStream.close();
				} catch (IOException e) {
					LOG.warn("Erreur de fermeture du flux zip", e);
				}
			}
		}
		return outputStream;
	}

	/**
	 * Charge le blob correspondant à un fichier zip dans le HttpServletResponse pour permettre le téléchargement
	 * 
	 * @param response
	 * @param zipBlob
	 */
	public static void downloadZip(HttpServletResponse response, Blob zipBlob) {
		response.reset();

		response.setContentType("application/zip; charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + zipBlob.getFilename() + "\"");
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		response.setCharacterEncoding("UTF-8");

		OutputStream responseOutputStream;
		try {
			responseOutputStream = response.getOutputStream();
			InputStream zipInputStream = zipBlob.getStream();

			byte[] bytesBuffer = new byte[2048];
			int bytesRead;
			while ((bytesRead = zipInputStream.read(bytesBuffer)) > 0) {
				responseOutputStream.write(bytesBuffer, 0, bytesRead);
			}

			responseOutputStream.flush();

			zipInputStream.close();
			responseOutputStream.close();

		} catch (IOException ioExc) {
			LOGGER.error(null, STLogEnumImpl.FAIL_GET_FILE_TEC, ioExc);
		}
	}

	/**
	 * Ajoute les fichiers contenus dans la List à l'archive zip
	 * 
	 * @param zipFile
	 *            le fichier d'archive
	 * @param filesToAdd
	 *            les fichiers à ajouter à l'archive
	 * @param encoding
	 *            l'encodage utilisé pour l'archive
	 */
	public static void zipFiles(File zipFile, List<File> filesToAdd, String encoding) {
		try {
			FileOutputStream fos = new FileOutputStream(zipFile);

			ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(new BufferedOutputStream(fos));
			zaos.setEncoding(encoding);
			zaos.setFallbackToUTF8(true);
			zaos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NOT_ENCODEABLE);
			zaos.setLevel(Deflater.BEST_COMPRESSION);

			// Get to putting all the files in the compressed output file
			for (File f : filesToAdd) {
				addFile(zaos, f, "");
			}
			// Close
			zaos.close();
			fos.close();
		} catch (IOException exc) {
			LOGGER.error(STLogEnumImpl.FAIL_ZIP_FILE_TEC, exc);
		}

	}

	/**
	 * Ajoute un fichier à une archive zip
	 * 
	 * @param outputStream
	 * @param fileToAdd
	 * @throws IOException
	 */
	public static void addFile(ArchiveOutputStream outputStream, File fileToAdd, String parentPath) throws IOException {
		if (fileToAdd.isFile()) {
			outputStream.putArchiveEntry(new ZipArchiveEntry(fileToAdd, fileToAdd.getName()));

			// Add the file to the archive
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileToAdd));
			IOUtils.copy(bis, outputStream);
			outputStream.closeArchiveEntry();
			bis.close();

		} else if (fileToAdd.isDirectory()) {
			// go through all the files in the directory and using recursion, add them to the archive
			for (File childFile : fileToAdd.listFiles()) {
				addFile(outputStream, childFile, parentPath + fileToAdd.getName());
			}
		}
	}

}
