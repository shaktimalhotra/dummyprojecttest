package mapreduce;

class CountyCounter {
	public int getCounter() {
		return counter;
	}

	private final String country;
	private int counter = 0;

	CountyCounter(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	void increment() {
		counter++;
	}

	void increment(int x) {
		counter += x;
	}

	public String toString() {
		return "country=" + country + "; Count=" + counter;
	}

}
