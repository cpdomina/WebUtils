package net.cpdomina.webutils;


import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Parses relevant information (title, description, author, keywords, and thumbnail) from HTML pages.
 * @author Pedro Oliveira
 *
 */
public class LinkParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LinkParser.class);

	public static final int DEFAULT_TIMEOUT = 3000;	
	private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();

	private final int timeout;
	private final ThumbnailExtractor thumbnails;

	public LinkParser() {
		this(DEFAULT_TIMEOUT);
	}

	/**
	 * @param timeout HTTP connection timeout
	 */
	public LinkParser(int timeout) {
		this(timeout, ThumbnailExtractor.DEFAULT_READ_TIMEOUT, ThumbnailExtractor.DEFAULT_CONNECT_TIMEOUT);
	}

	/**
	 * @param timeout HTTP connection timeout
	 * @param readTimeout Thumbnail reading timeout
	 * @param connectTimeout Thumbnail connection timeout
	 */
	public LinkParser(int timeout, int readTimeout, int connectTimeout) {
		this.timeout = timeout;
		thumbnails = new ThumbnailExtractor(readTimeout, connectTimeout);
	}

	/**
	 * Parse the given {@link URL}
	 * @param url
	 * @return
	 */
	public Link parse(URL url) {

		Link link = new Link(url);
		try {
			Document doc = Jsoup.connect(url.toString()).timeout(timeout).get();

			//Title
			Element title = doc.head().select("title").first();
			if(title != null) {
				link.setTitle(normalize(title.text()));
			} else {
				title = doc.head().select("meta[property=og:title], meta[name=title]").first();
				if(title != null) {
					link.setDescription(normalize(title.attr("content")));
				}
			}

			//Description
			Element description = doc.head().select("meta[name=description], meta[property=og:description]").first();
			if(description != null) {			
				link.setDescription(normalize(description.attr("content")));
			}

			//Author
			Element author = doc.head().select("meta[name=author]").first();
			if(author != null) {
				link.setAuthor(normalize(author.attr("content")));
			}

			//keywords
			Element keywords = doc.head().select("meta[name=keywords]").first();
			if(keywords != null) {
				link.setKeywords(Iterables.toArray(SPLITTER.split(normalize(keywords.attr("content"))), String.class));
			}

			//Thumbnail
			link.setThumbnail(thumbnails.thumbnail(doc));

		} catch (IOException e) {
			LOGGER.warn("Problem while parsing {}", url, e);
		}

		return link;
	}

	private static String normalize(String text) {
		if(text != null) {
			return Jsoup.parse(text).text().replaceAll("\\s", " ");
		} else {
			return null;
		}		
	}
}
