import java.util.Set;

import javax.imageio.plugins.tiff.GeoTIFFTagSet;

import java.util.ArrayList;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;

final public class AirlineSystem implements AirlineInterface {

 private unDigraph graph=null;
  public String[] cityNames;
  AirlineInterface airline;
  public boolean loadRoutes(String fileName) {
    try
    {
      Scanner fileScan = new Scanner(new FileInputStream(fileName));
      //intialze graph
      int v = Integer.parseInt(fileScan.nextLine());
      graph = new unDigraph(v);
      airline=new AirlineSystem();
      cityNames = new String[v];
      for(int i=0; i<v; i++)
      {
        cityNames[i] = fileScan.nextLine();
      }
      
      int from = fileScan.nextInt();
        int to = fileScan.nextInt();
        int distance=fileScan.nextInt();
        double cost= fileScan.nextDouble();
        
        Route r = new Route(cityNames[from-1],cityNames[to-1],distance,cost);
        graph.addRoute(r,cityNames); 
        //the second path backwards
         r = new Route(cityNames[to-1],cityNames[from-1],distance,cost);
       graph.addRoute(r,cityNames); 
       ++graph.r;
     
      while(fileScan.hasNextLine())
      {
        fileScan.nextLine(); 
        from = fileScan.nextInt();
         to = fileScan.nextInt();
         distance=fileScan.nextInt();
         cost= fileScan.nextDouble();
         Route d = new Route(cityNames[from-1],cityNames[to-1],distance,cost);
         graph.addRoute(d,cityNames); 
         //the second path backwards
         d = new Route(cityNames[to-1],cityNames[from-1],distance,cost);
         graph.addRoute(d,cityNames); 
         ++graph.r;
        }
      fileScan.close();
    }
    catch(IOException i)
    {
      return false;
    }
    return true;
  }

  public Set<String> retrieveCityNames() {
    Set<String> s=new HashSet<>(Arrays.asList(cityNames));
    return s;
  }

  public Set<Route> retrieveDirectRoutesFrom(String city)
    throws CityNotFoundException {
      Set<Route> s=new HashSet<>();
      int i;
      for(i=0;i<cityNames.length;i++)
      {
        if(cityNames[i].equals(city))
        {
          break;
        }
      }
    
     for(Route r: graph.adj(i))
    {
      s.add(r);
    }
      return s;
  }

