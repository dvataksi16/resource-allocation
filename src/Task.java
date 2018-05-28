
public class Task{

	private String activity;
	private int taskNumber;
	//ignore for initiate activities
	private int delay;
	private int resourceType;

	/*	claim used differently for different activities

		for initiate-- initial claim
		for request-- number requested
		for released-- number released
		for terminated-- unused, ignore
	*/
	private int claim;

	Task(String activity, int taskNumber, int delay, int resourceType, int claim){
		this.activity = activity;
		this.taskNumber = taskNumber;
		this.delay = delay;
		this.resourceType = resourceType;
		this.claim = claim;
	}
	
	//class variable to calculate number of tasks remaining in both algos
	public static int getRemainingTasks(int totalTasks, int terminatedTasks){
		return totalTasks - terminatedTasks;
	}
	
	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getResourceType() {
		return resourceType;
	}

	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
	}

	public int getClaim() {
		return claim;
	}

	public void setClaim(int claim) {
		this.claim = claim;
	}
}