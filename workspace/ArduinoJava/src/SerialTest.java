import static java.lang.System.out;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class SerialTest extends JPanel implements SerialPortEventListener {
	SerialPort serialPort;
	private static InputStream input;
	private static OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;

	static String[] teamNames = { "Gamma Berries", "Cinephiles", "Humabol" };
//	static String[] toPrint = { "5", "10", "180" };
	 static String[] toPrint = Arrays.copyOf(teamNames,teamNames.length);
	static int[] size;
	static Color[] colors = { Color.RED, Color.GREEN, Color.BLUE };
	static Bulbs bb;
	static int timeLimit = 5;
	static int varTimeLimit[] = { 5, 10, 180 };
	static String timeNow = ":D";

	static boolean bawalNa[] = new boolean[3];
	static AudioStream as[] = new AudioStream[3];

	public static void main(String[] args) throws Exception {

		SerialTest main = new SerialTest();
		main.initialize();
		initSound();
		System.out.println("QuizVi");

		JFrame frame = new JFrame("QuizVI");
		frame.setVisible(true);
		frame.setSize(500, 500);
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		bb = new Bulbs();
		bb.setLayout(null);

		frame.add(bb);
	}

	static void initSound() throws Exception {
		InputStream in = new FileInputStream(
				"C:/Users/emcy/workspace/ArduinoJava/bin/2sneeze1.wav");
		as[0] = new AudioStream(in);
		in = new FileInputStream(
				"C:/Users/emcy/workspace/ArduinoJava/bin/killChicken.wav");
		as[1] = new AudioStream(in);
		in = new FileInputStream(
				"C:/Users/emcy/workspace/ArduinoJava/bin/buzzer3.wav");
		as[2] = new AudioStream(in);
	}

	static class Bulbs extends JPanel {

		private Color[] colors;

		public Bulbs() {
			super();
			setFocusable(true);
			colors = new Color[3];
			colors[0] = colors[1] = colors[2] = Color.BLACK;
			size = new int[3];
			size[0] = size[1] = size[2] = 10;
		}

		public void setColor(int n, Color color) {
			colors[n] = color;
			repaint();
		}

		public void setPlayerTime(int n, String count) {
			toPrint[n] = "" + count;
			repaint();
		}

		public void setCount(String count) {
			timeNow = count; 
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			int w = getWidth(), h = getHeight();
			int r = h / 4;
			for (int n = 0; n < 3; n++) {
				g.setColor(colors[n]);
				g.fillOval((n + 1) * w / 4 - r / 2, h / 2 - r / 2, r, r);
				g.setColor(Color.white);
				g.setFont(new Font("Cooper Black", Font.BOLD, toPrint[n]
						.equals(teamNames[n]) ? h / 40 : h / 10));
				g.drawString(toPrint[n], (n + 1) * w / 4 - toPrint[n].length()
						* (toPrint[n].equals(teamNames[n]) ? r / 35 : r / 8), h
						/ 2
						+ (toPrint[n].equals(teamNames[n]) ? r / 30 : r / 8));
			}
		}
	}

	public void initialize() {
		CommPortIdentifier portId = (CommPortIdentifier) CommPortIdentifier
				.getPortIdentifiers().nextElement();

		try {
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
				byte chunk[] = new byte[available];
				input.read(chunk, 0, available);

				// Displayed results are codepage dependent
				String temp = new String(chunk);
				int x = Integer.parseInt(temp);
				doSomething(x);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	public void doSomething(int x) throws Exception {
		if (x >= 1 && x <= teamNames.length && !bawalNa[x - 1]) {
			AudioPlayer.player.start(as[x - 1]);
			bawalNa[x - 1] = true;
			out.println(teamNames[x - 1]);
			bb.setColor(x - 1, colors[x - 1]);

			 for (int i = timeLimit; i >= 0; i--) {
//			for (int i = varTimeLimit[x - 1]; i >= 0; i--) {
				try {
					out.println(i);
					toPrint[x - 1] = "" + i;
					bb.setPlayerTime(x - 1, "" + i);
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}

			bb.setColor(x - 1, Color.black);
//			bb.setPlayerTime(x - 1, toPrint[x - 1]);
			 bb.setPlayerTime(x - 1,teamNames[x - 1]);
			as[x - 1].close();
		} else if (x == 4) {
			bawalNa = new boolean[3];
			initSound();
		}
		output.write(1);
	}
}
