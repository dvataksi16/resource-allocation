import java.util.Arrays;


public class OptimisticManager extends Algorithm{

	private int deadlock = 0;
	private int deadlockIteration = 0;
	
	public OptimisticManager(){}
	public OptimisticManager(int T, int R){
		super(T,R);
		this.algorithmName = "FIFO";
	}
	/**
	 * adds in main class when running through the file since all resources are
	 * available at first
	 * @param index
	 * @param resource
	 */
	public void addToAvailable(int index, int resource){
		this.available[index] = resource;
	}
	
	/**
	 * @Override
	 * runs the algorithm 
	 */
	public void run () {	
		//while not all the tasksNums are terminated
		//cycle 0-1
		while(terminatedTasks != T) {
			deadlockIteration = 0;
			remainingTasks = Task.getRemainingTasks(T, terminatedTasks);

			//check if deadlock: if all tasksNums are blocked
			if (deadlock == remainingTasks) {
				runDeadlock();
			}
			deadlock = 0; 
			
			//go through the tasks
			for (int i = 0; i < T; i++) {
				//add previously terminated/aborted tasks back into queue in order
				if (!running.contains(i) && !(tasks.get(i).isEmpty()))
					running.add(i);
			}

			//go through remaining tasks
			remainingTasks = Task.getRemainingTasks(T, terminatedTasks);
			int task = 1;
			while (task <= remainingTasks) {
				//System.out.println("During cycle " + cycle + " to " + (cycle+1));
				//System.out.println(running.toString() + "  tasks: " + task);
				int taskNumber = running.poll();
				if (tasks.get(taskNumber).size() == 0) {
					continue;
				} else {
					Task currentTask = tasks.get(taskNumber).get(0); 
					//check delay
					if (currentTask.getDelay() != delayedTasks[taskNumber]) {
						taskCycles[taskNumber]+=1;
						delayedTasks[taskNumber]+=1;
					} 
					//if not delayed run normally
					else {
						delayedTasks[taskNumber] = 0;
						runActivity(currentTask);  
					}
				}
				task++;
			}

			//make all resources available
			resetResources();
			
			
		} 

		printAlgorithm();
	}
	/**
	 * run the deadlock first
	 */
	public void runDeadlock(){
		deadlockIteration = 1;
		for (int i = 0; i < T; i++) { 
			//check the running Queue for tasks that can be finished before going through the tasks
			if (!(tasks.get(i).isEmpty())) {

				taskCycles[i] = Integer.MIN_VALUE;
				for (int j = 0; j < R; j++) {
					available[j] += tasksNums.get(i)[j]; 
					tasksNums.get(i)[j] = 0;
				}
				tasks.get(i).removeAll(tasks.get(i));
				terminatedTasks ++;
				break; 
			}
		}
	}
	
	/**
	 * @Override
	 * runs the task based on the activity assigned to it
	 */
	protected void runActivity(Task task){
		String activity = task.getActivity().toLowerCase();
		int taskNumber = task.getTaskNumber() - 1;
		int resourceType = task.getResourceType();
		int claim = task.getClaim();
		
		switch(activity){
			case "initiate":
				tasksNums.put(taskNumber, new Integer[R]);
				Arrays.fill(tasksNums.get(taskNumber),new Integer(0));
				tasksNums.get(taskNumber)[resourceType - 1] = 0; 
				taskCycles[taskNumber]++;
				tasks.get(taskNumber).remove(0);
				break;
			case "request":
				//if can't grant the request, then make it wait
				if (claim > available[resourceType - 1]) {
					if (deadlockIteration == 0) {
						taskCycles[taskNumber]+=1;
						tasksWaiting[taskNumber]+=1;
					}
					running.add(taskNumber);
					deadlock++;
				} 
				//if it CAN grant the request
				else {
					tasksNums.get(taskNumber)[resourceType - 1] += claim;
					available[resourceType-1] = available[resourceType-1] - claim;
					taskCycles[taskNumber]++;
					tasks.get(taskNumber).remove(0); //move on to next task
				}
				break;
			case "release":
				released[resourceType - 1] += claim; 
				tasksNums.get(taskNumber)[resourceType - 1] = tasksNums.get(taskNumber)[resourceType - 1] - claim;
				taskCycles[taskNumber]++;
				tasks.get(taskNumber).remove(0); //move on to next task
				break;
			case "terminate":
				terminatedTasks++;
			    tasks.get(taskNumber).remove(0);
				break;
			default:
				System.err.println("Error: Activity input not recognized");
				break;
		}
	}	
}
