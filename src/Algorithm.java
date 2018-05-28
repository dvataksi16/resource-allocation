import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Algorithm {
	
	//used to print correct algo name in print results
	String algorithmName;
	
	protected int T; //total number of tasks
	protected int R; //total number of resources
	
	//keep track of total allocated resources available
	protected int[] available = new int[R];
	protected int[] released = new int[R];
	protected int cycle;
	
	
	//running total after each cycle
	protected int terminatedTasks = 0;
	protected int totalRuntime = 0; 
	protected int totalWaittime = 0;
	protected int remainingTasks = 0;
	
	//tracking tasks for each cycle
	HashMap<Integer, Integer[]> tasksNums = new HashMap<Integer, Integer[]>(T);
	HashMap<Integer, ArrayList<Task>> tasks;
	
	//what's left to run -- prioritizes tasks
	Queue<Integer> running = new LinkedList<Integer>();
	
	//create arrays for task cycles
	protected int[] taskCycles;
	protected int[] tasksWaiting;
	protected int[] delayedTasks;
	
	public Algorithm(){}
	
	public Algorithm(int T, int R){
		this.T = T;
		this.R = R;
		this.available = new int [R];
		this.released = new int[R];
		this.taskCycles = new int[T];
		this.tasksWaiting = new int[T];
		this.delayedTasks = new int[T];
		
		this.tasks = new HashMap<Integer, ArrayList<Task>>();
	}
	/**
	 * shows results
	 * Sample output:
	 *               FIFO                             BANKER'S
     *Task 1      3   0   0%           Task 1        3   0   0%
     *Task 2      3   0   0%           Task 2        5   2  40%
     *total       6   0   0%           total         8   2  25%
	 */
	
	public void printAlgorithm(){
		System.out.println(algorithmName+ " Algorithm:\n");
		System.out.println("Task #: Total  Wait  Wait%");
		int j = 1;
		for (Integer cycle : taskCycles) {
			System.out.print("Task " + j + ": ");
			if (cycle != Integer.MIN_VALUE) {
				System.out.printf("%5d %5d %5d", cycle, tasksWaiting[j-1], Math.round(tasksWaiting[j-1]/(float)cycle * 100));
				System.out.println("%");
				totalRuntime = totalRuntime + cycle;
				totalWaittime = totalWaittime + tasksWaiting[j-1];
			} else 
				System.out.println("     aborted");
			j++;
		} 
		System.out.printf("Total:  %5d %5d %5d", totalRuntime, totalWaittime, Math.round(totalWaittime/(float)totalRuntime * 100));
		System.out.println("%");
		System.out.println();
	}
	
	public void resetResources(){
		for (int i = 0; i < R; i++) {
			available[i] += released[i];
			released[i] = 0;
		}
	}
	
	public abstract void run();
	protected abstract void runActivity(Task task);
}
