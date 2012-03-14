package com.android.enai;

import java.util.Arrays;

import android.util.Log;

public class VisitDetails {
	public String fullData;
	public int registryNumber;
	public String patientName;
	public int age;
	public int scoreGravida;
	public int scorePara;
	public int scoreFpal;
	public String regDate;
	public String regBy;
	public String lmp;
	public String edc;
	public String aog;
	public String bloodType;
	public String height;
	public String bpSys;
	public String bpDias;
	public String TTStatus;
	public String riskFactors;
	
	public VisitDetails(String data) {
		fullData = data;
		String[] ss = data.split("\n");
		registryNumber = Integer.parseInt(ss[0].split(": ")[1]);
		patientName = ss[1].split(": ")[1];
		age = (int)Double.parseDouble(ss[2].split(": ")[1]);
		{
			String[] s = ss[3].split(": ")[1].split(" ");
			scoreGravida = Integer.parseInt(s[0].split("/")[0]);
			scorePara = Integer.parseInt(s[0].split("/")[1]);
			scoreFpal = Integer.parseInt(s[1]);
		}
//		regDate = ss[4].split(": ")[1];
//		regBy = ss[5].split(": ")[1];
//		lmp = ss[6].split(": ")[1];
//		lmp = ss[7].split(": ")[1];
//		edc = ss[8].split(": ")[1];
//		aog = ss[9].split(": ")[1];
//		bloodType = ss[10].split(": ")[1];
//		height = ss[11].split(": ")[1];
//		bpSys = ss[12].split(": ")[1];
//		bpDias = ss[13].split(": ")[1];
//		TTStatus = ss[14].split(": ")[1];
//		riskFactors = ss[15].split(": ")[1];
		
	}
}
