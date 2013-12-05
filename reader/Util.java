package reader;

import java.util.regex.*;

public class Util {
	
	private static Pattern spec_char = Pattern.compile("[\".!?',*_;:^%+=()\\[\\]~`\\{}]|[-]{2,}");
	private static Pattern spaces = Pattern.compile("\\s+|\\t+");
	private static Pattern strip = Pattern.compile("^\\s+|\\s+$");
	
	public static String strip(String word) {
		return strip.matcher(word).replaceAll("");
	}
	
	public static String clean(String str) {
		// Remove special characters
		String w_spaces = spec_char.matcher(str).replaceAll("");
				
		// Remove extra spaces + tabs and return
		w_spaces = spaces.matcher(w_spaces).replaceAll(" ");
				
		// Remove spaces at the beginning and end of sentence
		return strip.matcher(w_spaces).replaceAll("");
	}

}
