WebUtils
=========

Web-related utilities for Java.

URL Unshortener
---------------

Expand short urls. Works with all the major url shorteners (t.co, bit.ly, fb.me, is.gd, goo.gl, etc).

	//Read/connect timeout of 3s, cache 10k urls
	URLUnshortener u = new URLUnshortener(3000, 3000, 10000);	
	
	//Returns https://github.com/cpdomina/WebUtils
	URL url = u.expand(new URL("http://goo.gl/x3Ta9"));	


Thumbnail Extractor
-------------------

Extracts a representative image from an HTML page. Looks for certain HTML tags that usually contain the most representative image, returning the biggest image in the page in case they aren't there.

	//Read/connect image timeout of 3s
	ThumbnailExtractor extractor = new ThumbnailExtractor(3000, 3000);	 
	
	//Returns the GitHub logo URL
	URL url = extractor.thumbnail(new URL("https://github.com/cpdomina/WebUtils"), 3000);	
	
	
Link Parser
-----------
Parses relevant information (title, description, author, keywords, and thumbnail) from HTML pages.

	//Read/connect/parse timeout of 3s
	LinkParser parser = new LinkParser(3000, 3000, 3000);	
	
	//Parse URL info
	Link link = parser.parse(new URL("https://github.com/cpdomina/WebUtils"));	
	link.getTitle();
	link.getDescription();
	link.getAuthor();
	link.getKeywords();
	link.getThumbnail();