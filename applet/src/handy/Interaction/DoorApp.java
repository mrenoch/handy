package Interaction;
/*
 *      @(#)DoorApp.java 1.1 00/09/22 16:24
 *
 * Copyright (c) 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;


public class DoorApp extends Applet {


    public class OpenBehavior extends Behavior{

        private TransformGroup  targetTG;
        private WakeupCriterion pairPostCondition;
        private WakeupCriterion wakeupNextFrame;
        private WakeupCriterion AWTEventCondition;
        private Transform3D     t3D = new Transform3D();
        private Matrix3d        rotMat = new Matrix3d();
        private double          doorAngle;

        OpenBehavior(TransformGroup targetTG){
            this.targetTG = targetTG;
            AWTEventCondition = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
            wakeupNextFrame = new WakeupOnElapsedFrames(0);
        }

        public void setBehaviorObjectPartner(Behavior behaviorObject){
            pairPostCondition = new WakeupOnBehaviorPost(behaviorObject, 1);
        }

        public void initialize(){
            this.wakeupOn(AWTEventCondition);
            doorAngle = 0.0;
        }

        public void processStimulus(Enumeration criteria){
            if (criteria.nextElement().equals(pairPostCondition)){
                System.out.println("ready to open door");
                this.wakeupOn(AWTEventCondition);
                doorAngle = 0.0f;
            } else { // could be KeyPress or nextFrame, in either case: open
                if (doorAngle < 1.6){
                    doorAngle += 0.1;
                    if (doorAngle > 1.6) doorAngle = 1.6;
                    // get rotation and scale portion of transform
                    targetTG.getTransform(t3D);
                    t3D.getRotationScale(rotMat);
                    // set y-axis rotation to doorAngle
                    // (clobber any previous y-rotation, x and z scale)
                    rotMat.m00 = Math.cos(doorAngle);
                    rotMat.m22 = rotMat.m00;
                    rotMat.m02 = Math.sin(doorAngle);
                    rotMat.m20 = -rotMat.m02;
                    t3D.setRotation(rotMat);
                    targetTG.setTransform(t3D);
                    this.wakeupOn(wakeupNextFrame);
                } else { // finished opening door, signal other behavior
                    System.out.println("door is open");
                    this.wakeupOn(pairPostCondition);
                    postId(1);
                }
            }
        }

    } // end of class OpenBehavior

    public class CloseBehavior extends Behavior{

        private TransformGroup  targetTG;
        private WakeupCriterion pairPostCondition;
        private WakeupCriterion wakeupNextFrame;
        private WakeupCriterion AWTEventCondition;
        private Transform3D     t3D = new Transform3D();
        private Matrix3d        rotMat = new Matrix3d();
        private double          doorAngle;

        CloseBehavior(TransformGroup targetTG){
            this.targetTG = targetTG;
            AWTEventCondition = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
            wakeupNextFrame = new WakeupOnElapsedFrames(0);
        }

        public void setBehaviorObjectPartner(Behavior behaviorObject){
            pairPostCondition = new WakeupOnBehaviorPost(behaviorObject, 1);
        }

        public void initialize(){
            this.wakeupOn(pairPostCondition);
            doorAngle = 1.6;
        }

        public void processStimulus(Enumeration criteria){
            if (criteria.nextElement().equals(pairPostCondition)){
                System.out.println("ready to close door");
                this.wakeupOn(AWTEventCondition);
                doorAngle = 1.6f;
            } else { // could be KeyPress or nextFrame, in either case: close
                if (doorAngle > 0.0){
                    doorAngle -= 0.1;
                    if (doorAngle < 0.0) doorAngle = 0.0;
                    // get rotation and scale portion of transform
                    targetTG.getTransform(t3D);
                    t3D.getRotationScale(rotMat);
                    // set y-axis rotation to doorAngle
                    // (clobber any previous y-rotation, x and z scale)
                    rotMat.m00 = Math.cos(doorAngle);
                    rotMat.m22 = rotMat.m00;
                    rotMat.m02 = Math.sin(doorAngle);
                    rotMat.m20 = -rotMat.m02;
                    t3D.setRotation(rotMat);
                    targetTG.setTransform(t3D);
                    this.wakeupOn(wakeupNextFrame);
                } else { // finished opening door, signal other behavior
                    System.out.println("door is closed");
                    this.wakeupOn(pairPostCondition);
                    postId(1);
                }
            }
        }

    } // end of class CloseBehavior

    public BranchGroup createSceneGraph() {
	// Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();

        TransformGroup doorTG = new TransformGroup();
        doorTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        doorTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        OpenBehavior   openObject  = new OpenBehavior(doorTG);
        CloseBehavior  closeObject = new CloseBehavior(doorTG);

        //prepare the behavior objects
        openObject.setBehaviorObjectPartner(closeObject);
        closeObject.setBehaviorObjectPartner(openObject);


        // set scheduling bounds for behavior objects
        BoundingSphere bounds = new BoundingSphere();
        openObject.setSchedulingBounds(bounds);
        closeObject.setSchedulingBounds(bounds);

        // assemble scene graph
        objRoot.addChild(openObject);
        objRoot.addChild(closeObject);
        objRoot.addChild(doorTG);
        doorTG.addChild(new ColorCube(0.4));

	// Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    } // end of CreateSceneGraph method of DoorApp

    public DoorApp() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        // SimpleUniverse is a Convenience Utility class
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
        simpleU.getViewingPlatform().setNominalViewingTransform();

        BranchGroup scene = createSceneGraph();

        simpleU.addBranchGraph(scene);
    } // end of DoorApp (constructor)


    //  The following allows this to be run as an application
    //  as well as an applet

    public static void main(String[] args) {
        System.out.print("DoorApp.java \n- a demonstration of coordinating behavior ");
        System.out.println("objects for complex behaviors in a Java 3D scene.");
        System.out.println("When the app loads, press a key to make the 'door' open and close.");
        System.out.println("This is an example progam from The Java 3D API Tutorial.");
        System.out.println("The Java 3D Tutorial is available on the web at:");
        System.out.println("http://java.sun.com/products/java-media/3D/collateral");
        Frame frame = new MainFrame(new DoorApp(), 256, 256);
    } // end of main (method of DoorApp)

} // end of class DoorApp
