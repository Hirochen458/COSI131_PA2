/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used to print the final results in pipe into console or STDOUT.
 * Known bugs: None.
 */


package edu.brandeis.cs.cs131.pa2.filter.concurrent;


/**
 * Implements printing as a {@link ConcurrentFilter} - overrides necessary
 * behavior of ConcurrentFilter
 * 
 * @author Chami Lamelas
 *
 */
public class PrintFilter extends ConcurrentFilter {

	/**
	 * Overrides ConcurrentFilter.processLine() to just print the line to stdout.
	 */
	@Override
	protected String processLine(String line) {

		System.out.println(line);
		return null;
	}

}
