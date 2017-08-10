package net.cpdomina.webutils;

import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class with simple facilitators for using String in web applications
 * @author Marcos Stefani Rosa
 *
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	/**
	 * @param object with model java class
	 */
	public static String getJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		String writeValueAsString = "";
		
		try {
			writeValueAsString = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return writeValueAsString;
	}
	
	/**
	 * @param string to encode in Base64
	 */
	public static String encode64(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes());
	}

}
