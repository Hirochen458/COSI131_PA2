/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used to implement the wc function.
 * Known bugs: None.
 */


package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements wc command - overrides necessary behavior of ConcurrentFilter
 * 
 * @author Chami Lamelas
 *
 */
public class WordCountFilter extends ConcurrentFilter {

	/**
	 * word count in input - words are strings separated by space in the input
	 */
	private int wordCount;

	/**
	 * character count in input - includes ws
	 */
	private int charCount;

	/**
	 * line count in input
	 */
	private int lineCount;

	/**
	 * Constructs a wc filter.
	 */
	public WordCountFilter() {
		super();
		wordCount = 0;
		charCount = 0;
		lineCount = 0;
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} by computing the word count,
	 * line count, and character count then adding the string with line count + " "
	 * + word count + " " + character count to the output queue
	 * And the method is been rewrite that is support the concurrency function as well as the background & foreground function.
	 */
	@Override
	public void process() {
		try {
			while(true) {
				String line = input.readAndWait();
				if(line == null) {
					break;
				}else {
					processLine(line);
				}
			}
			output.writeAndWait(lineCount + " " + wordCount + " " + charCount);
			output.writePoisonPill();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Overrides ConcurrentFilter.processLine() - updates the line, word, and
	 * character counts from the current input line
	 */
	@Override
	protected String processLine(String line) {
		lineCount++;
		wordCount += line.split(" ").length;
		charCount += line.length();
		return null;
	}

}
