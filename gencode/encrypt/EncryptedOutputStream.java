package gencode.encrypt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import gcore.util.ArrayUtils;
import gcore.util.WrapperUtils;
import gmath.numbertheory.BigModuloArithmetic;
import gmath.types.BigInteger;

/**
 * class for encrypting an output into an outputstream. To use please look at
 * the {@link PublicKeyEncryptionService PublicKeyEncryptionService} class.
 * 
 * @author Gavin
 *
 */
public class EncryptedOutputStream extends OutputStream {
	// static field for the exponent to raise to.
	private final static BigInteger e = new BigInteger(65537);

	// static field for the number of bytes to read from each input.
	private final int numOfBytes;

	// public decryption key to use for decryption of the input stream
	private final BigInteger publicEncryptionKey;

	// input stream to receive the input from.
	private final OutputStream out;

	// buffer of numOfBytes is used to store the info before it is sent
	private final byte[] buffer;

	// pointer for where we are in the buffer
	private int pointer = 0;

	/**
	 * initializer that takes the input stream data is being received from and a
	 * key for decryption.
	 * 
	 * @param in
	 *            an encrypted stream to decrypt
	 * @param publicDecryptionKey
	 *            the key to decrypt the stream
	 */
	public EncryptedOutputStream(OutputStream out, BigInteger publicEncryptionKey2, int bytes) {
		this.numOfBytes = bytes - 2;

		this.publicEncryptionKey = publicEncryptionKey2;
		this.out = out;

		buffer = new byte[numOfBytes + 2];
	}

	/**
	 * encrypts the data and writes it to the outputstream given.
	 */
	@Override
	public void write(int data) throws IOException {
		// add info to the buffer
		buffer[pointer++] = (byte) data;

		// if the buffer is full encrypt and send
		if (pointer == numOfBytes) {
			// reset the pointer
			pointer = 0;

			// encrypt the buffer
			byte[] encryptedData = encrypt(buffer);

			// clear the last two bytes of the buffer so the size of it will be
			// reset when reading.
			buffer[numOfBytes] = 0;
			buffer[numOfBytes + 1] = 0;

			// send the encrypted data
			out.write(encryptedData);
		}
	}

	/**
	 * encrypts the given data for transmission.
	 * 
	 * @param data
	 *            data to encrypt
	 * @return encrypted data
	 */
	private byte[] encrypt(byte[] data) {

		// convert info to a BigInteger.
		BigInteger info = new BigInteger(data);

		// encrypt the info using the public encryption key
		BigInteger encrypted = BigModuloArithmetic.powerModulus(info, e, publicEncryptionKey);

		// break back into bytes.
		byte[] encryptedData = encrypted.toByteArray();

		// if there is a -1 in the encrypted data reencrypt with an added 1 to
		// element after the size byte
		if (ArrayUtils.countOccurences(WrapperUtils.toWrapper(encryptedData), new Byte((byte) -1)) > 0) {
			if (data.length == numOfBytes + 2) {

				// increment the -1 check byte
				data[numOfBytes + 1]++;

				// return the re-encrypted data
				return encrypt(data);
			} else {

				// create the -1 check byte and set to 1
				data = Arrays.copyOf(data, numOfBytes + 2);
				data[numOfBytes + 1] = 1;

				// return the re-encrypted data
				return encrypt(data);
			}
		}

		// if the encrypted data is smaller than numOfBytes + 3 extend it to
		// that size.
		if (encryptedData.length < numOfBytes + 3) {
			byte[] copy = encryptedData;
			encryptedData = new byte[numOfBytes + 3];

			// loop through each element in the data array and set it
			// to either 0 if it hasn't reached the copies data yet or the
			// copies data after that has been reached.
			for (int i = 0; i < encryptedData.length; i++) {

				// check if we have reached the data in copy yet. If we haven't
				// do nothing and go to the next index. If we have copy the
				// data.
				if (i >= encryptedData.length - copy.length) {
					encryptedData[i] = copy[i + copy.length - encryptedData.length];
				}
			}
		}

		// return the encrypted data
		return encryptedData;

	}

	/**
	 * flushes the buffer into encrypted data and then flushes the output stream
	 */
	@Override
	public void flush() throws IOException {
		// check if there is data in the buffer.
		if (pointer != 0) {
			// if there is encrypt the buffer and send.

			// save the amount of data in the buffer
			int amountOfData = pointer;

			// reset the pointer
			pointer = 0;

			// add the size byte
			buffer[numOfBytes] = (byte) amountOfData;

			// encrypt the data
			byte[] encryptedData = encrypt(buffer);

			// send the encrypted data
			out.write(encryptedData);
		}

		// flush the output stream
		out.flush();
	}

	/**
	 * flushes the buffer and then closes the output stream
	 */
	@Override
	public void close() throws IOException {
		// flush the buffer
		flush();

		// close the output stream.
		out.close();
	}

}
