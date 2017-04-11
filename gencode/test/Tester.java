package gencode.test;

import java.io.File;
import java.io.IOException;

import gencode.encrypt.file.EncryptedFileUtils;
import gfiles.file.VirtualFile;

public class Tester {
	public static void main(String[] args) {

		try {
			VirtualFile picture = VirtualFile.load(new File("I:/encrypted.jpeg"));
			VirtualFile text = VirtualFile.load(new File("I:/encrypted.txt"));
			picture = EncryptedFileUtils.decryptFile(picture, "this is a test key.");
			text = EncryptedFileUtils.decryptFile(text, "this is a test key.");
			picture.save(new File("I:/picture.jpeg"));
			text.save(new File("I:/text.txt"));
			System.out.println("success");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		/*
		 * IOPair pairA = new IOPair(); IOPair pairB = new IOPair();
		 * 
		 * DataOutputStream clientEncryptOut; DataInputStream clientEncryptIn;
		 * DataOutputStream serverEncryptOut; DataInputStream serverEncryptIn;
		 * 
		 * PublicKeyEncryptionService clientEncrypt = new
		 * PublicKeyEncryptionService(pairA.getInputStream(),
		 * pairB.getOutputStream(), 128); ; PublicKeyEncryptionService
		 * serverEncrypt = new
		 * PublicKeyEncryptionService(pairB.getInputStream(),
		 * pairA.getOutputStream(), 2048);
		 * 
		 * new Thread(() -> { try { clientEncrypt.init(); } catch (Exception e)
		 * { System.out.println("client"); e.printStackTrace(); } }).start();
		 * 
		 * new Thread(() -> { try { serverEncrypt.init(); } catch (Exception e)
		 * { System.out.println("server"); e.printStackTrace(); } }).start();
		 * try { int x = 0; while (!clientEncrypt.isInitialized() ||
		 * !serverEncrypt.isInitialized()) { x++; if (x % 10 == 0)
		 * System.out.println("waiting on initialization..."); try {
		 * Thread.sleep(100); } catch (InterruptedException e) {} }
		 * 
		 * clientEncryptOut = new
		 * DataOutputStream(clientEncrypt.getOutputStream()); clientEncryptIn =
		 * new DataInputStream(clientEncrypt.getInputStream()); serverEncryptOut
		 * = new DataOutputStream(serverEncrypt.getOutputStream());
		 * serverEncryptIn = new
		 * DataInputStream(serverEncrypt.getInputStream());
		 * 
		 * clientEncryptOut.writeUTF("this is a test");
		 * clientEncryptOut.flush(); String test = serverEncryptIn.readUTF();
		 * System.out.println(test); serverEncryptOut.writeUTF(
		 * "This is a test of differing encryption standards between streams");
		 * serverEncryptOut.flush(); test = clientEncryptIn.readUTF();
		 * System.out.println(test);
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 */

	}
}
