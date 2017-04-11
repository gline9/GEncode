package gencode.probability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Event<T> {
	private ArrayList<T> atomics = new ArrayList<>();
	private ArrayList<Double> probs = new ArrayList<>();
	private double probability;
	public Event(Event<T> event){
		this.atomics = new ArrayList<>(event.atomics);
		this.probs = new ArrayList<>(event.probs);
		this.probability = event.probability;
	}
	public Event(ArrayList<Double> probabilities, ArrayList<T> atomics){
		this.atomics = new ArrayList<>(atomics);
		this.probs = probabilities;
		double sum = 0;
		for (double prob : probabilities){
			sum += prob;
		}
		this.probability = sum;
	}
	@SafeVarargs
	public Event(ArrayList<Double> probabilities, T... atomics){
		this(probabilities, new ArrayList<>(Arrays.asList(atomics)));
	}
	public Event(ArrayList<T> atomics, Double... probabilities){
		this(new ArrayList<>(Arrays.asList(probabilities)), atomics);
	}
	public Event(T atomic, Double probability) {
		this(new ArrayList<>(Arrays.asList(probability)), new ArrayList<>(Arrays.asList(atomic)));
	}
	public void addAtomic(double probability, T atomic){
		if (!atomics.contains(atomic)){
			atomics.add(atomic);
			probs.add(probability);
			this.probability += probability;
		}
	}
	public void addEvent(Event<T> event){
		Iterator<Double> probIter = event.probs.iterator();
		Iterator<T> atomicIter = event.atomics.iterator();
		while (probIter.hasNext()){
			addAtomic(probIter.next(), atomicIter.next());
		}
	}
	public boolean hasAtomic(T atomic) {
		if (atomics.contains(atomic)) return true;
		return false;
	}
	public double getProbability(){
		return probability;
	}
	public boolean equals(Object obj){
		if (obj instanceof Event){
			Event<?> event = (Event<?>) obj;
			if (event.atomics.size() != this.atomics.size()) return false;
			Iterator<Double> probIter = this.probs.iterator();
			Iterator<Double> eProbIter = event.probs.iterator();
			Iterator<T> atomicIter = this.atomics.iterator();
			Iterator<?> eAtomicIter = event.atomics.iterator();
			while (probIter.hasNext()){
				if (!probIter.next().equals(eProbIter.next())) return false;
				if (!atomicIter.next().equals(eAtomicIter.next())) return false;
			}
			return true;
		}
		return false;
	}
	public int hashCode(){
		if (atomics.size() == 0) return 0;
		return atomics.get(0).hashCode() * 3 * atomics.size();
	}
	public String toString() {
		return "(" + atomics + "," + probability + ")";
	}
}
