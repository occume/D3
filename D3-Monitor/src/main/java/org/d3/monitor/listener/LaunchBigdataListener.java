package org.d3.monitor.listener;

import org.d3.monitor.util.CmdExecutor;
import org.d3.monitor.util.EventListener;
import org.d3.monitor.warrior.Warrior.Event;

public class LaunchBigdataListener implements EventListener {

	public static final Event care = Event.START;
	@Override
	public void onEvent(Event event) {
		if(event.equals(care)){
			System.out.println("LaunchBigdataListener is trigged");
//			BigdataUpdater.updateAndLauncher();
		}
	}
	
	public static class BigdataUpdater{
		public static void updateAndLauncher(){
			String cmd = "rsync -r 10.8.90.87::octopus_bigdata /home/deploy/bigdata";
			CmdExecutor.execute(cmd);
			cmd = "sudo /home/deploy/bigdata/bigdata.sh start";
			CmdExecutor.execute(cmd);
		}
		public static void updateAndRestart(){
			String cmd = "rsync -r 10.8.90.87::octopus_bigdata /home/deploy/bigdata";
			CmdExecutor.execute(cmd);
			cmd = "sudo /home/deploy/bigdata/bigdata.sh stop";
			CmdExecutor.execute(cmd);
			cmd = "sudo /home/deploy/bigdata/bigdata.sh start";
			CmdExecutor.execute(cmd);
		}
	}

}
