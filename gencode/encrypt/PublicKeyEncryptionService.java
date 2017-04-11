package gencode.encrypt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gmath.numbertheory.BigModuloArithmetic;
import gmath.numbertheory.BigPrimes;
import gmath.types.BigInteger;

/**
 * call this class to create encrypted input and output streams. This is
 * necessary as the public keys need to be exchanged between the streams.
 * 
 * @author Gavin
 *
 */
public class PublicKeyEncryptionService {
	// e used for the encryption
	private static final BigInteger e = new BigInteger(65537);

	// bits used for encryption, will be divisible by 8
	private final int bits;

	private int receptionBits;

	// input stream and output stream data needs to be transfered between
	private final InputStream in;
	private final OutputStream out;

	private BigInteger publicEncryptionKey;
	private BigInteger privateDecryptionKey;
	private BigInteger publicKey;

	// boolean to store wether the streams have been initialized or not.
	private boolean initialized = false;

	/**
	 * used to initialize the service for use.
	 * 
	 * @param in
	 *            input stream for the service
	 * @param out
	 *            output stream for the service
	 * 
	 * @param bitEncryption
	 *            number of bits to use in the encryption.
	 */
	public PublicKeyEncryptionService(InputStream in, OutputStream out, int bitEncryption) {

		// initialize the bits used to bitEncryption and make sure that it is
		// divisible by 8. The bits used will be bigger than or equal to the bit
		// encryption given.
		bits = ((int) Math.ceil(bitEncryption / 8)) * 8;

		// initialize the input and output streams
		this.in = in;
		this.out = out;
	}

	/**
	 * initializes the streams for use and sends over the public keys between
	 * this stream and the target output stream must be called before the
	 * streams can be used.
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {

		// set the lower bound for the prime numbers as 2 ^ bits / 2 + 1
		BigInteger primeLowerBound = new BigInteger(2).pow(bits / 2 + 1);

		// set the upper bound for the prime numbers as 2 ^ bits / 2 + 2
		BigInteger primeUpperBound = new BigInteger(2).pow(bits / 2 + 2);

		// initialize data input and output streams for writing integers to the
		// stream.
		DataOutputStream dOut = new DataOutputStream(out);
		DataInputStream dIn = new DataInputStream(in);

		// number chosen as it is prime and could be picked so it is a good
		// start so the loop will execute once.
		BigInteger primeAStart = e.multiply(new BigInteger(14)).inc();
		BigInteger primeBStart = e.multiply(new BigInteger(14)).inc();
		BigInteger primeA = new BigInteger(0);
		BigInteger primeB = new BigInteger(0);

		// get values to check for the first prime over and make sure the prime
		// minus 1 isn't divisible by e.
		while ((primeAStart.dec().mod(e).equals(e.ZERO()))) {
			primeAStart = BigInteger.randomBigInteger(primeUpperBound.subtract(primeLowerBound)).add(primeLowerBound);
			primeA = BigPrimes.getFirstPrimeLargerThan(primeAStart);
		}

		// get values to check for the first prime over and make sure the prime
		// minus 1 isn't divisible by e.
		while ((primeBStart.dec().mod(e).equals(e.ZERO()))) {
			primeBStart = BigInteger.randomBigInteger(primeUpperBound.subtract(primeLowerBound)).add(primeLowerBound);
			primeB = BigPrimes.getFirstPrimeLargerThan(primeBStart);
		}

		// public key is just the product of the two primes.
		publicKey = primeA.multiply(primeB);

		// converts the public key to a byte array for transmission to the other
		// stream pair.
		byte[] publicKeyArray = publicKey.toByteArray();

		// size values are so there can be a different amount of encryption
		// coming from the other user than is being sent by this user.

		// send the public key to the other stream pair.
		dOut.writeInt(bits);
		dOut.writeInt(publicKeyArray.length);
		dOut.flush();
		out.write(publicKeyArray);

		// read the other public key.
		receptionBits = dIn.readInt();
		int encryptionDataSize = dIn.readInt();
		byte[] encryptionKeyData = new byte[encryptionDataSize];
		int read = 0;
		while (read < encryptionKeyData.length) {
			read += in.read(encryptionKeyData, read, encryptionKeyData.length - read);
		}

		// convert it to BigInteger
		publicEncryptionKey = new BigInteger(encryptionKeyData);

		// compute the private decryption key.
		privateDecryptionKey = BigModuloArithmetic.moduloInverse(e, primeA.dec().multiply(primeB.dec()));
		while (privateDecryptionKey.lessThan(new BigInteger(0))) {
			privateDecryptionKey = primeA.dec().multiply(primeB.dec()).add(privateDecryptionKey);
		}

		// let the user know the streams have been successfully initialized.
		initialized = true;
	}

	/**
	 * method for accessing the input stream that is encrypted.
	 * 
	 * @return an encrypted input stream.
	 */
	public InputStream getInputStream() {
		// if they haven't been initialized throw a runtime exception
		if (!initialized)
			throw new RuntimeException("Streams must be initialized before they can be used.");

		// return the encrypted input stream
		return new EncryptedInputStream(in, privateDecryptionKey, publicKey, bits / 8);
	}

	/**
	 * mothod for accessing the output stream that is encrypted.
	 * 
	 * @return an encrypted output stream.
	 */
	public OutputStream getOutputStream() {
		// if they haven't been initialized throw a runtime exception
		if (!initialized)
			throw new RuntimeException("Streams must be initialized before they can be used.");

		// return the encrypted output stream
		return new EncryptedOutputStream(out, publicEncryptionKey, receptionBits / 8);
	}

	/**
	 * method so the user can tell if the streams for the service have been
	 * initialized or not.
	 * 
	 * @return if the streams have been initialized.
	 */
	public boolean isInitialized() {
		return initialized;
	}
}
