

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


import tools.FFT;
import tools.MathUtils;
import tools.FIR;



public class HeartRateExtraction {
	

	public static void main(String args[]) {
		double windowSize = 1.85759;	//1.85759-second window
		int calculations = 6;
		double stepSize = (windowSize / calculations);
		
		try {
			// Open the file that is the first
			// command line parameter
			File soundFile = new File("Heartbeat8weeks.wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat format = ais.getFormat(); // get format
			int Fs = (int)format.getFrameRate();  // get Sampling frequency
			int ds =10;								// downsample e.g. 22050 to 2205
			Fs=Fs/ds;
			int samples = 2*4096;	//set number of samples, samples = windowSize*Fs
			int minT0 = (int) (0.3*Fs);							//minimum = 0.3sec (200bpm)
			int maxT0 = (int) (0.6*Fs);							//maximum = 0.6sec (100bpm)
			
			int stepSizeSample = 2*(int) (stepSize * Fs);
			long nBytes = (long) (ais.getFrameLength() * format.getFrameSize());	//get number of bytes 
			
			byte[] inBuffer = new byte[(int)nBytes];
			ais.read(inBuffer, 0, (int) nBytes);		//read
			FIR filt = new FIR();		//create filter
			
			int j=0;
			double[] value = new double[(int) (nBytes)/format.getFrameSize()];
			double[] value1 = new double [2*(value.length/ds)];
			long start = System.currentTimeMillis();
			for (int i = 0; i < inBuffer.length; i = i + 2) {
				value[j] = 0.000030525*(double) (((byte) inBuffer[i] & 0xff) | ((int) inBuffer[i + 1] << 8));
				//filter
				value[j] = filt.process(value[j]);
				j++;
			}
			
			
			//Downsample the reading and put it in real and imaginary array.
			//Real in even elements and Imaginary in odd elements
			for (int i=0; i<value1.length/2; i++){
				if(i!=0){
					value1[2*i]=value[(i*10)-1];	//real part		
					value1[2*i+1]=0;				//imaginary
				}
				else {
					value1[i]= value[i];
					value1[2*i+1]=0;
				}
			}
			long end = System.currentTimeMillis();
//			System.out.println((end-start));
			
				
			//Shift over time
			for (int s = 0; s<value.length; s+=stepSizeSample){	
				if ((int)(value1.length-s) >= samples) {	//if there is enough number of samples to compute
					
					
					double[] value2 = new double[samples];
					//Copy window-size samples to value2
					System.arraycopy(value1, s, value2, 0, samples);
					
					//Get envelope
					double[] envelope = new double[value2.length/2];
					envelope=FFT.envelope(value2);
					
					//Autocorrelation
					double[] R = new double[envelope.length];
					R = FFT.autoCorrelate(envelope);
					
					//Get the time of occurrence of peak
					int t = MathUtils.findLocalPeakLocation(R, minT0, maxT0);
					
					//Solve FHR using the time of occurrence of peak
					double FHR =  ((double)(60 * Fs) / t);
					//Print
					System.out.format("%3.3f\n", FHR);
				
				}
				else{
//					System.out.println("end");
					break;
				}
				
			}
			
			// Close the input stream
			ais.close();
			
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
     * Added by Nathaniel Cruz
     * Find the maximum between StartIndex and EndIndex in the array, ignoring elements that are NaN.
     * @param data
     * @return the index number of the maximum element
     */
    public static int findLocalPeakLocation(double[] data, int StartIndex, int EndIndex)
    {
        double max = Double.NaN;
        int imax = -1;
        for (int i=StartIndex; i<=EndIndex; i++) {
            if (Double.isNaN(data[i])) continue;
            if (Double.isNaN(max)|| data[i] > max) {
                max = data[i];
                imax = i;
            }
        }
        return imax;
    }
}
