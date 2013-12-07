package reader;

import java.io.*;
import java.util.*;

public class FileBuffer {

	final static File folder = new File("./txt_files");

	public static ArrayList<String> read(int max_files) {
		ArrayList<String> sentences = new ArrayList<String>();

		ArrayList<String> prev_lines = new ArrayList<String>();
		BufferedReader reader = null;

		for (final File file : folder.listFiles()) {
			System.err.println("\tReading " + file.getName() + "...");
			try {
				String line = null;
				reader = new BufferedReader(new FileReader(file));

				while ((line = reader.readLine()) != null) {
					// Convert sentence to lower case and clean it
					line = Util.clean(line.toLowerCase());

					// If the line is blank then it's the end of the sentence
					if (line.length() == 0) {
						String sentence = makeSentence(prev_lines);

						if (sentence.length() != 0)
							sentences.add(sentence);

						prev_lines = new ArrayList<String>();
						continue;
					}

					// Split line into 'sentences' by end-of-line markers
					String[] lines = line.split("[?!.]");
					int num_lines = lines.length;

					if (num_lines == 1) 
						if (line.length() != 0)
							prev_lines.add(line);	

					// Sentence end somewhere in there
						else {
							// No matter what happens we always add the first statement 
							// to the ArrayList and create a sentence
							prev_lines.add(lines[0]);
							sentences.add(makeSentence(prev_lines));

							prev_lines = new ArrayList<String>();

							// Skip the first sentence
							for (int i = 1; i < num_lines; i++) {
								String sentence = lines[i];

								if (i < (num_lines - 1)) 
									if (sentence.length() != 0)
										sentences.add(sentence);

									else if (sentence.length() != 0)
										prev_lines.add(sentence);
							}
						}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null)
						reader.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (max_files-- == 0)
				break;
		}
		return sentences;
	}

	private static String makeSentence(ArrayList<String> lines) {
		String sentence = "";
		int len = lines.size();

		for (int i = 0; i < len; i++) 
			sentence += (i < len - 1) ? lines.get(i) + " " : lines.get(i);

		return sentence;
	}

}
