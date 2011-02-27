package example;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import net.olivo.lc4j.LanguageCategorization;
import twitter4j.StatusAdapter;
import twitter4j.StatusStream;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import db.MongoDatastore;
import db.Status;

public class TwitterStreamMongoCollector {

	interface TweetFilter {
		boolean accepts(twitter4j.Status status);
	}

	private static final MongoDatastore DATA_STORE = MongoDatastore
			.getInstance();
	private static final TwitterStream TWITTER_STREAM = new TwitterStreamFactory()
			.getInstance();
	private static final TweetFilter ENGLISH_STATUS_FILTER = new TweetFilter() {
		private final LanguageCategorization languageCategorization = new LanguageCategorization();


		@Override
		public boolean accepts(twitter4j.Status status) {
			return languageCategorization.findLanguage(
					new ByteArrayList(status.getText().getBytes())).contains(
					"english.lm");
		}
	};

	public static void main(String[] args) {
		TwitterStreamMongoCollector collector = new TwitterStreamMongoCollector();
		collector.go();
	}
	
	private void go() {
		TweetStatusListener listener = new TweetStatusListener(
				ENGLISH_STATUS_FILTER);
		StatusStream statusStream;
		try {
			statusStream = TWITTER_STREAM.getSampleStream();
			while (true) {
				statusStream.next(listener);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	private class TweetStatusListener extends StatusAdapter {
		private TweetFilter filter;

		public TweetStatusListener(TweetFilter filter) {
			this.filter = filter;
		}

		public void onStatus(twitter4j.Status status) {
			if (filter.accepts(status)) {
				DATA_STORE.save(new Status(status));				
			}
		}
	}
}
