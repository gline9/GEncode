package gencode.alphabet.util;

import gencode.alphabet.Bit;
import gencode.alphabet.Word;

public final class Bits {
	private Bits(){}
	public static byte[] toByteArray(Word<Bit> word){
		String repr = word.getRepresentation();
		byte[] results = new byte[(int)Math.ceil(repr.length() / ((double)8))];
		int index = 0;
		int count = 0;
		for (char ch : repr.toCharArray()){
			if (count == 8){
				count = 0;
				index ++;
			}
			switch(ch){
			case '0':
				break;
			case '1':
				results[index] += Math.pow(2, count);
				break;
			}
			count ++;
		}
		return results;
	}
}
