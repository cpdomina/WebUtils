package net.cpdomina.webutils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

/**
 * Expand short urls. Works with all the major url shorteners (t.co, bit.ly, fb.me, is.gd, goo.gl, etc).
 * @author Pedro Oliveira
 *
 */
public class URLUnshortener {

	private static final Logger LOGGER = LoggerFactory.getLogger(URLUnshortener.class);

	public static final int DEFAULT_CONNECT_TIMEOUT = 1000;
	public static final int DEFAULT_READ_TIMEOUT = 1000;
	public static final int DEFAULT_CACHE_SIZE = 10000;

	private final int connectTimeout;
	private final int readTimeout;
	private final LRUCache<URL, URL> cache;

	public URLUnshortener() {
		this(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_CACHE_SIZE);
	}

	/**
	 * @param connectTimeout HTTP connection timeout, in ms
	 * @param readTimeout HTTP read timeout, in ms
	 * @param cacheSize Number of resolved URLs to maintain in cache
	 */
	public URLUnshortener(int connectTimeout, int readTimeout, int cacheSize) {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.cache = LRUCache.build(cacheSize);
	}

	/**
	 * Expand the given short {@link URL}
	 * @param address
	 * @return
	 */
	public URL expand(URL address) {
		
		//Check cache
		URL inCache = cache.get(address);
		if(inCache != null) {
			return inCache;
		}

		//Connect & check for the location field
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) address.openConnection(Proxy.NO_PROXY);
			connection.setConnectTimeout(connectTimeout);
			connection.setInstanceFollowRedirects(false);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			String expandedURL = connection.getHeaderField("Location");
			if(expandedURL != null) {
				URL expanded = new URL(expandedURL);
				cache.put(address, expanded);
				return expanded;
			}
		} catch (Throwable e) {
			LOGGER.warn("Problem while expanding {}", address, e);
		} finally {
			try {
				if(connection != null) {
					Closeables.closeQuietly(connection.getInputStream());
				}
			} catch (IOException e) {
				LOGGER.warn("Unable to close connection stream", e);
			}
		}

		return address;
	}
}
