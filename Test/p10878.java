import java.util.*;
import java.io.*;
import java.math.*;
import static java.lang.Math.*;
import static java.lang.System.*;
import static java.lang.Integer.*;
import static java.lang.Long.*;

/**
	This solves UVa problem 10878
*/
public class p10878 {
	
	public static void main(String args[]) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		br.readLine();
		StringBuilder sb = new StringBuilder("");
		while(true) {
			String line = br.readLine();
			if(line.charAt(0) != '|') break;
			
			int k = 0; /** \var k is a variable  */
			for(int i=1;i<10;i++) {
				if(i==6) continue;
				k <<= 1;
				k |= line.charAt(i)=='o'?1:0;
			}
			sb.append((char)k);
		}
		out.print(sb);
	}
}