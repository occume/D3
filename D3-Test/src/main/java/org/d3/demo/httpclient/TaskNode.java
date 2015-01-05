package org.d3.demo.httpclient;

public class TaskNode {
	
	private static class Action{
		double complete;
		double fail;
		double retry;
		double duplicate;
		@Override
		public String toString() {
			return "Action [complete=" + complete + ", fail=" + fail
					+ ", retry=" + retry + ", duplicate=" + duplicate + "]";
		}
	}
	
	private static final String COMPLETE 	= "complete";
	private static final String FAIL 		= "fail";
	private static final String RETRY 		= "retry";
	private static final String DUPLICATE 	= "duplicate";
	
	private static Action reader = new Action();
	private static Action writer = new Action();
	
	public static Action reader(){
		return reader;
	}
	
	public static void reader(Action action){
		reader = action;
	}
	
	public static Action writer(){
		return writer;
	}
	
	public static void writer(Action action){
		writer = action;
	}
	
	public static Action newAction(){
		return new Action();
	}
	
	private Action act;
	
	public TaskNode(){
		act = new Action();
	}
	
	public void add(String taskType, String action, double point){
		
		switch(action){
			case COMPLETE:
				act.complete += point;
				break;
			case FAIL:
				act.fail += point;
				break;
			case RETRY:
				act.retry += point;
				break;
			case DUPLICATE:
				act.duplicate += point;
				break;
		}
		
	}
	
	public double all(){
		return (act.complete + act.duplicate + act.fail + act.retry);
	}
	
	public double fail(){
		return (act.fail + act.retry);
	}
	
	public double okRate(){
		
		if(all() == 0){
			return 0;
		}
		
		double rate = fail() / all();
		return rate;
	}

	@Override
	public String toString() {
		return "ClientNode [act=" + act + "]";
	}
}
