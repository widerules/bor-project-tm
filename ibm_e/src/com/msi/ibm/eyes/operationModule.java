package com.msi.ibm.eyes;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class operationModule {
	static Calendar clndr = Calendar.getInstance();
	private static int taskListCount = 0;
	private static double taskListCoords[][] = new double[100][3];
	private static double taskDir[] = new double[100];// 0-360
	private static long taskListTimeExpire[] = new long[100]; // time task in mSec
	private static Boolean taskListTaskComplete[] = new Boolean[100];
	private static int currentTask = 0;
	private static double currentCoords[] = new double[3];
	private static double currentDir[] = new double[3];

	private static long startCGtime = 0;
	private static long stopCGtime = 0;
	private static int na = 0;
	private static int F = 0;// + cw, - ccw; 0:off;1:min;2:half;3:cruise;4:max;
	private static int signF = 1;// cw:1;ccw:-1;
	// format CG (pos(x,y,z,dir,time)[])
	private static double CGlist[][] = new double[1000][5];
	private static long CGTimeList[] = new long[1000];
	private static int cgs = 0; // cgs current step of cg
	private int alphaprec = 10;// pogreshnost' ugla

	/*
	 * 1)tE.prio > tC.prio ?!tE:go(T);nextTask();
	 * 
	 * 2)tE.prio < tC.prio (tC==true) ?tCed():tE=getTime(); ?tE&!tCed():tE++;
	 * ?tE:nextTask();go(T);
	 * 
	 * tE(){ if tE>getTime() returne false; else returne true; }
	 */
	public static void addTask(double x, double y, double h, int dir,long tE, Boolean tC) {
		taskListCoords[taskListCount][0] = x;
		taskListCoords[taskListCount][1] = y;
		taskListCoords[taskListCount][2] = h;
		taskDir[taskListCount] = dir;
		taskListTimeExpire[taskListCount] = tE;
		taskListTaskComplete[taskListCount] = tC;
		taskListCount++;

	}

	public static void nextTask() {
		currentTask++;

		// A) get current Position
		// get current position

		currentCoords[0] = shakeServ.locXe; // lat
		currentCoords[1] = shakeServ.locYe; // lng
		currentCoords[2] = 100; // height

		// get current direction
		currentDir[0] = shakeServ.dir0; // azimuth
		currentDir[1] = shakeServ.dir1;
		currentDir[2] = shakeServ.dir2;

		// B) get task Position
		// taskListCoords[currentTask]
		/*
		 * taskListCoords[taskListCount][0] = x;
		 * taskListCoords[taskListCount][1] = y;
		 * taskListCoords[taskListCount][2] = h; taskDir[taskListCount]=0;
		 * taskListTimeExpire[taskListCount] = tE;
		 * taskListTaskComplete[taskListCount] = tC;
		 */

		// C) get CG array
		// format CG (pos(x,y,z,dir,time)[])
		// C.a get count steps of CG = f( (Task.posT-Task.pos0),
		// (Task.tT-Task.t0),dt,F 'posT,tT:target position and time;dt:size of
		// time step;F:value of control force;)
		// two props F[0-3]~rpm[] (F0 -minEffective, F1= F2/F0, F2 - normal, F3
		// -max), rpm~[0-1024]:[0:100;1:400;2:700;3:970;] )
		// dt = 1000 mS, normal omega w(grad/sec)=10 grad/sec;
		long dt = 1000;

		// delta alpha da=taskDir[taskListCount]-currentDir[0];
		double da = taskDir[taskListCount] - currentDir[0];
		if (da > 180) {
			signF = -1;
		} else {
			signF = 1;
		}

		// step alpha sa = 10;
		double sa = 10;
		// na count of step alpha
		na = (int) (da / sa);

		int i = 0;

		// format CG (pos(x,y,z,dir,time)[])
		// double CGlist[][] = new double[1000][5];
		// long CGTimeList[] = new long[1000];
		for (i = 0; i < na; i++) {
			CGlist[i][0] = taskListCoords[taskListCount][0];// x
			CGlist[i][1] = taskListCoords[taskListCount][1];// y
			CGlist[i][2] = taskListCoords[taskListCount][2];// z
			CGlist[i][3] = currentDir[0] + i * sa;// dir
			CGTimeList[i] = i * dt;
			// time
		}
		na++;
		CGlist[na][0] = taskListCoords[taskListCount][0];// x
		CGlist[na][1] = taskListCoords[taskListCount][1];// y
		CGlist[na][2] = taskListCoords[taskListCount][2];// z
		CGlist[na][3] = taskDir[taskListCount];// dir
		CGTimeList[na] = na * dt;

		// init go
		startCGtime = clndr.getTimeInMillis();
		stopCGtime = startCGtime + taskListTimeExpire[currentTask];
		Log.d("", "oM_nextTask_go:startT:" + startCGtime+";stopT:"+stopCGtime +";na:"+na);
		go();

	}

	public static void go() {
		// D) run CG array
		long ct = clndr.getTimeInMillis();// current time

		// cgs current step of cg
		int prevcgs = cgs;
		cgs = (int) ((ct - startCGtime) * na / taskListTimeExpire[taskListCount]);

		if (ct < stopCGtime) {
			if (cgs == 0) {
				F = signF * 1;
			} else {
				// if (prevcgs!=cgs){//calc F once per CGperiod
				// correct F as delta between theor and real alpha
				// double da=(CGlist[cgs][3]-shakeServ.dir0)*signF;
				// if (da>alphaprec){F++;}
				// get w(i) ugl.skorost' i & i-1
				Log.d("", "oM_go_:cgs:" + cgs+";cgdir_i:"+CGlist[cgs][3]+";cgdir_i-1:"+CGlist[cgs-1][3]);
				Log.d("", "oM_go_:cgs:" + cgs+";cgtm_i:"+CGTimeList[cgs]+";cgtm_i-1:"+CGTimeList[cgs-1]);
				double wi = (CGlist[cgs][3] - CGlist[cgs - 1][3])
						/ (CGTimeList[cgs] - CGTimeList[cgs - 1]);
				double wci = (shakeServ.dir0 - CGlist[cgs - 1][3])
						/ (ct - startCGtime - CGTimeList[cgs - 1]);
				Log.d("", "oM_go_wi:" + wi);
				Log.d("", "oM_go_wci:" + wci);

				if (wci > wi) {
					F--;
				} else {
					F--;
				}
				// limi F
				if (F < -4)
					F = -4;
				if (F > 4)
					F = 4;
				Log.d("", "oM_go_F:" + F);
				// send To Ardu
				// }
			}

		} else {
			// debug
			Log.d("", "oM_go:stop");

		}

		// E) run CG.LOG

		// F) nextTASK

		// get TargetDirection (atan(dx/dy))
		//
		// Log.d("opMod",
		// "opMod_go:=shakeServ.locXe"+shakeServ.locXe+"_	shakeServ.locYe_"+shakeServ.locYe);
		// need to check!!!
		// double targetDir = Math.atan2(
		// (taskListCoords[currentTask][1] - currentCoords[1]),
		// taskListCoords[currentTask][0] - currentCoords[0]);
		// Log.d("opMod", "opMod_go:targetDir=" + targetDir);

		// get direction
		/*
		 * y(lat) x(lng) arctg(dlt x/dlt y)
		 */

		// send commands
	}
}