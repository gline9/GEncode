package gencode.probability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;


public class ProbabilityMeasure<T> {
	private final ArrayList<Event<T>> events;
	private final ArrayList<Double> probabilities;
	public ProbabilityMeasure(ArrayList<Event<T>> events){
		this.events = events;
		Iterator<Event<T>> iterator = events.iterator();
		ArrayList<Double> probabilities = new ArrayList<>();
		while(iterator.hasNext()){
			probabilities.add(iterator.next().getProbability());
		}
		this.probabilities = probabilities;
	}
	@SafeVarargs
	public ProbabilityMeasure(Event<T>... events){
		this(new ArrayList<>(Arrays.asList(events)));
	}
	public ProbabilityMeasure(ProbabilityMeasure<T> sorted) {
		this(new ArrayList<>(sorted.events));
	}
	public double getProbabilityOf(Event<T> event){
		return probabilities.get(events.indexOf(event));
	}
	public int size(){
		return events.size();
	}
	public Event<T> getEvent(int index) {
		return events.get(index);
	}
	public int indexOf(T atomic) {
		Iterator<Event<T>> iterator = events.iterator();
		while (iterator.hasNext()) {
			Event<T> event = iterator.next();
			if (event.hasAtomic(atomic)) return events.indexOf(event);
		}
		return -1;
	}
	public int indexOf(Event<T> lastCombined) {
		return events.indexOf(lastCombined);
	}
	public int getIndexOfBackwardsRankedEvent(int rank){
		double[] worstProbs = new double[rank];
		int[] worstIndexes = new int[rank];
		for (int x = 0; x < rank; x++) {
			worstProbs[x] = 1;
			worstIndexes[x] = -1;
		}
		for (int x = 0; x < events.size(); x++){
			for (int y = 0; y < rank; y++) {
				if (worstIndexes[y] == -1) {
					worstProbs[y] = events.get(x).getProbability();
					worstIndexes[y] = x;
					break;
				}else {
					if (worstProbs[y] > events.get(x).getProbability()) {
						for( int index = rank - 2; index >= y ; index-- ){
					    	worstProbs[index+1] = worstProbs [index];
					    	worstIndexes[index + 1] = worstIndexes[index];
						}
						worstProbs[y] = events.get(x).getProbability();
						worstIndexes[y] = x;
						break;
					}
				}
			}
		}
		return worstIndexes[rank - 1];
	}
	public ProbabilityMeasure<T> combineEvents(int index1, int index2){
		ArrayList<Event<T>> events = new ArrayList<>(this.events);
		if (Math.max(index1, index2) >= events.size()) throw new RuntimeException("argument: " + Math.max(index1, index2) + " is out of bounds for event lookup");
		Event<T> event1 = events.get(index1);
		Event<T> event2 = events.get(index2);
		Event<T> combined = new Event<>(event1);
		combined.addEvent(event2);
		events.remove(event1);
		events.remove(event2);
		events.add(combined);
		return new ProbabilityMeasure<>(events);
	}
	public ArrayList<Event<T>> getProbabilisticallySortedSpace(){
		return getSpaceFromSortedProbabilities(getSortedProbabilities());
	}
	public ProbabilityMeasure<T> getProbabilisticallySortedMeasure(){
		ArrayList<Event<T>> sortedSpace = getSpaceFromSortedProbabilities(getSortedProbabilities());
		return new ProbabilityMeasure<>(sortedSpace);
	}
	private ArrayList<Event<T>> getSpaceFromSortedProbabilities(ArrayList<Double> sorted){
		ArrayList<Double> probabilityCopy = new ArrayList<Double>(probabilities);
		ArrayList<Event<T>> sortedEvents = new ArrayList<>();
		Iterator<Double> iterator = sorted.iterator();
		while (iterator.hasNext()){
			int index = probabilityCopy.indexOf(iterator.next());
			probabilityCopy.set(index, null);
			sortedEvents.add(events.get(index));
		}
		return sortedEvents;
	}
	private ArrayList<Double> getSortedProbabilities(){
		ArrayList<Double> sortedProbs = new ArrayList<Double>(probabilities);
		Collections.sort(sortedProbs, new Comparator<Double>(){
			@Override
			public int compare(Double d1, Double d2) {
				return d1.compareTo(d2);
			}
		});
		return sortedProbs;
	}
	public ProbabilityMeasure<T> removeEvent(Event<T> lastCombined) {
		if (!events.contains(lastCombined)) return this;
		ArrayList<Event<T>> copy = new ArrayList<>(events);
		copy.remove(lastCombined);
		return new ProbabilityMeasure<>(copy);
	}
	public ProbabilityMeasure<T> addEvent(Event<T> lastCombined) {
		ArrayList<Event<T>> copy = new ArrayList<>(events);
		copy.add(lastCombined);
		return new ProbabilityMeasure<>(copy);
	}
	public String toString() {
		String results = "[";
		Iterator<Event<T>> iterator = events.iterator();
		while (iterator.hasNext()) {
			results += " " + iterator.next() + ",";
		}
		results.substring(0, results.length() - 2);
		results += " ]";
		return results;
	}
}
