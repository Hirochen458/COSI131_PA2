/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used get the last elements from a file 
 * Known bugs: None.
 */

package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements tail command - overrides necessary behavior of ConcurrentFilter
 * 
 * @author Chami Lamelas
 *
 */
public class TailFilter extends ConcurrentFilter {

	/**
	 * number of lines passed to output via tail
	 */
	private static int LIMIT = 10;

	/**
	 * line buffer
	 */
	private List<String> buf;

	/**
	 * Constructs a tail filter.
	 */
	public TailFilter() {
		super();
		buf = new LinkedList<String>();
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} to only add up to 10 lines to
	 * the output queue.
	 * And the method is been rewrite that is support the concurrency function as well as the background & foreground function.
	 */
	@Override
	public void process() {

		// until the input is empty, add line to end of buffer if buffer reached LIMIT,
		// remove the head (LinkedList makes this O(1)), could also use Queue/Deque
		// removing the head removes the oldest line seen so far - this way buf will
		// hold the last 10 lines of the input (or as many lines were in the input if
		// the input had < 10 lines)
		// And the method is been rewrite that is support the concurrency function. 
		try {
			
		
			while (true) {
				String line = input.readAndWait();
				if (line == null) {
					break;
				}else {
					buf.add(line);
					if (buf.size() > LIMIT) {
						buf.remove(0);
					}
				}
			}
		
		// once we're done with the input (and have identified the last 10 lines), add
		// them to the output in the order in which they appeared in the input
			while (!buf.isEmpty()) {
				String test = buf.remove(0);
				output.writeAndWait(test);
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
