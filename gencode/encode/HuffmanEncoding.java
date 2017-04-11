package gencode.encode;

import java.util.ArrayList;

import gencode.alphabet.Bit;
import gencode.alphabet.Word;
import gencode.probability.ProbabilityMeasure;

public class HuffmanEncoding<T> {
	private final ProbabilityMeasure<T> measure;
	private final ArrayList<Word<Bit>> encoding;
	public HuffmanEncoding(ProbabilityMeasure<T> measure, ArrayList<Word<Bit>> encoding) {
		this.measure = measure;
		this.encoding = encoding;
	}
	public Word<Bit> getEncoding(T element){
		int index = measure.indexOf(element);
		return encoding.get(index);
	}
	public ArrayList<Word<Bit>> getEncoding(){
		return encoding;
	}
}
