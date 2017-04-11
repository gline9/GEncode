package gencode.encode.util;

import java.util.ArrayList;

import gencode.alphabet.Bit;
import gencode.alphabet.Word;
import gencode.alphabet.util.Words;
import gencode.encode.HuffmanEncoding;
import gencode.probability.Event;
import gencode.probability.ProbabilityMeasure;

public final class HuffmanEncodings {
	private HuffmanEncodings() {}

	public static <T> HuffmanEncoding<T> generateBinaryHuffmanCoding(ProbabilityMeasure<T> measure) {
		if (measure.size() == 2) {
			// results to hold the values of the huffman encoding
			ArrayList<Word<Bit>> results = new ArrayList<>();
			// add the binary bit 0 to the first position
			results.add(new Word<>(new Bit(0)));
			// add the binary bit 1 to the second position
			// the location of the 1 and 0 doesn't really matter for the
			// encoding
			results.add(new Word<>(new Bit(1)));
			// return the new huffman encoding with the measure and the results
			return new HuffmanEncoding<>(measure, results);
		}
		Event<T> lastCombined = measure.getEvent(measure.size() - 1);
		ProbabilityMeasure<T> sorted = measure.getProbabilisticallySortedMeasure();
		int worst = sorted.getIndexOfBackwardsRankedEvent(1);
		int secondWorst = sorted.getIndexOfBackwardsRankedEvent(2);
		// System.out.println(sorted.getEvent(worst));
		// System.out.println(sorted.getEvent(secondWorst));
		ProbabilityMeasure<T> copy = sorted.combineEvents(worst, secondWorst);
		HuffmanEncoding<T> encoding = generateBinaryHuffmanCoding(copy);
		// System.out.println("previous step " + encoding.getEncoding());
		// System.out.println("previous step " + copy);
		int indexOfCombined = sorted.indexOf(lastCombined);

		// removes the last combined event from the measure and adds it to the
		// end.
		sorted = sorted.removeEvent(lastCombined).addEvent(lastCombined);
		ArrayList<Word<Bit>> words = encoding.getEncoding();
		Word<Bit> combinedWord = words.get(words.size() - 1);
		words.remove(combinedWord);
		// System.out.println("combined word " + combinedWord);
		Word<Bit> worstWord = Words.concatenate(combinedWord, new Word<>(new Bit(0)));
		Word<Bit> secondWorstWord = Words.concatenate(combinedWord, new Word<>(new Bit(1)));
		// System.out.println("worst word " + worstWord + " position " + worst);
		// System.out.println("second worst word " + secondWorstWord + "
		// position " + secondWorst);
		// puts in the lower index first so it doesn't shift the larger index
		// over one when it is added.
		if (worst > secondWorst) {
			addToArray(words, secondWorst, secondWorstWord);
			addToArray(words, worst, worstWord);
		} else {
			addToArray(words, worst, worstWord);
			addToArray(words, secondWorst, secondWorstWord);
		}
		
		Word<Bit> lastCombinedWord = words.get(indexOfCombined);
		// System.out.println("last combined word " + lastCombinedWord);
		words.remove(lastCombinedWord);
		words.add(lastCombinedWord);
		// System.out.println("final " + words);
		// System.out.println("final " + sorted);
		return new HuffmanEncoding<>(sorted, words);
	}

	private static <T> void addToArray(ArrayList<T> list, int index, T element) {
		if (index >= list.size()) {
			int insertionPoint = list.size();
			list.add(null);
			addToArray(list, index, element);
			if (list.get(list.size() - 1) == null)
				list.remove(list.size() - 1);
			else
				list.remove(insertionPoint);
		} else {
			list.add(index, element);
		}
	}
}
