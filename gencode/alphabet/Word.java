package gencode.alphabet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Word<A extends Alphabet> {
	private final ArrayList<A> characters;
	public Word(Word<A> copy){
		this.characters = new ArrayList<>(copy.characters);
	}
	public Word(ArrayList<A> characters){
		this.characters = new ArrayList<>(characters);
	}
	@SafeVarargs
	public Word(A... characters){
		this.characters = new ArrayList<>(Arrays.asList(characters));
	}
	public String getRepresentation(){
		Iterator<A> iterator = characters.iterator();
		String results = "";
		while (iterator.hasNext()){
			results += iterator.next().getRepresentation();
		}
		return results;
	}
	public ArrayList<A> getCharacters(){
		return characters;
	}
	public boolean equals(Object obj){
		if (obj instanceof Word){
			Word<?> word = (Word<?>) obj;
			if (word.characters.size() != this.characters.size()) return false;
			Iterator<? extends Alphabet> iterator = word.characters.iterator();
			Iterator<A> iteratorA = this.characters.iterator();
			while(iterator.hasNext()){
				if (!iterator.next().equals(iteratorA.next())) return false;
			}
			return true;
		}
		return false;
	}
	public int hashCode(){
		if (characters.size() == 0) return 0;
		return characters.get(0).hashCode();
	}
	public String toString(){
		return getRepresentation();
	}
}