  public Set<ArrayList<String>> fewestStopsItinerary(String source,
  
  String destination) throws CityNotFoundException 
    {
      Set<ArrayList<String>> s =new HashSet<ArrayList<String>>();  
      int i;
      for(i=0;i<cityNames.length;i++)
      {
        if(cityNames[i].equals(source))
        {
          break;
        }
      }
      int j; 
      for(j=0;j<cityNames.length;j++)
      {
        if(cityNames[j].equals(destination))
        {
          break;
        }
      }
      graph.bfs(i,cityNames);
      if(!graph.marked[j]){
        
        return s;
      } else {
        Stack<Integer> path = new Stack<>();
        
        for (int x = j; x != i; x = graph.edgeTo[x]){
          
          path.push(x);
      }
      int prevVertex = i;
      ArrayList<String> a = new ArrayList<>();
      a.add(cityNames[i]); 
      while(!path.empty()){ 
          int v = path.pop();
          a.add(cityNames[v]); 
          prevVertex = v;
  }
  s.add(a);    
}
    
    return s;
  }
  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String destination) throws CityNotFoundException {
      Set<ArrayList<Route>> s =new HashSet<ArrayList<Route>>(); 
       
      int i;
      for(i=0;i<cityNames.length;i++)
      {
        if(cityNames[i].equals(source))
        {
          break;
        }
      }
      int j; 
      for(j=0;j<cityNames.length;j++)
      {
        if(cityNames[j].equals(destination))
        {
          break;
        }
      }
      graph.dijkstras(i, j, cityNames);
      if(!graph.marked[j]){
        return s;
      } else {
        Stack<Integer> path = new Stack<>();
        ArrayList<Route> a = new ArrayList<>();
        for (int x = j; x != i; x = graph.edgeTo[x]){
            path.push(x);
          }
          int prevVertex = i;
          while(!path.empty())
          {
            int v = path.pop();
            for(Route r:graph.adj(prevVertex))
            {
              if(r.source.equals(cityNames[prevVertex]) && r.destination.equals(cityNames[v]))
              {
                a.add(r);
                prevVertex=v;
              }
            } 
          }
          s.add(a);
      return s;
  }
}
  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String transit, String destination) throws CityNotFoundException 
    {
      Set<ArrayList<Route>> s =new HashSet<ArrayList<Route>>(); 
      s =shortestDistanceItinerary(source, transit);
     
      ArrayList<Route> a = new ArrayList<>();
      Iterator< ArrayList<Route>> it =s.iterator();
      while(it.hasNext())
      {
        for(Route r:it.next())
        a.add(r);
      }
      s =shortestDistanceItinerary(transit, destination);
      it =s.iterator();
      while(it.hasNext())
      {
        for(Route r:it.next())
        a.add(r);
      }
      s=new HashSet<ArrayList<Route>>(); 
      s.add(a);   
   return s;
    }

  

  public boolean addCity(String city){
//if already in array
    for(int i=0;i<cityNames.length;i++)
    {
      if(cityNames[i].equals(city))
      {
        return false;
      }
    }
    String[] temp= new String[cityNames.length+1];
    for(int i=0;i<cityNames.length;i++)
    {
      temp[i]=cityNames[i];
    }
    temp[temp.length-1]=city;
    cityNames=new String[temp.length];
    cityNames=temp;
    unDigraph temper=new unDigraph(cityNames.length); 
  
    for(int i=0;i<graph.adj.length;i++)
    {
      for(Route e: graph.adj(i))
      {
        temper.addRoute(e, cityNames);
      }
    }
    graph=temper;  
    graph.adj=temper.adj;  
   
    return true;
  }

  public boolean addRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException 
  {
    
    for(int i=0;i<cityNames.length;i++)
    {
      for(Route l : graph.adj(i))
      {
        if(l.source.equals(source) && l.destination.equals(destination))
        {
          if(l.distance==distance && l.price==price)
          {
            return false;
          }
          else{
            l.distance=distance;
            l.price=price;
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean updateRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
      int i;
    for(i=0;i<cityNames.length;i++)
    {
      if(cityNames[i].equals(source))
      {
        break;
      }
    }
      for(Route l : graph.adj(i))
      {
       // l.equals(d);
        return false;
      }
      return false;
  }
}


 /**
  *  The <tt>Digraph</tt> class represents an directed graph of vertices
  *  named 0 through v-1. It supports the following operations: add an edge to
  *  the graph, iterate over all of edges leaving a vertex.Self-loops are
  *  permitted.
  */
  class unDigraph {
    private final int v;
     int r;
     LinkedList<Route>[] adj;
     boolean[] marked;  // marked[v] = is there an s-v path
     int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
     int[] distTo;      // distTo[v] = number of edges shortest s-v path
    private static final int INFINITY = Integer.MAX_VALUE;

   
    public unDigraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.r = 0;
      @SuppressWarnings("unchecked")
      LinkedList<Route>[] temp =
      (LinkedList<Route>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<Route>();
    }
    public Iterable<Route> adj(int v) {
      return adj[v];
    }
    public void addRoute(Route route,String[] citys) {
      String from = route.source;
      for(int i=0;i<citys.length;i++ )
      {
        if(citys[i].equals(from))
        {
          adj[i].add(route);
          this.r++;
        }
      }
    } 
    public void bfs(int source,String[] citys) {
      marked = new boolean[this.v];
      distTo = new int[this.r];
      edgeTo = new int[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);
      
      while (!q.isEmpty()) 
      {
        int v = q.remove();
        
        for (Route w : adj(v)) 
        {
          
          int i;
          for(i=0;i<citys.length;i++ )
          {
            if(citys[i].equals(w.destination))
            {
              break; 
            }
          }
          if (!marked[i]) 
          {
            edgeTo[i] = v;
            distTo[i] = distTo[v] + 1;
            marked[i] = true;
            q.add(i);
          }
        }
      }
    }
    public void dijkstras(int source, int destination,String[] citys) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];


      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;

      int current = source;
      while (nMarked < this.v) {
        for (Route w : adj(current)) {
          int i;
          for(i=0;i<citys.length;i++ )
          {
            if(citys[i].equals(w.destination))
            {
              break; 
            }
          }
          
          
          if (distTo[current]+w.distance < distTo[i]) {
	      //TODO:update edgeTo and distTo
            edgeTo[i]=current;
            distTo[i]=distTo[current]+w.distance;
	      
          }
        }
        //Find the vertex with minimim path distance
        //This can be done more effiently using a priority queue!
        int min = INFINITY;
        current = -1;

        for(int i=0; i<distTo.length; i++){
          if(marked[i])
            continue;
          if(distTo[i] < min){
            min = distTo[i];
            current = i;
          }
        }

	//TODO: Update marked[] and nMarked. Check for disconnected graph.
      if(current>=0)
      {     
        marked[current]=true;
              ++nMarked;
      }
      else{
        break;
      }
      }
    }
  }
  
  
  

   
