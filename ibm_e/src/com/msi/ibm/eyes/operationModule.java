package com.msi.ibm.eyes;

import java.util.Calendar;
import java.util.Date;

public class operationModule {
	Calendar clndr = Calendar.getInstance();
	private int taskListCount = 0;
	private double taskListCoords[][] = new double[100][3];
	private Date taskListTimeExpire[] = new Date[100];
	private Boolean taskListTaskComplete[] = new Boolean[100];

	public void addTask(double x, double y, double h, Date tE, Boolean tC) {
		taskListCoords[taskListCount][0] = x;
		taskListCoords[taskListCount][1] = y;
		taskListCoords[taskListCount][2] = h;
		taskListTimeExpire[taskListCount] = tE;
		taskListTaskComplete[taskListCount] = tC;
		taskListCount++;
	}

}
