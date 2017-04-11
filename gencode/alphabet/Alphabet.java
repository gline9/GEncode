package gencode.alphabet;

public abstract class Alphabet {
	public abstract char getRepresentation();
	public abstract int size();
	public final boolean equals(Object obj){
		if (this.getClass().equals(obj.getClass())){
			Alphabet alpha = (Alphabet)obj;
			return alpha.getRepresentation() == this.getRepresentation();
		}
		return false;
	}
	public final int hashCode(){
		return new Character(getRepresentation()).hashCode();
	}
}
