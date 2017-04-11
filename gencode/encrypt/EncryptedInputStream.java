package gencode.encrypt;

import java.io.IOException;
import java.io.InputStream;

import gmath.numbertheory.BigModuloArithmetic;
import gmath.types.BigInteger;

/**
 * class for getting an encrypted input from an input stream. To use please look
 * at the {@link PublicKeyEncryptionService PublicKeyEncryptionService} class.
 * 
 * @author Gavin
 *
 */
public class EncryptedInputStream extends InputStream {
	// static field for the number of bytes to read from each input.
	private final int numOfBytes;

	// the read buffer for reading data from the input stream
	private byte[] buffer = new byte[8];

	// pointer for where the data is in the buffer when reading
	private int pointer = 0;

	// saves how much data there is to be read
	private int dataAmount = 0;

	// public decryption key to use for decryption of the input stream
	private final BigInteger privateDecryptionKey;

	private final BigInteger publicKey;

	// input stream to receive the input from.
	private final InputStream in;

	/**
	 * initializer that takes the input stream data is being received from and a
	 * key for decryption.
	 * 
	 * @param in
	 *            an encrypted stream to decrypt
	 * @param privateDecryptionKey2
	 *            the key to decrypt the stream
	 */
	public EncryptedInputStream(InputStream in, BigInteger privateDecryptionKey2, BigInteger publicKey2, int bytes) {
		this.privateDecryptionKey = privateDecryptionKey2;
		this.in = in;
		this.publicKey = publicKey2;

		// set the number of bytes to the bytes given in the constructor minus
		// two for special bits.
		numOfBytes = bytes - 2;
	}

	/**
	 * reads data from the input stream and decrypts the data it receives.
	 */
	@Override
	public int read() throws IOException {
		// if the buffer doesn't have any data left receive more data from the
		// input stream

		if (pointer == dataAmount) {
			// reset the pointer
			pointer = 0;

			// stores how many bytes were read
			int read = 0;

			// reset the buffer
			buffer = new byte[numOfBytes + 3];

			// read until the buffer is full
			while (read < numOfBytes + 3) {
				read += in.read(buffer, read, numOfBytes + 3 - read);
			}

			// decode the buffer
			buffer = decrypt(buffer);

			// check for a size bit, if there is one change the amount of data
			// to it otherwise set amount of data to the default.

			if (buffer[numOfBytes] != 0) {
				dataAmount = buffer[numOfBytes];
			} else {
				dataAmount = numOfBytes;
			}
		}
		// returns the next item in the buffer and increase the pointer also
		// filters the byte instead of returning the casted byte.
		return buffer[pointer++] & 0xFF;
	}

	/**
	 * takes data in and then decrypts it with the private decryption key.
	 * 
	 * @param data
	 *            data taken in
	 * @return decrypted data using the private decryption key.
	 */
	private byte[] decrypt(byte[] data) {

		// convert info to a big integer.
		BigInteger info = new BigInteger(data);

		// encrypt the info using the public encryption key
		BigInteger decrypted = BigModuloArithmetic.powerModulus(info, privateDecryptionKey, publicKey);

		// break back into bytes.
		byte[] decryptedData = decrypted.toByteArray();

		// check if some of the first bytes were 0's
		if (decryptedData.length < numOfBytes + 2) {
			// copy the decyrypted data to a copy array and make a new array
			// with wanted size to store the values in.
			byte[] copy = decryptedData;
			decryptedData = new byte[numOfBytes + 2];

			// loop through each element in the decryptedData array and set it
			// to either 0 if it hasn't reached the copies data yet or the
			// copies data after that has been reached.
			for (int i = 0; i < decryptedData.length; i++) {

				// check if we have reached the data in copy yet. If we haven't
				// do nothing and go to the next index. If we have copy the
				// data.
				if (i >= decryptedData.length - copy.length) {
					decryptedData[i] = copy[i + copy.length - decryptedData.length];
				}
			}

		}

		// return the decrypted data
		return decryptedData;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public void reset() throws IOException {
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void mark(int i) {
		in.mark(i);
	}

}
