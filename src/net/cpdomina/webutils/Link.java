package net.cpdomina.webutils;

import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;

import com.google.common.base.Objects;

/**
 * A Link and its related information (url, thumbnail, title, description, author, and keywords).
 * 
 * @author Pedro Oliveira
 *
 */
public class Link implements Serializable {

	private final URL url;
	private URL thumbnail;
	private String title;
	private String description;
	private String author;
	private String[] keywords;

	public Link(URL url) {
		this.url = url;
		this.keywords = new String[0];
	}

	public Link(URL url, URL thumbnail, String title, String description) {
		this.url = url;
		this.thumbnail = thumbnail;
		this.title = title;
		this.description = description;
	}

	public Link(URL url, URL thumbnail, String title, String description, String author, String[] keywords) {
		this.url = url;
		this.thumbnail = thumbnail;
		this.title = title;
		this.description = description;
		this.author = author;
		this.keywords = keywords;
	}

	public URL getUrl() {
		return url;
	}

	public URL getThumbnail() {
		return thumbnail;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getAuthor() {
		return author;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setThumbnail(URL thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setKeywords(String[] keywords) {
		if(keywords != null) {
			this.keywords = keywords;
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
		.add("URL", url)
		.add("Thumbnail", thumbnail)
		.add("Title", title)
		.add("Description", description)
		.add("Author", author)
		.add("Keywords", Arrays.toString(keywords))
		.toString();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Link) {
			return ((Link)o).getUrl().equals(url);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}
	
	/**
	 * Return a decent title for this link: it's original title, if available & not empty, the url otherwise
	 * @return
	 */
	public String getDecentTitle() {
		if(title != null && title.length() > 0) {
			return title;
		} else {
			return url.toString();
		}
	}
}
