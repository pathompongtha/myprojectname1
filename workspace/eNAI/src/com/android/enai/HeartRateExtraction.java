package com.android.enai;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


import fhrtools.FFT;
import fhrtools.MathUtils;
import fhrtools.FIR;



public class HeartRateExtraction {
	

	public static void main(String args[]) {
		double windowSize = 1.3932; //1.8576;	//1.85759-second window
		int calculations = 4;//6;
		double stepSize = (windowSize / calculations);
		
		try {
			// Open the file that is the first
			// command line parameter
			File soundFile = new File("180bpmstd0.8.wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat format = ais.getFormat(); // get format
			int Fs = (int)format.getFrameRate();  // get Sampling frequency
			int N=30;
			int samples = 30720;	//set number of samples, samples = windowSize*Fs
			double ds = (double) (Fs/N);
			int minT0 = (int) (0.3*ds);							//minimum = 0.3sec (200bpm)
			int maxT0 = (int) (0.6*ds);//(windowSize*Fs)-1;							//maximum = 0.6sec (100bpm)
		
			int stepSizeSample = (int) (stepSize * Fs);		
			long nBytes = (long) (ais.getFrameLength() * format.getFrameSize());	//get number of bytes 
			
			byte[] inBuffer = new byte[(int)nBytes];
			ais.read(inBuffer, 0, (int) nBytes);		//read
			FIR filt = new FIR();		//create filter
			
			int j=0;
			double[] value = new double[(int) (nBytes)/format.getFrameSize()];
			
			for (int i = 0; i < inBuffer.length; i = i + 2) {
				value[j] = 0.000030525*(double) (((byte) inBuffer[i] & 0xff) | ((int) inBuffer[i + 1] << 8));
				//filter
				value[j] = filt.process(value[j]);
				j++;
			}
			
						
				
			//Shift over time
			long start = System.currentTimeMillis();
			for (int s = 0; s<value.length; s+=stepSizeSample){	
				if ((int)(value.length-s) >= samples) {	//if there is enough number of samples to compute
					
					
					double[] value1 = new double[samples];
					//Copy window-size samples to value2
					System.arraycopy(value, s, value1, 0, samples);
					
					//Get peak-decimation
					double[] beatpeak = new double[value1.length/N];
					int base = 0;
					int k = 0;
					for (int a = 0; a<samples;a+=N){
						double peak = 0;
						for(int b = 0; b<N; b++){
							if (base+N<samples){
								if(Math.abs(value1[base+b])>peak){
									beatpeak[k] = Math.abs(value1[base+b]);
									peak = beatpeak[k];
								}
							}
						}
					base = a + N;
					k++;
					}
					
					
					//Autocorrelation
					double[] R = new double[beatpeak.length];
					R = FFT.autoCorrelate(beatpeak);
					
					//Get the time of occurrence of peak
					int t = MathUtils.findLocalPeakLocation(R, minT0, maxT0);
					
					//System.out.println(time[t]);
					//Solve FHR using the time of occurrence of peak
					double FHR =  (double) 60*ds/t;// time[t];
					//Print
					System.out.format("%3.3f\n", FHR);
				
				}

				else{
					System.out.println("end");
					break;
				}
				
			}
			long end = System.currentTimeMillis();
			System.out.println((end-start));
			// Close the input stream
			ais.close();
			
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

}
