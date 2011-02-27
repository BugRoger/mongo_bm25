package example;

import java.util.List;

import token.LuceneTokenizer;
import token.Tokenizer;
import data.SampleProvider;
import data.TwitterStreamSampleProvider;

public class TwitterStreamPrinter {

	private static final SampleProvider SAMPLE_PROVIDER = new TwitterStreamSampleProvider();
	private static final Tokenizer      TOKENIZER       = new LuceneTokenizer();


	public static void main(String[] args) {
		List<String> samples = SAMPLE_PROVIDER.getSamples(10);
		
		for (String sample : samples) {
			System.out.println(sample);
			System.out.println(TOKENIZER.tokenize(sample));
		}
	}

}
