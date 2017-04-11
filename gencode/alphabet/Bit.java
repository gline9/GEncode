package gencode.alphabet;

public class Bit extends NumericAlphabet{
	private final boolean value;
	public Bit(boolean isOne){
		value = isOne;
	}
	public Bit(int num){
		if (num == 0){
			value = false;
		}else{
			value = true;
		}
	}
	@Override
	public int getValue() {
		return value ? 1 : 0;
	}

	@Override
	public char getRepresentation() {
		return value ? '1' : '0';
	}

	@Override
	public int size() {
		return 2;
	}

}
