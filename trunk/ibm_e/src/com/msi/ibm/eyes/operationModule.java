package com.msi.ibm.eyes;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class operationModule {
	Calendar clndr = Calendar.getInstance();
	private int taskListCount = 0;
	private double taskListCoords[][] = new double[100][3];
	private Date taskListTimeExpire[] = new Date[100];
	private Boolean taskListTaskComplete[] = new Boolean[100];
	private int currentTask = 0;
	private double currentCoords[] = new double[3];
	private double currentDir[] = new double[3];
	

	/*
	1)tE.prio > tC.prio
	?!tE:go(T);nextTask();
	
	2)tE.prio < tC.prio (tC==true)
	?tCed():tE=getTime();
	?tE&!tCed():tE++;
	?tE:nextTask();go(T);

	tE(){ if tE>getTime() returne false; else returne true; }
	*/
	public void addTask(double x, double y, double h, Date tE, Boolean tC) {
		taskListCoords[taskListCount][0] = x;
		taskListCoords[taskListCount][1] = y;
		taskListCoords[taskListCount][2] = h;
		taskListTimeExpire[taskListCount] = tE;
		taskListTaskComplete[taskListCount] = tC;
		taskListCount++;
	}
	
	public void nextTask(){
		currentTask++;
	}
	
	public void go(){
		// A) get current Position 
		//get current position

		currentCoords[0]=shakeServ.locXe; //lat
		currentCoords[1]=shakeServ.locYe; //lng
		currentCoords[2]=100; //height
		
		//get current direction
		currentDir[0]=shakeServ.dir0;  //azimuth
		currentDir[1]=shakeServ.dir1;
		currentDir[2]=shakeServ.dir2;
		
		// B) get task Position 
		//taskListCoords[currentTask]
		/*
		taskListCoords[taskListCount][0] = x;
		taskListCoords[taskListCount][1] = y;
		taskListCoords[taskListCount][2] = h;
		taskListTimeExpire[taskListCount] = tE;
		taskListTaskComplete[taskListCount] = tC;
		*/

		// C) get CG array
		// format CG (pos(x,y,z,dir,time)[])
		// C.a get count steps of CG = f( (Task.posT-Task.pos0), (Task.tT-Task.t0),dt,F  'posT,tT:target position and time;dt:size of time step;F:value of control force;) 
		
		

		// D) run CG array
		
		// E) run CG.LOG 

		// F) nextTASK
		
		//get TargetDirection (atan(dx/dy))
		//
		//Log.d("opMod", "opMod_go:=shakeServ.locXe"+shakeServ.locXe+"_	shakeServ.locYe_"+shakeServ.locYe);
		//need to check!!!
		double targetDir= Math.atan2((taskListCoords[currentTask][1]-currentCoords[1]), taskListCoords[currentTask][0]-currentCoords[0]);
		Log.d("opMod", "opMod_go:targetDir="+targetDir);
		
		
		
		//get direction
		/*y(lat) x(lng) arctg(dlt x/dlt y)
		 * 
		 * */
		
		//send commands
	}

}
