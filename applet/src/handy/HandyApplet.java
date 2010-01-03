/**
 * Title:       HandyApplet
 * Description:
 *
 * $Header: /home/users/jonah/cvs/handy/HandyApplet.java,v 1.3 2001/07/29 07:56:30 jonah Exp $
 * @version $Version: $
 */

import java.applet.Applet;
import java.awt.*;
import javax.swing.*;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.mouse.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import java.util.Vector;
import java.awt.event.*;
import java.util.Enumeration;

import Interaction.Axis;
import Interaction.RotateBehavior;
import Interaction.ResizeBehavior;

import FirstHand;

//   MouseRotateApp renders a single, interactively rotatable cube.
public class HandyApplet extends Applet {

    public static double innerInit = 0.6;
    public static double outerInit  = 0.025;
    public static double spanInit = 1.0;
    // feilds
    private TransformGroup objRotate;
    private float angle = 0.0f;
    private Transform3D trans = new Transform3D();

    JButton rotateButton = new JButton("Rotate");

    private SimpleUniverse simpleU = null;
    private ResizeBehavior resizeBehavior = null;
    private BranchGroup dynamicGroup = null;

   /**
    * constructor
    */
    public HandyApplet() {
    }

    /**
     *  init
     *  Create a simple scene and attach it to the virtual universe
     */
    public void init() {
        /** set up the look **/
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        this.add("Center", canvas3D);

        JPanel allControls = new JPanel();
        allControls.setLayout(new BorderLayout());
        // JPanel rotatePanel = new JPanel();
	// rotatePanel.add(rotateButton);

        resizeBehavior = new ResizeBehavior(this);
        this.resizeBehavior = resizeBehavior;

        JPanel resizePanel = resizeBehavior.getPanel();

        // allControls.add("North", rotatePanel);
        allControls.add("South", resizePanel);

        this.add("South", allControls);

        /** actually create the scene and control objects **/
        // SimpleUniverse is a Convenience Utility class
        simpleU = new SimpleUniverse(canvas3D);

        BranchGroup staticGroup = createStaticGroup();

        BranchGroup shapes = new BranchGroup();
        shapes.addChild(new FirstHand(innerInit, outerInit, spanInit, Math.PI * 3/2, FirstHand.UP));
        BranchGroup dynamicGroup = createDynamicGroup(shapes);
        this.dynamicGroup = dynamicGroup; // save reference so we can replaceme later

	// This will move the ViewPlatform back a bit so the
	// objects in the scene can be viewed.
        simpleU.getViewingPlatform().setNominalViewingTransform();
        simpleU.addBranchGraph(staticGroup);
        simpleU.addBranchGraph(dynamicGroup);
    }

    /**
     * createDynamicGroup
     */
    public BranchGroup createDynamicGroup(BranchGroup shapes) {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);

        objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

	objRoot.addChild(objRotate);

        objRotate.addChild(shapes);

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(objRotate);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(myMouseRotate);

	// create the AWTInteractionBehavior
	/** RotateBehavior rotBehavior = new RotateBehavior(objRotate);
	    rotateButton.addActionListener(rotBehavior);

          // perhaps use the same bounds for the myMouseRotate too
          BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
						   100.0);
	  rotBehavior.setSchedulingBounds(bounds);
	  objRoot.addChild(rotBehavior);
        **/

	// Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

	return objRoot;
    }

    /**
     * createStaticGroup
     */
    public BranchGroup createStaticGroup() {
	// Create the root of the branch graph
	BranchGroup objRoot = new BranchGroup();

        objRoot.addChild(new Axis());

        // perhaps use the same bounds for the myMouseRotate too
	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
						   100.0);
	resizeBehavior.setSchedulingBounds(bounds);
        objRoot.addChild(resizeBehavior);

	// Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();
	return objRoot;
    }

    /**
     * replaceDynamicGroup
     */
    public void replaceDynamicGroup(BranchGroup shapes) {
        BranchGroup newGroup = createDynamicGroup(shapes);
        simpleU.getLocale().replaceBranchGraph(dynamicGroup, newGroup);
        dynamicGroup = newGroup;  // save this for next replacement
    }

    /**
     * destroy
     */
    public void destroy() {
	simpleU.removeAllLocales();
    }

    /**
     * main
     * The following allows this to be run as an application
     *  as well as an applet
     */
    public static void main(String[] args) {
        System.out.print("HandyApplet.java \n");
        System.out.print("A model the Meru Foundation's First Hand Model\n");
        System.out.println("Hold the mouse button while moving the mouse to make the letters come to life.");
        Frame frame = new MainFrame(new HandyApplet(), 512, 512);
    }
}





