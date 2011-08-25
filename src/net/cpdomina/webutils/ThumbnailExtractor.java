package net.cpdomina.webutils;


import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import org.apache.sanselan.Sanselan;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

/**
 * Extracts a representative image from an HTML page. 
 * Looks for certain HTML tags that usually contain the most representative image, returning the biggest image in the page otherwise.
 * @author Pedro Oliveira
 *
 */
public class ThumbnailExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThumbnailExtractor.class);
	
	public static final int DEFAULT_READ_TIMEOUT = 1000;
	public static final int DEFAULT_CONNECT_TIMEOUT = 1000;

	private final int readTimeout;
	private final int connectTimeout;

	public ThumbnailExtractor() {
		this(DEFAULT_READ_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
	}

	/**
	 * @param readTimeout HTTP reading timeout
	 * @param connectTimeout HTTP connect timeout
	 */
	public ThumbnailExtractor(int readTimeout, int connectTimeout) {
		this.readTimeout = readTimeout;
		this.connectTimeout = connectTimeout;
	}
	
	/**
	 * Extracts the thumbnail for the given {@link URL}
	 * @param url
	 * @param timeout HTTP connection timeout while reading the URL
	 * @return
	 */
	public URL thumbnail(URL url, int timeout) {
		try {
			return thumbnail(Jsoup.connect(url.toString()).timeout(timeout).get());
		} catch (IOException e) {
			LOGGER.warn("Problem while acquiring image for {}", url, e);
		}
		return null;
	}

	/**
	 * Extracts the thumbnail for the given {@link Document}
	 * @param doc
	 * @return
	 */
	public URL thumbnail(Document doc) {

		//OpenGraph image tag
		Element img = doc.head().select("meta[property=og:image]").first();
		if(img != null) {
			String url = img.absUrl("content");
			if(!url.isEmpty()) {
				return from(url);
			}
		}
		
		//<link rel=”image_src” content|href="url">
		img = doc.head().select("link[rel=image_src]").first();
		if(img != null) {
			String url = img.absUrl("content");
			if(url.isEmpty()) {
				url = img.absUrl("href");
			}
			if(!url.isEmpty()) {
				return from(url);
			}
		}

		//Otherwise, biggest image
		return getBiggestImage(doc);
	}

	private URL getBiggestImage(Document doc) {

		Set<String> urls = Sets.newHashSet();
		for(Element img: doc.body().select("img[src]")) {			
			String src = img.absUrl("src");
			if(src != null) {			
				urls.add(src);
			}
		}

		//System.out.println(urls.size()+" urls");

		int maxDim = 0;
		URL thumbnail = null;

		for(String src: urls) {
			InputStream stream = null;
			try {
				URL url = new URL(src);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
				stream = conn.getInputStream();

				Dimension dims = Sanselan.getImageSize(stream, src);
				int dim = dims.height*dims.width;
				if(dim > maxDim) {
					maxDim = dim;
					thumbnail = url;
				}
			} catch (Exception e) {
				LOGGER.warn("Problem while acquiring image from {}", src, e);
			} finally {
				Closeables.closeQuietly(stream);
			}
		}

		return thumbnail;
	}

	private static URL from(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {}
		return null;
	}
}
