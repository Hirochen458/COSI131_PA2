/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used to implement the ">" function.
 * Known bugs: None.
 */


package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.brandeis.cs.cs131.pa2.filter.CurrentWorkingDirectory;
import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * Implements redirection as a {@link ConcurrentFilter} - overrides necessary
 * behavior of ConcurrentFilter
 * 
 * @author Chami Lamelas
 *
 */
public class RedirectFilter extends ConcurrentFilter {

	/**
	 * destination of redirection
	 */
	private String dest;

	/**
	 * command that was used to construct this filter
	 */
	private String command;

	/**
	 * writer for writing - set in process(), leave as null till then
	 */
	private PrintWriter printWriter;

	/**
	 * Constructs a RedirectFilter given a >.
	 * 
	 * @param cmd cmd is guaranteed to either be ">" or ">" followed by a space.
	 * @throws InvalidCommandException if a file parameter was not provided
	 */
	public RedirectFilter(String cmd) {
		super();

		// save command as a field, we need it when we throw an exception in
		// setNextFilter
		command = cmd;

		// find index of space, if there isn't a space that means we got just ">" =>
		// > needs a parameter so throw IAE with the appropriate message
		int spaceIdx = cmd.indexOf(" ");
		if (spaceIdx == -1) {
			throw new InvalidCommandException(Message.REQUIRES_PARAMETER.with_parameter(cmd));
		}

		// we have a space, filename will be trimmed string after space
		String relativeDest = cmd.substring(spaceIdx + 1).trim();

		// set redirection destination as cwd joined with relative destination file
		dest = CurrentWorkingDirectory.get() + CurrentWorkingDirectory.getPathSeparator() + relativeDest;

		// check if the destination file exists, if so delete it b/c > overwrites the
		// destination file if one exists
		File destFile = new File(dest);
		if (destFile.isFile()) {
			destFile.delete();
		}
//		try {
//			destFile.createNewFile();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * Overrides ConcurrentFilter.process to close write stream
	 * And the method is been rewrite that is support the concurrency function as well as the background & foreground function.
	 */
	@Override
	public void process() {
		try {
			printWriter = new PrintWriter(new FileWriter(new File(dest)));
			//super.process();
			try {
				while(true) {
					String line = input.readAndWait();
					if(line == null) {
						break;
					}else {
						printWriter.println(line);
						//String processedLine = processLine(line);
					}
				}
				printWriter.close();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			printWriter.close();
		} catch (IOException e) {
			// do nothing we know file exists
		}

	}

	/**
	 * Overrides ConcurrentFilter.processLine() to just write the line to the
	 * destination file. Returns null so {@link ConcurrentFilter#process()} doesn't
	 * add anything to the output.
	 */
	@Override
	protected String processLine(String line) {
		printWriter.println(line);
		return null;
	}

	/**
	 * Overrides ConcurrentFilter.setPrevFilter() to not allow a {@link Filter} to
	 * be placed after {@link RedirectFilter} objects.
	 * 
	 * @throws InvalidCommandException - always
	 */
	@Override
	public void setNextFilter(Filter nextFilter) {
		throw new InvalidCommandException(Message.CANNOT_HAVE_OUTPUT.with_parameter(command));
	}

}
