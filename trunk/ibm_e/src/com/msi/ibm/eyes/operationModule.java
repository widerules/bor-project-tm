package com.msi.ibm.eyes;

import java.util.Calendar;
import java.util.Date;

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
		//get current position
		currentCoords[0]=shakeServ.locXe;
		currentCoords[1]=shakeServ.locYe;
		currentCoords[2]=100;
		
		//get current direction
		currentDir[0]=shakeServ.dir0;
		currentDir[1]=shakeServ.dir1;
		currentDir[2]=shakeServ.dir2;
		
		//get target position
		//taskListCoords[currentTask]
		
		
		//get direction
		/*y(lat) x(lng) 
		 * 
		 * */
		
		//send commands
	}

}
