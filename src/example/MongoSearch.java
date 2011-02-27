package example;

import java.util.List;

import data.SampleProvider;
import data.TwitterStreamSampleProvider;
import db.MongoDatastore;
import db.Sample;

public class MongoSearch {
	private static final SampleProvider SAMPLE_PROVIDER = new TwitterStreamSampleProvider();
	private static final MongoDatastore DATA_STORE      = MongoDatastore.getInstance();
	
	public static void main(String[] args) {
		List<String> samples = SAMPLE_PROVIDER.getSamples(10);
		for (String status : samples) {
			Sample sample = new Sample(status);
			DATA_STORE.save(sample);
		}
	}
}
