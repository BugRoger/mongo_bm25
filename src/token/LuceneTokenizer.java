package token;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

public class LuceneTokenizer implements Tokenizer {

    private static final StandardAnalyzer ANALYSER = new StandardAnalyzer(Version.LUCENE_30);

    @Override
    public List<String> tokenize(String text) {
	if (text == null) {
	    return new ArrayList<String>();
	}

	// Strip off HTML
	TextExtractor te = new TextExtractor(new Source(text));
	
	TokenStream stream = new PorterStemFilter(ANALYSER.tokenStream("whatever", new StringReader(te.toString())));
	TermAttribute term = (TermAttribute) stream.addAttribute(TermAttribute.class);

	List<String> tokens = new ArrayList<String>();
	try {
	    while (stream.incrementToken()) {
		tokens.add(term.term());
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return tokens;
    }

}
