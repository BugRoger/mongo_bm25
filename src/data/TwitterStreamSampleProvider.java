package data;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;

import java.util.ArrayList;
import java.util.List;

import net.olivo.lc4j.LanguageCategorization;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusStream;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class TwitterStreamSampleProvider implements SampleProvider {

	interface TweetFilter {
		boolean accepts(Status status);
	}

	private static final TwitterStream TWITTER_STREAM = new TwitterStreamFactory()
			.getInstance();
	private static final TweetFilter ENGLISH_STATUS_FILTER = new TweetFilter() {
		private final LanguageCategorization languageCategorization = new LanguageCategorization();

		@Override
		public boolean accepts(Status status) {
			return languageCategorization.findLanguage(
					new ByteArrayList(status.getText().getBytes())).contains(
					"english.lm");
		}
	};

	@Override
	public List<String> getSamples(int count) {
		List<String> samples = new ArrayList<String>();

		TweetStatusListener listener = new TweetStatusListener(samples,
				ENGLISH_STATUS_FILTER);
		StatusStream statusStream;
		try {
			statusStream = TWITTER_STREAM.getSampleStream();
			while (samples.size() < count) {
				statusStream.next(listener);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return samples;
	}

	private class TweetStatusListener extends StatusAdapter {

		private List<String> samples;
		private TweetFilter filter;

		public TweetStatusListener(List<String> samples, TweetFilter filter) {
			this.samples = samples;
			this.filter = filter;
		}

		public void onStatus(Status status) {
			if (filter.accepts(status)) {
				samples.add(status.getText());
				System.out.println("Collected "+samples.size()+" samples");
			}
		}
	}

}
