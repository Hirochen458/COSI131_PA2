/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used read <file> and write everything into pipe.
 * Known bugs: None.
 */

package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements cat command - includes parsing cat command, detecting if input
 * filter was linked, as well as overriding necessary behavior of
 * SequentialFilter.
 * 
 * @author Chami Lamelas
 *
 */
public class CatFilter extends ConcurrentFilter {

	/**
	 * file to be read
	 */
	private File file;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * Constructs a CatFilter given a cat command.
	 * 
	 * @param cmd cmd is guaranteed to either be "cat" or "cat" followed by a space.
	 * @throws InvalidCommandException if the file in the command cannot be found
	 *                                  or if a file parameter was not provided
	 */
	public CatFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setPrevFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just "cat" =>
		// cat needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new InvalidCommandException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, filename will be trimmed string after space
		String dest = cmd.substring(spaceIdx + 1).trim();

		// create a File with the path to the file from the current working directory
		// since we interpret dest as a relative path
		file = new File(CurrentWorkingDirectory.get() + CurrentWorkingDirectory.getPathSeparator() + dest);

		// if this is not a valid File, throw an IAE with the appropriate message
		if (!file.isFile()) {
			throw new InvalidCommandException(Message.FILE_NOT_FOUND.with_parameter(cmd));
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
	 * Overrides {@link ConcurrentFilter#process()} to push lines of input from file
	 * specified in command to the output.
	 * And the method is been rewrite that is support the concurrency function as well as the background & foreground function.
	 */
	@Override
	public void process() {

		// open a Scanner on the File and read it line by line adding each line to the
		// output message queue with try catch surround. 
		Scanner s = null;
		try {
			s = new Scanner(file);
			while (s.hasNextLine()) {
				try {
					output.writeAndWait(s.nextLine());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//Add a poison pill
			try {
				output.writePoisonPill();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
		} finally {
			if (s != null) {
				s.close();
			}
		}

	}

	/**
	 * Overrides Concurrent.setPrevFilter() to not allow a {@link Filter} to
	 * be placed before {@link CatFilter} objects.
	 * 
	 * @throws InvalidCommandException - always
	 */
	@Override
	public void setPrevFilter(Filter prevFilter) {

		// as specified in the PDF throw an IAE with the appropriate message if we try
		// to link a Filter before this one (since cat doesn't take input)
		throw new InvalidCommandException(Message.CANNOT_HAVE_INPUT.with_parameter(command));

	}
	
}
