/**
 * Title:        SacredHand
 * Description:  java3d implementation of the Meru hand recipricol spiral
 * $Header: /home/users/jonah/cvs/handy/FirstHand.java,v 1.2 2001/07/29 07:56:30 jonah Exp $
 * @Version $Version: $
 */
import java.util.Vector;

import javax.media.j3d.*;
import javax.vecmath.*;


public class FirstHand extends Shape3D {

  /** the orientation of the spiral */
  public static final boolean UP   = true;
  public static final boolean DOWN = false;

  /** the resolution of the spriral sampling */
  private static final double EPSILON = 0.1;
  private static final double EPSILON_FINE = 0.001;

  /**
   * detfault constructor w/ some reasonable constaants
   */
  public FirstHand() {
    // just make up some values for now
    this(0.5, 2, 0.3, 0, FirstHand.UP);
  }

  /**
   * detailed constructor
   * @param inner the inner radius of the torus
   * @param outer the outer radius of the torus
   * @param span the width of the spiral - in radians
   * @param alpha the offset of the spiral - in radians (eg to stagger them on a plane)
   * @param up the orientation of the spiral - UP or DOWN
   */
   public FirstHand(double inner, double outer, double span, double alpha, boolean up) {
      this.setGeometry(createHand(inner, outer, span, alpha, up));
   }

  /**
   * creates a reciprical spiral hand
   */
  private Geometry createHand(double inner, double outer, double span, double alpha, boolean up) {
    double SHIFT = 0.0;  // this is unused for now
    double r, theta, x, y, z, epsilon;
    double orientation = (up) ? 1.0 : -1.0;

    boolean first = true;
    Point3d v1 = null, v2 = null, v3 = null, v4 = null;

    Vector handPoints = new Vector(); // initial cap.
    epsilon = EPSILON;

    /* Reciprocal spiral:  r = 1/theta.
     * Standard Torus Equation: f(u,v) = [ (a + b*cos(v))*cos(u),
     *                                     (a + b*cos(v))*sin(u),
     *                                     c*sin(v) ]
     */
    for (theta = 12*Math.PI; theta > 0; theta -= epsilon) {
      if (theta < 1) {  // increase resolution as we approach the origin
	  epsilon = EPSILON_FINE;
      }
      r = 1/(theta);
      x = r * Math.cos(theta + alpha);
      z = r * Math.sin(theta + alpha);


//      y = Math.abs((inner * inner) - ( (r - (inner)) * (r - (inner)) ));
      y = Math.abs((inner * inner) - ( (r - (inner + outer)) * (r - (inner + outer)) ));
      // y = Math.abs((inner * inner) - (r * r) + ( 2 * (inner + outer) * r ) - ( (inner + outer) * (inner + outer)));
      // y = Math.abs(( -1 * (r * r)) + ( 2 * (inner + outer) * r ) - ( outer * outer) - (2 * inner * outer));
      // System.out.println(y);
      /*
      // y = (-(r*r) + 2*inner*r);
      y = ((inner*inner) - ( (r-inner-SHIFT)*(r-inner-SHIFT)) );
      y = orientation * outer * Math.sqrt(y);
      */
      if (y > EPSILON_FINE) {
	  y = orientation * Math.sqrt(y);
	  // y = orientation * outer * Math.sqrt(y);
      }
      else if (r < SHIFT) {
	  y = 0;
      }
      else {
        // Abandon ship when y becomes imaginary
        handPoints.add(new Point3d(v1.x, v1.y, v1.z));
        handPoints.add(new Point3d(v2.x, v2.y, v2.z));
        handPoints.add(new Point3d(v1.x, 0, v1.z));

        handPoints.add(new Point3d(v1.x, 0, v1.z));
        handPoints.add(new Point3d(v2.x, v2.y, v2.z));
        handPoints.add(new Point3d(v1.x, v1.y, v1.z));

        handPoints.add(new Point3d(v2.x, v2.y, v2.z));
        handPoints.add(new Point3d(v1.x, 0, v1.z));
        handPoints.add(new Point3d(v2.x, 0, v2.z));

        handPoints.add(new Point3d(v2.x, 0, v2.z));
        handPoints.add(new Point3d(v1.x, 0, v1.z));
        handPoints.add(new Point3d(v2.x, v2.y, v2.z));

        break;
      }

      // First time through start at origin
      if (first) {
        v3 = new Point3d(x, 0, z);
        v4 = new Point3d(x, 0, z);
        first = false;
      }
      else {
        v3 = new Point3d(v1.x, v1.y, v1.z);
        v4 = new Point3d(v2.x, v2.y, v2.z);
      }

      v1 = new Point3d(x, y, z);

      x = r * Math.cos(theta + alpha + span);
      z = r * Math.sin(theta + alpha + span);

      v2 = new Point3d(x, y, z);

      // now add 4 triangular faces
      // one rectangle facing up, one facing down
      //
      handPoints.add(v1);
      handPoints.add(v3);
      handPoints.add(v2);

      handPoints.add(v2);
      handPoints.add(v3);
      handPoints.add(v1);

      handPoints.add(v2);
      handPoints.add(v3);
      handPoints.add(v4);

      handPoints.add(v4);
      handPoints.add(v3);
      handPoints.add(v2);
    }
    TriangleArray triHand = new TriangleArray(handPoints.size(),
                                              GeometryArray.COORDINATES |
                                              GeometryArray.BY_REFERENCE);
    //  set coordinates by reference
    //  call toArray w/ a dummy array to get the vector to return the correct type
    triHand.setCoordRef3d((Point3d[]) handPoints.toArray(new Point3d[1]));
    return triHand;
  }
}

/*

. Only seeing one side of a flat object


The reason you're seeing this is that the faces are only visible in the direction that the normals are pointing. There are couple of different ways to do what you want. One way is to take the faces you currently have, duplicate them, wind the polygons in the opposite direction, and set the normals in the opposite direction from the original. Another (easier) way is to take an appearance ("app" in the example below) and do the following:

    PolygonAttributes pa = new PolygonAttributes();
    pa.setCullFace(PolygonAttributes.CULL_NONE);
    app.setPolygonAttributes(pa);
*/


