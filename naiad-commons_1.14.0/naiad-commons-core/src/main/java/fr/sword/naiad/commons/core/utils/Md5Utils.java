package fr.sword.naiad.commons.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility functions concerning md5
 * 
 */
public class Md5Utils {

	/**
	 * Check a File according to a MD5
	 * 
	 * @param file
	 * @param md5
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static boolean checkMd5(final File file, final String md5) throws NoSuchAlgorithmException, IOException {
		final String md5Value = getMD5Checksum(file);
		return md5Value.equals(md5);
	}

	/**
	 * Get the MD5 Hash of a File
	 * 
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String getMD5Checksum(final File file) throws NoSuchAlgorithmException, IOException {
		final byte[] b = createMD5Checksum(file);
		String result = "";
		for (final byte element : b) {
			result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	private static byte[] createMD5Checksum(final File file) throws NoSuchAlgorithmException, IOException {
		InputStream fis = null;
		final MessageDigest complete = MessageDigest.getInstance("MD5");

		try {
			fis = new FileInputStream(file);
			final byte[] buffer = new byte[1024];

			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}

		return complete.digest();
	}

	public static String getMD5Hash(final byte[] message) throws NoSuchAlgorithmException {
		if (message == null) {
			return null;
		}

		final MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(message);
		final byte[] md5hash = md.digest();
		final StringBuilder builder = new StringBuilder();
		for (final byte b : md5hash) {
			builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return builder.toString();
	}

}
