/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used to implement the Head function.
 * Known bugs: None.
 */


package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements head command - overrides necessary behavior of ConcurrentFilter
 * 
 * @author Chami Lamelas
 *
 */
public class HeadFilter extends ConcurrentFilter {

	/**
	 * number of lines read so far
	 */
	private int numRead;

	/**
	 * number of lines passed to output via head
	 */
	private static int LIMIT = 10;

	/**
	 * Constructs a head filter.
	 */
	public HeadFilter() {
		super();
		numRead = 0;
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} to only add up to 10 lines to
	 * the output queue.
	 * And the method is been rewrite that is support the concurrency function as well as the background & foreground function.
	 */
	@Override
	public void process() {
		try {
			while (numRead < LIMIT) {
				String line = input.readAndWait();
				if(line == null) {
					break;
				}else {
					output.writeAndWait(line);
					numRead++;
				}
				
			}
			output.writePoisonPill();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
	}

	/**
	 * Overrides ConcurrentFilter.processLine() - doesn't do anything.
	 */
	@Override
	protected String processLine(String line) {
		return null;
	}

}
