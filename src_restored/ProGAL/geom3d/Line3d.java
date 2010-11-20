package ProGAL.geom3d;

/**
 * A line represented by a point and a direction. Several methods work with a  
 * parameter that can be used to specify a point on the line. For instance 
 * <pre>
 * Line3d l = new Line3d( new Point3d(0,0,0), new Vector3d(2,0,0) );
 * System.out.println( l.getPoint( 0.0 ) );
 * System.out.println( l.getPoint( 1.0 ) );
 * </pre>
 * will print the points (0,0,0) and (2,0,0). Similarly, the lines  
 * <pre>
 * l.orthogonalProjection(new Point3d(1,1,0));
 * l.orthogonalProjectionParameter(new Point3d(1,1,0));
 * </pre>
 * will return the point (1,0,0) and the parameter 0.5 respectively. 
 */
public class Line3d {
	protected Point3d p;
	protected Vector3d dir;
	
	/** Constructs a line through origo with direction d. */
	public Line3d(Vector3d d) {
		p = new Point3d(0, 0, 0);
		dir = d;
	}
	
	/** Constructs a line through p with direction d.*/
	public Line3d(Point3d p, Vector3d d) {
		this.p = p;
		dir = d;
	}
	
	/** 
	 * Constructs a line through the segment s. The point will be s.getA() and the direction 
	 * will be s.getAToB()
	 */
	public Line3d(Segment3d s) {
		p = s.a.clone();
		dir = s.getAToB();
	}
	
	/** Returns the point defining this line. */
	public Point3d getP()   { return p; }

	/** Returns the direction vector defining this line. */
	public Vector3d getDir() { return dir; }

	/** Gets the point on the line defined by the specified parameter. */
	public Point3d getPoint(double t) {
		return new Point3d(p.x + t*dir.x, p.y + t*dir.y, p.z + t*dir.z);
	}
	
	/** Returns the othogonal projection of the point q onto this line. */
	public Point3d orthogonalProjection(Point3d q) {
		Vector3d pq = p.vectorTo(q);
		double t = pq.dot(dir);
		return new Point3d(p.x + t*dir.x, p.y + t*dir.y, p.z + t*dir.z);
	}
	
	/** Returns the line-parameter of the othogonal projection of the point q onto this line. */
	public double orthogonalProjectionParameter(Point3d q) {
		Vector3d pq = p.vectorTo(q);
		return pq.dot(dir)/dir.lengthSquared();
	}
	
	/** Returns the smallest segment that contains all orthogonol 
	 * projections of a point set onto this line. */
	public Segment3d orthogonalProjection(PointList3d points) {
		double minT = Double.POSITIVE_INFINITY, maxT = Double.NEGATIVE_INFINITY;
		for(Point3d q: points){
			double t = orthogonalProjectionParameter(q);
			minT = Math.min(minT, t);
			maxT = Math.max(maxT, t);
		}
		return new Segment3d(getPoint(minT), getPoint(maxT)); 
	}
	
	/** Returns the line-parameters of the end-points of the smallest segment that 
	 * contains all orthogonol projections of a point set onto this line. 
	 * @return a double-array with two entries containing the smallest and largest 
	 * orthogonal projection line-parameter of the point-set.*/
	public double[] orthogonalProjectionParameters(PointList3d points) {
		double minT = Double.POSITIVE_INFINITY, maxT = Double.NEGATIVE_INFINITY;
		for(Point3d q: points){
			double t = orthogonalProjectionParameter(q);
			minT = Math.min(minT, t);
			maxT = Math.max(maxT, t);
		}
		return new double[]{minT, maxT}; 
	}

	/** Gets the squared orthogonal distance to a point. */
	public double getDistanceSquared(Point3d q) { 
		return dir.cross(q.vectorTo(p)).lengthSquared()/dir.lengthSquared();
	}
	
	/** Gets the orthogonal distance to a point. */
	public double getDistance(Point3d q) { 
		return Math.sqrt(getDistanceSquared(q));
	}

	/** Gets the largest squared distance from the points to the line. */
	public double getMaxDistanceSquared(PointList3d points) {
		if (points.size() == 0) throw new Error("No point");
		double maxDist = Double.NEGATIVE_INFINITY;
		for (Point3d q: points) {
			double dist = getDistanceSquared(q);
			if (dist > maxDist) maxDist = dist;
		}
		return maxDist;
	}

	/** Gets the largest distance from the points to the line. */
	public double getMaxDistance(PointList3d points) {
		return Math.sqrt(getMaxDistanceSquared(points));
	}
	
	/** Gets the minimum squared distance to another line. 
	 * @see [Ericsson 05, p. 147]*/
	public double getSquaredDistance(Line3d l) {
		double a = dir.lengthSquared();
		double b = dir.dot(l.dir);
		double e = l.dir.lengthSquared();
		double d = a*e-b*b;
		
		//TODO: An epsilon should perhaps be introduced
		if(d==0) return getDistanceSquared(l.p);
		
		Vector3d r = p.vectorTo(l.p);
		double c = dir.dot(r);
		double f = l.dir.dot(r);
		
		double s = (b*f-c*e)/d;
		double t = (a*f-b*c)/d;
		
		//TODO: Two point-allocations can be optimized away here, but the code would get ugly
		return getPoint(s).getDistanceSquared(l.getPoint(t));
	}

	/** Returns a string-representation of this line.*/
	public String toString(){
		return String.format("Line3d[p:%s,dir:%s]", p, dir);
	}
	
	/** Returns a string-representation of this line with <code>dec</code> decimals precision.*/
	public String toString(int dec){
		return String.format("Line3d[p:%s,dir:%s]", p.toString(dec), dir.toString(dec));
	}
}
