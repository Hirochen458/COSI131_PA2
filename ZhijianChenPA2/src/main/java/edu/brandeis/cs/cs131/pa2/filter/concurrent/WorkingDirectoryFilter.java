/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used to implement pwd function.
 * Known bugs: None.
 */


package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements pwd command - overrides necessary behavior of ConcurrentFilter
 * 
 * @author Chami Lamelas
 *
 */
public class WorkingDirectoryFilter extends ConcurrentFilter {

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs a pwd filter.
	 * @param cmd cmd is guaranteed to either be "pwd" or "pwd" surrounded by whitespace
	 */
	public WorkingDirectoryFilter(String cmd) {
		super();
		command = cmd;
	}

	/**
	 * Overrides {@link ConcurrentFilter#process()} by adding
	 * {@link SequentialREPL#currentWorkingDirectory} to the output queue
	 * And the method is been rewrite that is support the concurrency function as well as the background & foreground function.
	 */
	@Override
	public void process() {
		try {
			this.output.writeAndWait(CurrentWorkingDirectory.get());
			this.output.writePoisonPill();
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

	/**
	 * Overrides equentialFilter.setPrevFilter() to not allow a {@link Filter} to be
	 * placed before {@link WorkingDirectoryFilter} objects.
	 * 
	 * @throws InvalidCommandException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {
		throw new InvalidCommandException(Message.CANNOT_HAVE_INPUT.with_parameter(command));
	}
}
