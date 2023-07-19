/**
 * Zhijian Chen
 * Chen5340@brandeis.edu
 * Mar 12th 2023
 * PA2
 * This class is used to run the whole program.
 * Known bugs: None.
 */


package edu.brandeis.cs.cs131.pa2.filter.concurrent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import edu.brandeis.cs.cs131.pa2.filter.Filter;
import edu.brandeis.cs.cs131.pa2.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 */
public class ConcurrentREPL{

	/**
	 * pipe string
	 */
	static final String PIPE = "|";

	/**
	 * redirect string
	 */
	static final String REDIRECT = ">";
	
	/**
	 * Background String
	 */
	static final String BACKGROUND = "&";
	
	/**
	 * This is the flag for is the program should run in background.
	 */
	public static Boolean background = false;
	
	
	/**
	 * This is the arraylist of jobs name.
	 */
	public static List<String> jobs = new ArrayList<String>();
	
	/**
	 * THis is the Arraylist of list of threads.
	 */
	public static List<List<Thread>> threadJobs = new ArrayList<List<Thread>>();
	
	/**
	 * The main method that will execute the REPL loop
	 * And this main method is been rewrite that is support the concurrency function as well as the background & foreground function.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {

		Scanner consoleReader = new Scanner(System.in);
		System.out.print(Message.WELCOME);

		while (true) {
			System.out.print(Message.NEWCOMMAND);
			// read user command, if its just whitespace, skip to next command
			String cmd = consoleReader.nextLine();
			
			if (cmd.trim().isEmpty()) {
				continue;
			}

			// exit the REPL if user specifies it
			if (cmd.trim().equals("exit")) {
				jobs.clear();
				threadJobs.clear();
				break;
			}
			
			//check if repl_jobs exists and print out the jobs.
			if(cmd.trim().equals("repl_jobs")) {
				if(threadJobs.size()!=0) {
					for(int i = 0; i < threadJobs.size(); i++) {
						List<Thread> listOfThreads = threadJobs.get(i);
						if(listOfThreads != null && listOfThreads.size()!=0) {

							if(listOfThreads.get(listOfThreads.size()-1).isAlive()) {
								System.out.println("\t" + (i+1) + ". "+ jobs.get(i));
							}
						}
					}
				}
				continue;
			}
			
			//Check if the kill command exists and kill all the threads related with this command.
			if(cmd.trim().split(" ")[0].equals("kill")) {
				if(threadJobs.size()!=0) {
					int index =Integer.valueOf(cmd.trim().split(" ")[1]);
					jobs.set(index-1, null);
					List<Thread> listOfThreads = threadJobs.get(index-1);
					if(listOfThreads.size()!=0) {
						for(int k = 0; k < listOfThreads.size(); k++) {
							listOfThreads.get(k).interrupt();
						}	
					}
					threadJobs.set(index-1, null);
					
				}
				continue;
			}
			try {
				// parse command into sub commands, then into Filters, add final PrintFilter if
				// necessary, and link them together - this can throw IAE so surround in
				// try-catch so appropriate Message is printed (will be the message of the IAE)
				List<ConcurrentFilter> filters = ConcurrentCommandBuilder.createFiltersFromCommand(cmd);
				
				//This is the arraylist of threads
				List<Thread> threads = new ArrayList<Thread>();
				

				// call process on each of the filters to have them execute
				for (ConcurrentFilter filter : filters) {
				
					int i = 1;
					Thread thread = new Thread(filter, Integer.toString(i));
					thread.start();
					threads.add(thread);
					i++;
					//filter.process();
				}
				Thread last = threads.get(threads.size()-1);
				
				//check if its background command.
				if (background == true) {
					threadJobs.add(threads);
					jobs.add(cmd);
				}
				
				//to join in order to run sequentially. 
				try {
					if (background != true) {
						last.join();
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (InvalidCommandException e) {
				System.out.print(e.getMessage());
			}

		}
		System.out.print(Message.GOODBYE);
		consoleReader.close();

	}
}
