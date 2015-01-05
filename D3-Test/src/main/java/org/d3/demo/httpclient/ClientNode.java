package org.d3.demo.httpclient;

import java.util.HashMap;
import java.util.Map;

public class ClientNode {
	
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
	
	private Map<String, Action> taskTypes;
	private Action act;
	
	public ClientNode(){
		taskTypes = new HashMap<String, ClientNode.Action>();
		act = new Action();
	}
	
	public void add(String taskType, String action, double point){
		
		Action act = taskTypes.get(taskType);
		if(act == null){
			act = new Action();
			taskTypes.put(taskType, act);
		}
		
		switch(action){
			case COMPLETE:
				act.complete += point;
				this.act.complete += point;
				break;
			case FAIL:
				act.fail += point;
				this.act.fail += point;
				break;
			case RETRY:
				act.retry += point;
				this.act.retry += point;
				break;
			case DUPLICATE:
				act.duplicate += point;
				this.act.duplicate += point;
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
		return "ClientNode [taskTypes=" + taskTypes + ", act=" + act + "]";
	}
}
