// 1.356 seconds

import java.util.*;
import java.io.*;
import java.math.*;
import static java.lang.Math.*;
import static java.lang.System.*;
import static java.lang.Integer.*;
import static java.lang.Double.*;
import static java.util.Arrays.*;

/*! \brief UVa problem 563 solution
 *
 * implementation of an Edge
 */
public class p563 {
	public static void main(String args[]) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		int cas = parseInt(br.readLine());
		while(cas-->0) {
			String[] ss = br.readLine().trim().split("\\s+");
			int n = parseInt(ss[0]);
			int m = parseInt(ss[1]), b = parseInt(ss[2]);
			int off = (n+4)*n+4;
			edges = new LL[2+2*off];
			for(int i=0;i<edges.length;i++) edges[i] = new LL();
			int s = edges.length-2, t = s+1;
			for(int i=0;i<=n+1;i++) {
				for(int j=0;j<=n+1;j++) {
					int a = i*(n+2)+j;
					edges[a+off].add(new Edge(a+off,a,1));
				}
			}
			for(int i=1;i<=n;i++) {
				for(int j=1;j<=n;j++) {
					int map = i*(n+2)+j;
					int map2 = -1;
					//east
					map2 = i*(n+2)+j+1;
					Edge e = new Edge(map,map2+off,1);
					edges[map].add(e);
					edges[map2+off].add(e);
					
					e = new Edge(map2,map+off,1);
					edges[map+off].add(e);
					edges[map2].add(e);

					
					//south
					map2 = (i+1)*(n+2)+j;
					e = new Edge(map,map2+off,1);
					edges[map].add(e);
					edges[map2+off].add(e);
					
					e = new Edge(map2,map+off,1);
					edges[map+off].add(e);
					edges[map2].add(e);
					
					if(i == 1) {
						//north
						map2 = (i-1)*(n+2)+j;
						e = new Edge(map,map2+off,1);
						edges[map].add(e);
						edges[map2+off].add(e);
						
						e = new Edge(map2,map+off,1);
						edges[map+off].add(e);
						edges[map2].add(e);
					}
					
					if(j == 1) {
						//west
						map2 = i*(n+2)+j-1;;
						e = new Edge(map,map2+off,1);
						edges[map].add(e);
						edges[map2+off].add(e);
						
						e = new Edge(map2,map+off,1);
						edges[map+off].add(e);
						edges[map2].add(e);
					}
				}
			}
			for(int i=0;i<b;i++) {
				ss = br.readLine().trim().split("\\s+");
				int x = parseInt(ss[0]), y = parseInt(ss[1]);
				int u = x*(n+2)+y;
				Edge e = new Edge(s,u+off,1);
				edges[s].add(e);
			}
			for(int i=1,u=0,v=0;i<=n;i++) {
				u = i; v = n+2;
				Edge e = new Edge(u*(n+2)+v,t,1);
				edges[u*(n+2)+v].add(e);
				edges[t].add(e);
				
				u = i; v = n+1;
				e = new Edge(u*(n+2)+v,t,1);
				edges[u*(n+2)+v].add(e);
				edges[t].add(e);
				
				u = 0; v = i;
				e = new Edge(u*(n+2)+v,t,1);
				edges[u*(n+2)+v].add(e);
				edges[t].add(e);
				
				u = n+1; v = i;
				e = new Edge(u*(n+2)+v,t,1);
				edges[u*(n+2)+v].add(e);
				edges[t].add(e);
			}
			//out.println("off = "+off);
			//out.println("s = "+s+"   t = "+t);
			//for(int i=0;i<edges.length;i++) out.println(i+" "+edges[i]);
			long sum = 0;
			while(augment(s,t)) {
				int min = Integer.MAX_VALUE;
				for(int v=t;v!=s;v=edgeTo[v].other(v))
					min = min(min,edgeTo[v].res(v));
				for(int v=t;v!=s;v=edgeTo[v].other(v))
					edgeTo[v].add(v,min);
				sum += min;
			}
			//out.println(sum);
			out.println(sum == b ? "possible" : "not possible");
		}
	}
	
	static boolean augment(int s, int t) {
		edgeTo = new Edge[edges.length];
		vis = new boolean[edges.length];
		LinkedList<Integer> q = new LinkedList<Integer>();
		q.add(s);
		vis[s] = true;
		while(!q.isEmpty()) {
			int x = q.poll();
			if(x == t) return true;
			for(Edge e : edges[x]) {
				int y = e.other(x);
				if(!vis[y] && e.res(y) > 0) {
					vis[y] = true;
					edgeTo[y] = e;
					q.add(y);
				}
			}
		}
		return vis[t];
	}
	
	static Edge[] edgeTo;
	static boolean[] vis;
	static LL[] edges;
	
	static class LL extends LinkedList<Edge> {}
	
	
	static class Edge {
		int u,v,cap,flow;
		
		Edge(int a, int b, int c) {
			u = a;
			v = b;
			cap = c;
			flow = 0;
		}
		
		int other(int a) {
			return u == a ? v : u;
		}
		
		int res(int a) {
			return u == a ? flow : cap-flow;
		}
		
		void add(int a, int x) {
			flow -= u == a ? x : -x;
		}
		
		public String toString() {
			return u+"->"+v+"("+flow+"/"+cap+")";
		}
	}
}