package gencode.encrypt.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import gfiles.file.VirtualFile;

/**
 * utility that will encrypt and decrypt files given their corresponding key.
 * 
 * @author Gavin
 *
 */
public final class EncryptedFileUtils {

	// make the class non-instantiable
	private EncryptedFileUtils() {}

	/**
	 * encrypts the given virtual file with the key given, the key must be at
	 * least 8 characters long or this will throw an exception.
	 * 
	 * @param file
	 *            file to encrypt
	 * @param key
	 *            key to encrypt file with
	 * @return file that was encrypted
	 * @throws Throwable
	 */
	public static VirtualFile encryptFile(VirtualFile file, String key) throws Throwable {
		// file to store the encryption in
		VirtualFile results = new VirtualFile();

		// input stream from the file given.
		InputStream vfInputStream = file.getInputStream();

		// encrypts the file with the given key.
		encrypt(key, vfInputStream, results.getOutputStream());

		// return the encrypted file
		return results;
	}

	/**
	 * decrypts the given virtual file with the key given, the key must be at
	 * least 8 characters long or this will throw an exception.
	 * 
	 * @param file
	 *            file to decrypt
	 * @param key
	 *            key to decrypt file
	 * @return file that was decrypted
	 * @throws Throwable
	 */
	public static VirtualFile decryptFile(VirtualFile file, String key) throws Throwable {
		// file to store the decryption in
		VirtualFile results = new VirtualFile();

		// input stream from the file given.
		InputStream vfInputStream = file.getInputStream();

		// decrypts the file with the given key.
		decrypt(key, vfInputStream, results.getOutputStream());

		// return the decrypted file
		return results;
	}

	/**
	 * helper function for encrypting a file.
	 * 
	 * @param key
	 *            key for encryption
	 * @param is
	 *            input stream for data that will be encrypted
	 * @param os
	 *            output stream for data to be saved to
	 * @throws Throwable
	 */
	private static void encrypt(String key, InputStream is, OutputStream os) throws Throwable {
		// use helper function with encrypt mode for encryption
		encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, is, os);
	}

	/**
	 * helper function for decrypting a file.
	 * 
	 * @param key
	 *            key for decryption
	 * @param is
	 *            input stream for data that will be decrypted
	 * @param os
	 *            output stream for data to be saved to
	 * @throws Throwable
	 */
	private static void decrypt(String key, InputStream is, OutputStream os) throws Throwable {
		// use helper function with decrypt mode for decryption
		encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os);
	}

	/**
	 * main helper function for encryption and decryption, takes the data from
	 * the input stream and process it into the output stream.
	 * 
	 * @param key
	 *            key for the encryption/ decryption.
	 * @param mode
	 *            whether the program should encrypt or decrypt.
	 * @param is
	 *            input stream to take data from.
	 * @param os
	 *            output stream to write data to.
	 * @throws Throwable
	 */
	private static void encryptOrDecrypt(String key, int mode, InputStream is, OutputStream os) throws Throwable {

		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for
													// SunJCE

		if (mode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			doCopy(cis, os);
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			doCopy(is, cos);
		}
	}

	/**
	 * helper function to perform stream copy between stream.
	 * 
	 * @param is
	 *            input stream to read from
	 * @param os
	 *            output stream to write to
	 * @throws IOException
	 */
	public static void doCopy(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		is.close();
		os.close();
	}

}
