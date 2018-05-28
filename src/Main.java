import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**
**@author Denisa Vataksi
**/

/*
 * SAMPLE INPUT:
 * 	2 1 4
	initiate  1 0 1 4
	request   1 0 1 1
	release   1 0 1 1
	terminate 1 0 0 0
	initiate  2 0 1 4
	request   2 0 1 1
	release   2 0 1 1
	terminate 2 0 0 0
 */



public class Main{

	public static void main (String[] args) throws IOException { 
		
		File inputFile = new File(args[0]);
		//parse input  file
		Scanner file = new Scanner(inputFile);	
		
		int T = file.nextInt();
		int R = file.nextInt();
		
		Banker banker = new Banker(T,R);
		OptimisticManager fifo = new OptimisticManager(T, R);
		
		for (int i = 0; i < R; i++) {
			int availability = file.nextInt();
			banker.addToAvailable(i, availability);
			fifo.addToAvailable(i, availability);
		}
		
		
		HashMap<Integer, ArrayList<Task>> tasks = new HashMap<Integer, ArrayList<Task>>();
		HashMap<Integer, ArrayList<Task>> fifoTasks = new HashMap<Integer, ArrayList<Task>>();
		
		while(file.hasNext()){ //nextLine
			String activity = file.next();
			int taskNumber = file.nextInt();
			int delay = file.nextInt();
			int resourceType = file.nextInt();
			int claim = file.nextInt();
			Task task = new Task(activity, taskNumber, delay, resourceType, claim);
			
			if(!tasks.containsKey(taskNumber -1)){
				//System.out.println("does contains task" + taskNumber);
				ArrayList<Task> currentTasks = new ArrayList<Task>();
				currentTasks.add(task);
				tasks.put(taskNumber - 1, currentTasks);
			}
			else{
				//System.out.println(tasks.get(taskNumber - 1));
				ArrayList<Task> updatedTasks = tasks.get(taskNumber - 1);
				updatedTasks.add(task);
				tasks.put(taskNumber - 1, updatedTasks);
			}
		}
		
		banker.tasks = tasks;
		fifoTasks = copy(tasks);
		fifo.tasks = fifoTasks;
		
		/*
		 * run both algorithms
		 */
		fifo.run();
		banker.run();
		
		
		file.close();
	}
	/**
	 * allows both algorithms to use the same tasks map/list
	 * @param original hashmap
	 * @return deep copy of hashmap
	 */
	public static HashMap<Integer, ArrayList<Task>> copy(
		    HashMap<Integer, ArrayList<Task>> original)
		{
		    HashMap<Integer, ArrayList<Task>> copy = new HashMap<Integer, ArrayList<Task>>();
		    for (Map.Entry<Integer, ArrayList<Task>> entry : original.entrySet())
		    {
		        copy.put(entry.getKey(),
		           // Or whatever List implementation you'd like here.
		           new ArrayList<Task>(entry.getValue()));
		        }
		    return copy;
		    }
}