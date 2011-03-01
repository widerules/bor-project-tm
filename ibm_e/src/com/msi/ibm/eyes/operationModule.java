package com.msi.ibm.eyes;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class operationModule {
	static Calendar clndr = Calendar.getInstance();
	private static int taskListCount = 0;
	private static double taskListCoords[][] = new double[100][3];
	private static double taskDir[] = new double[100];// 0-360
	private static long taskListTimeExpire[] = new long[100]; // time task in
	// mSec
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
	private static long ct = 0;

	private static long curTime = 0;
	private static long preTime = 0;

	private static double curDir=0;
	private static double preDir=0;
	/*
	 * 1)tE.prio > tC.prio ?!tE:go(T);nextTask();
	 * 
	 * 2)tE.prio < tC.prio (tC==true) ?tCed():tE=getTime(); ?tE&!tCed():tE++;
	 * ?tE:nextTask();go(T);
	 * 
	 * tE(){ if tE>getTime() returne false; else returne true; }
	 */
	public static void addTask(double x, double y, double h, int dir, long tE,
			Boolean tC) {
		taskListCount++;
		taskListCoords[taskListCount][0] = x;
		taskListCoords[taskListCount][1] = y;
		taskListCoords[taskListCount][2] = h;
		taskDir[taskListCount] = dir;
		taskListTimeExpire[taskListCount] = tE;
		taskListTaskComplete[taskListCount] = tC;
		Log.d("", "oM_nextTask_addTask:x:" + taskListCoords[taskListCount][0]
				+ ";taskDir:" + taskDir[taskListCount] + ";tE:"
				+ taskListTimeExpire[taskListCount]);
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
		
		/*
		long dt = 1000;

		// delta alpha da=taskDir[taskListCount]-currentDir[0];
		if (taskDir[taskListCount]==0){taskDir[taskListCount]=1;}
		double da = taskDir[taskListCount] - currentDir[0];
		Log.d("", "oM_nextTask_go:taskDir[taskListCount]:"
				+ taskDir[taskListCount] + ";currentDir[0]:" + currentDir[0]);
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
		*/
		// init go
		startCGtime = System.currentTimeMillis();// clndr.getTimeInMillis();
		stopCGtime = startCGtime + taskListTimeExpire[currentTask];
		Log.d("", "oM_nextTask_go:startT:" + startCGtime + ";stopT:"
				+ stopCGtime + ";na:" + na);
		go();

	}
	

	public static void go() {
		Log.d("", "oM_go:start;");
		try {
			// D) run CG array
			ct = System.currentTimeMillis();// clndr.getTimeInMillis();//
			// current time
			long dt = stopCGtime - ct;
			Log.d("", "oM_go_:ct:" + ct + ";stopCGtime:" + stopCGtime + ";dt:"
					+ dt + ";");

			// cgs current step of cg
			int prevcgs = cgs;
			cgs = (int) ((ct - startCGtime) * na / taskListTimeExpire[taskListCount]);
			Log.d("", "oM_go_:cgs:" + cgs
					+ ";taskListTimeExpire[taskListCount]:"
					+ taskListTimeExpire[taskListCount] + ";");

			if (ct < stopCGtime) {
				/*
				if (cgs == 0) {//???
					F = signF * 1;
					Log.d("", "oM_go_F:" + F);
				} else {
					// if (prevcgs!=cgs){//calc F once per CGperiod
					// correct F as delta between theor and real alpha
					// double da=(CGlist[cgs][3]-shakeServ.dir0)*signF;
					// if (da>alphaprec){F++;}
					// get w(i) ugl.skorost' i & i-1
					Log.d("", "oM_go_:cgs:" + cgs + ";cgdir_i:"
							+ CGlist[cgs][3] + ";cgdir_i-1:"
							+ CGlist[cgs - 1][3]);
					Log.d("", "oM_go_:cgs:" + cgs + ";cgtm_i:"
							+ CGTimeList[cgs] + ";cgtm_i-1:"
							+ CGTimeList[cgs - 1]);
					double wi = signF * (CGlist[cgs][3] - CGlist[cgs - 1][3])
							/ (CGTimeList[cgs] - CGTimeList[cgs - 1]);
					
					double tha=wi*(ct - startCGtime - CGTimeList[cgs - 1])+ CGlist[cgs - 1][3];//theo alpa in current time
					double wci = (shakeServ.dir0 - CGlist[cgs - 1][3])/ (ct - startCGtime - CGTimeList[cgs - 1]);
					double log_cda=shakeServ.dir0 - CGlist[cgs - 1][3];
					long log_cdt=(ct - startCGtime );
					Log.d("", "oM_go_cda:" + log_cda+";cdir0:"+shakeServ.dir0+";CGdir[cgs - 1]:"+CGlist[cgs - 1][3]+";tha:"+tha+";curRelTime:"+log_cdt);
					Log.d("", "oM_go_wi:" + wi);
					Log.d("", "oM_go_wci:" + wci);

					//if (wci > wi) {
					if (shakeServ.dir0>tha){
						F--;
					} else {
						F++;
					}
					// Limit F
					if (F < -1)
						F = -1;
					if (F > 1)
						F = 1;
					Log.d("", "oM_go_F:" + F);
					// send To Ardu
					// }
				}
				*/
				preTime=curTime;
				curTime=ct;
				
				preDir=curDir;
				curDir=shakeServ.dir0;
				
				Log.d("", "oM_go-:================================"+curTime);
				Log.d("", "oM_go-pT:" + preTime+"; cT:"+curTime);
				Log.d("", "oM_go-pD:" + preDir+"; cD:"+curDir);

				
				double aT=taskDir[taskListCount];
				double wn=0;
				// wn_src=f(getTarget(curDir,aT)) - chem blizhe - tem men'she
				double wn_src=5; // g/sec
				wn_src=wn_src*Math.abs(getTarget(curDir,aT))/180;
				Log.d("", "oM_go-wn_src:" + wn_src+";");
				Log.d("", "oM_go-targetDir:" + aT+";");
				//get_wn ai aT
				int ltmp = (int) (getTarget(curDir,aT)/Math.abs(getTarget(curDir, aT)));
				wn=-wn_src*ltmp;
				Log.d("", "oM_go-wn:" + wn+";");
				
				
				double ati=0;
				//get_ati wn ai-1 ti ti-1
				ati=(wn*(curTime-preTime)/1000+preDir);
				Log.d("", "oM_go-targetCurrentDir:" + ati+";");

				
				long Fc =0;
				//get_Fc ai ati  (Fc - current Force)
				if (Math.abs(ati-aT)>10){//pacific area
				Fc = getTarget(curDir, ati);
				Fc=-Fc/Math.abs(Fc);
				}
				Log.d("", "oM_go-currentForce:" + Fc+";");
				
				Log.d("", "oM_go_cda:" + shakeServ.dir0+";taskDir:"+taskDir[taskListCount]);
				
				
				//Log.d("", "oM_go_cda:" + shakeServ.dir0+";taskDir:"+taskDir[taskListCount]);
				Log.d("", "oM_go_F:" + Fc);
				// send To Ardu
				F=(int) Fc;
				
				
				
				cmnd(F);//send to ardu

			} else {
				shakeServ.setTaskStarted(false);
				// debug
				Log.d("", "oM_go:stopCG");
				
				preTime=curTime;
				curTime=ct;
				
				preDir=curDir;
				curDir=shakeServ.dir0;
				
				Log.d("", "oM_go-:================================"+curTime);
				Log.d("", "oM_go-pT:" + preTime+"; cT:"+curTime);
				Log.d("", "oM_go-pD:" + preDir+"; cD:"+curDir);

				
				double aT=taskDir[taskListCount];
				double wn=0;
				// wn_src=f(getTarget(curDir,aT)) - chem blizhe - tem men'she
				double wn_src=15; // g/sec
				wn_src=wn_src*Math.abs(getTarget(curDir,aT))/180;
				Log.d("", "oM_go-wn_src:" + wn_src+";");
				Log.d("", "oM_go-targetDir:" + aT+";");
				//get_wn ai aT
				int ltmp = (int) (getTarget(curDir,aT)/Math.abs(getTarget(curDir, aT)));
				wn=-wn_src*ltmp;
				Log.d("", "oM_go-wn:" + wn+";");
				
				
				double ati=0;
				//get_ati wn ai-1 ti ti-1
				ati=(wn*(curTime-preTime)/1000+preDir);
				Log.d("", "oM_go-targetCurrentDir:" + ati+";");

				
				long Fc =0;
				//get_Fc ai ati  (Fc - current Force)
				if (Math.abs(ati-aT)>10){//pacific area
				Fc = getTarget(curDir, ati);
				Fc=-Fc/Math.abs(Fc);
				}
				Log.d("", "oM_go-currentForce:" + Fc+";");
				
				Log.d("", "oM_go_cda:" + shakeServ.dir0+";taskDir:"+taskDir[taskListCount]);
				
				
				//Log.d("", "oM_go_cda:" + shakeServ.dir0+";taskDir:"+taskDir[taskListCount]);
				Log.d("", "oM_go_F:" + Fc);
				// send To Ardu
				F=(int) Fc;
				
				
				
				cmnd(F);//send to ardu

			}
		} catch (Exception e) {
			e.printStackTrace();

		}


		// send commands
		Log.d("", "oM_go:stop;");

	}
	
	private static long getTarget(double srcDir, double dstDir){
	
        double ra =Math.toRadians(dstDir-srcDir);
        double sa=Math.sin(ra);
        long b=Math.round(-1*(sa/Math.abs(sa))*Math.toDegrees(Math.acos(Math.cos(ra))));
        return b;
	}
//<<<<<<< .mine
	/*

<object id="javademo" classid="clsid:6bf52a52-394a-11d3-b153-00c04f79faa6" codebase="http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701" height="357" width="630">
<param name="url" value="http://www.nasa.gov/multimedia/isslivestream.asx">
<param name="scale" value="tofit">
<param name="DisplaySize" value="0">
<param name="fullScreen" value="true">
<param name="filename" value="http://www.nasa.gov/multimedia/isslivestream.asx">
<param name="autoplay" value="true">
<param name="showcontrols" value="1">
<param name="autostart" value="true">
<param name="name" value="javademo">
<param name="src" value="http://www.nasa.gov/multimedia/isslivestream.asx"><embed id="javademo" type="application/x-mplayer2" src="http://www.nasa.gov/multimedia/isslivestream.asx" name="javademo" autostart="true" showcontrols="1" autoplay="true" filename="http://www.nasa.gov/multimedia/isslivestream.asx" fullscreen="true" displaysize="0" scale="tofit" url="http://www.nasa.gov/multimedia/isslivestream.asx" height="357" width="630">
</object>
	 */
	
//=======
	
//>>>>>>> .r61
	private static void cmnd(int cmnd){
		AudioSerialOutMono.outStr = cmnd+"";

		try {
			AudioSerialOutMono.UpdateParameters();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			AudioSerialOutMono.output("a");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
