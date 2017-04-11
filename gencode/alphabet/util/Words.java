package gencode.alphabet.util;

import java.util.ArrayList;

import gencode.alphabet.Alphabet;
import gencode.alphabet.Word;

public final class Words {
	private Words(){}
	public static <A extends Alphabet> Word<A> concatenate(Word<A> a, Word<A> b){
		ArrayList<A> resultCharacters = new ArrayList<>(a.getCharacters());
		resultCharacters.addAll(b.getCharacters());
		Word<A> result = new Word<>(resultCharacters);
		return result;
	}
}
