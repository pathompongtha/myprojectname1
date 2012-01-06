package com.android.enai;

import static java.lang.Float.parseFloat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

import org.microbridge.server.Server;

import fhrtools.FFT;
import fhrtools.FIR;
import fhrtools.MathUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FHRAutoPlotView extends View {
	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	private static final int RECORDER_SAMPLERATE = 22050;//44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;//AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private AudioRecord recorder = null;
	private int bufferSize = 40960;
	private Thread recordingThread = null;
	private boolean isRecording = false;

	private double windowSize = 1.85759;	//1.85759-second window
	private int calculations = 6;
	private double stepSize = (windowSize / calculations);
	private int Fs = 22050; // get Sampling frequency, hardcoded above
	private int ds = 10;								// downsample e.g. 22050 to 2205
	private int minT0 = (int) (0.3*Fs/ds);							//minimum = 0.3sec (200bpm)
	private int maxT0 = (int) (0.9*Fs/ds);							//maximum = 0.6sec (100bpm)
	private int samples, stepSizeSample;
	private FIR filt = new FIR();
	
	private Handler mHandler;
	private LinkedList<Point> points;
	private Paint paint;
	private int height, width, off;
	private int ctr = 0;
	private byte[] toBeProcessed = new byte[bufferSize];
	private Thread fftThread = null;
	private byte data[] = new byte[bufferSize];
	
	// Create TCP server (based on MicroBridge LightWeight Server).
	// Note: This Server runs in a separate thread.
	Server server = null;

	public FHRAutoPlotView(Context context, int h, int w, int resource, Thread thread) {
		super(context);
		points = new LinkedList<Point>();
		off = 20;
		height = h;
		width = w - off;

		setFocusable(true);
		setFocusableInTouchMode(true);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		

		Fs=Fs/ds;
		samples = 2*4096;	//set number of samples, samples = windowSize*Fs
		stepSizeSample = 2*(int) (stepSize * Fs);
//		toBeProcessed = new byte[samples];
		
		mHandler = new Handler();
        
//		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
//				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		startRecording();
		fftThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
				try {
					Thread.sleep(4000);
					final long time = System.currentTimeMillis();
					process(toBeProcessed, data);
					final double[] ans = process(toBeProcessed);
					 final double f = fft(ans);
//					final double f = 1000;
					mHandler.post(new Runnable() {
						public void run() {
							getData(f + "");
							// getData(data[0]+"");
//							Toast.makeText(getContext(),System.currentTimeMillis()-time+"",Toast.LENGTH_SHORT).show();
						}
					});
				} catch (InterruptedException ie) {
				}
				}
			}

		});
		fftThread.start();
	}

	public void getData(String s) {
		points.add(conv((parseFloat(s)-0) / 200.0));
		invalidate();
		if (ctr == width)
			while (width - points.size() < 10)
				points.removeFirst();
		else if (ctr > width)
			points.removeFirst();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(points.isEmpty()) return;
		Point prev = points.getFirst();
		for (final Point point : points) {
			if (prev.x < point.x)
				canvas.drawLine(prev.x, prev.y, point.x, point.y, paint);
			else
				canvas.drawPoint(point.x, point.y, paint);
			prev = point;
		}
	}

	public Point conv(double c) {
		return new Point((ctr++) % width + off / 2, (float)(height / 2 - 30 * c));
	}

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}
		
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		
		String fileName = (month+1)+"."+date+"."+year+"-"+hour+"."+min+"."+sec;
		Intent data = new Intent();
		
		String fullPath = (file.getAbsolutePath() + "/" + fileName + AUDIO_RECORDER_FILE_EXT_WAV);
	    data.putExtra("toAttach",fullPath);
	    data.putExtra("fileName",fileName);
//	    setResult(Activity.RESULT_OK,data);
	    
//	    BufferedWriter br = null;
//		try {
//			br = new BufferedWriter(new FileWriter(new File(file.getAbsolutePath()+"/"+fileName+".txt")));
//			br.write(sb.toString());
//			Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT ).show();
//		} catch(IOException e) {}
		
//	    finish();
		
		return fullPath;
	}

	private String getTempFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

		if (tempFile.exists())
			tempFile.delete();

		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

	private void startRecording() {
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, bufferSize);

		recorder.startRecording();

		isRecording = true;

		recordingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");

		recordingThread.start();
	}
	
	private void writeAudioDataToFile() {
		String filename = getTempFilename();
		FileOutputStream os = null;
//		final byte[] data = new byte[bufferSize];

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int read = 0;

		if (null != os) {
			while (isRecording) {
				read = recorder.read(data, 0, bufferSize);
				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
//						os.write(data);
						mHandler.post(new Runnable() {
							public void run() {
//								getData(f+"");
								for(int i=0;i<data.length;i+=1000)
									getData(data[i]+"");
//								getData(data[0]+"");
							}
						});
//						sb.append(Arrays.toString(data));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void stopRecording() {
		if (null != recorder) {
			isRecording = false;

			recorder.stop();
			recorder.release();

			recorder = null;
			recordingThread = null;
		}

//		copyWaveFile(getTempFilename(), getFilename());
//		deleteTempFile();
	}
	
	private void saveRecording() {
		copyWaveFile(getTempFilename(), getFilename());
		deleteTempFile();
	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());

		file.delete();
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 1;//2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			Log.i("AudioRecorder","File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}
	
	public double[] process(byte[] data) {
		double[] filtered = new double[data.length/2];
		for (int i = 0; i < data.length; i = i + 2) {
			filtered[i/2] = 0.000030525*(((int)data[i] & 0xff) | ((int) data[i + 1] << 8));
			filtered[i/2] = filt.process(filtered[i/2]); // filter
		}
		double[] postDownSample = new double[2*filtered.length/ds];
		//Downsample the reading and put it in real and imaginary array.
		//Real in even elements and Imaginary in odd elements
		for (int i=0; i<postDownSample.length/2; i++){
			if(i!=0){
				postDownSample[2*i] = filtered[(i*10)-1];	//real part		
				postDownSample[2*i+1]=0;				//imaginary
			}
			else {
				postDownSample[i]= filtered[i];
				postDownSample[2*i+1]=0;
			}
		}
		return postDownSample;
	}
	
	public void process(byte[] oldData, byte[] newData) {
		for(int i=0;i<oldData.length-newData.length;i++) {
			oldData[i] = oldData[i+newData.length];
		}
		for(int i=oldData.length-newData.length,j=0;i<oldData.length;i++,j++) {
			oldData[i] = newData[j];
		}
//		return process(oldData);
	}
	
	public double fft(double[] data) {
		
		//Get envelope
		double[] envelope = new double[data.length/2];
		envelope = FFT.envelope(data);
		//Autocorrelation
		double[] R = new double[envelope.length];
		R = FFT.autoCorrelate(envelope);
		
		//Get the time of occurrence of peak
		int t = MathUtils.findLocalPeakLocation(R, minT0, maxT0);
		
		//Solve FHR using the time of occurrence of peak
		double FHR = (double) (60 * Fs) / t;
		//Print
//		System.out.printf("%3.3f\n", FHR);
		return FHR;
	}
}
