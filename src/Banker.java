import java.util.Arrays;

public class Banker extends Algorithm{
		
	private int[][] claims; 
	private int[][] originalClaims;
	
	private int usedResources;
	
	public Banker(){}
	public Banker(int T, int R){
		super(T,R);
		
		this.algorithmName = "Banker's";
		this.claims = new int[T][R]; 
		this.originalClaims = new int[T][R];
		
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
	public void run() {
		//run until all tasks are terminated
		
		//cycle 0 - 1
		while(terminatedTasks != T) {
			//go through the tasks
			for (int i = 0;i < T; i++) {
				//System.out.println(tasks.get(i));
				if (!running.contains(i)){
					running.add(i);
				}
			}
			remainingTasks = Task.getRemainingTasks(T, terminatedTasks);
			cycle = 1;
			while(cycle <= remainingTasks) {
				//System.out.println("During cycle " + cycle + " to " + (cycle+1));
				int taskNumber = running.poll();
				//System.out.println(taskNumber);
				if (tasks.get(taskNumber).size() == 0) {
					continue;
				} else {
					Task currentTask = tasks.get(taskNumber).get(0);

					//check if task is delayed
					if (currentTask.getDelay() != delayedTasks[taskNumber]) {
						delayedTasks[taskNumber]+=1;
						taskCycles[taskNumber]+=1;
					} 
					//if not delayed run normally
					else {
						runActivity(currentTask);  
					}
				}
				cycle++;
			}
			
			//make resources available
			resetResources();

		}
		printAlgorithm();
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
				if (claim > available[resourceType - 1]) {
					printAbortion(taskNumber, resourceType, claim);
					taskCycles[taskNumber] = Integer.MIN_VALUE; 
					tasks.get(taskNumber).removeAll(tasks.get(taskNumber));
					terminatedTasks++;
				} 
				//else initiate
				else {
					taskCycles[taskNumber]++;
					delayedTasks[taskNumber] = 0;
					tasksNums.put(taskNumber, new Integer[R]);
					Arrays.fill(tasksNums.get(taskNumber),new Integer(0));
					tasksNums.get(taskNumber)[resourceType - 1] = 0; 
					tasks.get(taskNumber).remove(0);
					//keep track of old claims 
					originalClaims[taskNumber][resourceType - 1] = claim;
					claims[taskNumber][resourceType - 1] = claim * -1;
				}
				break;
			case "request":
				//check if everything is safe else make it wait
				if (claims[taskNumber][resourceType - 1] < 0) {
					if(claim > available[resourceType-1]){
						taskCycles[taskNumber]++;
						tasksWaiting[taskNumber]++;
						running.add(taskNumber);
					}
				} 
				//else grant it AND MAKE SURE ITS safe
				else {
					//System.out.println(Arrays.toString(tasksNums.get(taskNumber)));
					tasksNums.get(taskNumber)[resourceType - 1] +=claim;
					taskCycles[taskNumber]++;
					tasks.get(taskNumber).remove(0);
					
					//keep track of available resources to ensure safety
					if (claims[taskNumber][resourceType - 1]  < 0) {
						claims[taskNumber][resourceType - 1]  += claim;
						available[resourceType - 1] -= claim;
					} else {
						claims[taskNumber][resourceType - 1]  -= claim;
						//make sure that each initiate doesn't exceed the bankers available
						if (claims[taskNumber][resourceType - 1]  < 0) {
							printClaimExceeds(taskNumber, claim);
							//abort, release all resources, and terminate
							taskCycles[taskNumber] = Integer.MIN_VALUE;
							for (int i = 0; i < R; i++) {
								released[i] += tasksNums.get(taskNumber)[i]; 
								available[i] += claims[taskNumber][resourceType - 1] ;
								tasksNums.get(taskNumber)[i] = 0;
							}
							tasks.get(taskNumber).removeAll(tasks.get(taskNumber)); //terminated
							terminatedTasks++;
						}
					}
	
					//check for safety
					for (int i = 0; i < R; i++) {
						int claimsCheck = claims[taskNumber][i];
						if(claimsCheck < 0)
							claimsCheck *= -1;
						if (available[i] >= claimsCheck) {
							usedResources++;
						}
					}
					if (usedResources == R) {
						for (int i = 0; i < R; i++) {
							int claimsCheck = claims[taskNumber][i];
							if(claimsCheck < 0)
								claimsCheck *= -1;
							claims[taskNumber][i] = claimsCheck;
							available[i] -= claims[taskNumber][i];
						}
					}
					usedResources = 0;
					delayedTasks[taskNumber] = 0;
				}
				break;
			case "release":
				//making sure claims and resources and safety are in check
				delayedTasks[taskNumber] = 0;  
				if (claims[taskNumber][resourceType - 1]  >= 0) {
					claims[taskNumber][resourceType - 1]  *= -1;
					for (int i = 0; i < R; i++) {
						int claimsCheck = claims[taskNumber][i];
						if(claimsCheck < 0)
							claimsCheck = claimsCheck *-1;
						if (claimsCheck == originalClaims[taskNumber][i] || claims[taskNumber][i] <= 0)
							usedResources++;
					}
					if (usedResources == R) {
						released[resourceType - 1]= released[resourceType - 1]+ claim;
						int i = 0;
						while(i < R) {  
							if (claims[taskNumber][i] > 0){
								claims[taskNumber][i] = claims[taskNumber][i]  * -1;
							}
							available[i] = available[i] - claims[taskNumber][i]; 
							i++;
						}
					}
					usedResources = 0;
					claims[taskNumber][resourceType - 1] = claims[taskNumber][resourceType - 1] - claim;
				} else { claims[taskNumber][resourceType - 1] = claims[taskNumber][resourceType - 1]- claim; }
				tasksNums.get(taskNumber)[resourceType - 1] -= claim;
				taskCycles[taskNumber]+=1;
				tasks.get(taskNumber).remove(0); //move on to next task
				break;
			case "terminate":
				delayedTasks[taskNumber] = 0;
				terminatedTasks++;
				tasks.get(taskNumber).remove(0); //acts like a queue
				break;
			default:
				System.err.println("Error: Activity input not recognized");	
				break;	
		}
	}
	
	/**
	 * functions to print back errors in Banker's algorithm
	 * 
	 */
	
	private void printAbortion(int taskNumber, int resourceType, int claim){
		int task = taskNumber + 1;
		System.out.println("Banker aborts task " + task + " before it begins:"
				+ "\n\tclaim for "+ resourceType + " ("+claim + ")" 
				+  "  exceeds the number of units present (" + available[resourceType-1] + ")");
		System.out.println();
	}
	private void printClaimExceeds(int taskNumber, int claim){
		System.out.println("During cycle "+ cycle + "-" + (cycle+ 1)+ " of Banker's algorithms" +
				"\n\tTask " + (taskNumber+ 1) +"\'s request exceeds its claim; aborted; "+ claim+ " units available next cycle");
		System.out.println();
	}	
}
