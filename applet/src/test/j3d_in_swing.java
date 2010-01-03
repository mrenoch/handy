
/*
*  This code demonstrates j3d in a Swing environment.
*
*  It creates a menu system that can draw on top of a Canvas3D
*
*  It puts several 3D scenes into a Tabbed
*
*  It creates two 3D windows on a Desktop.  These do not work well because Canvas3D objects
*  always draw on top due to a heavyweight/lightweight issue. See ProblemChild
*  Note that Swing uses lightweight components and j3d heavy.  This means that j3d will get drawn on top
*  of swing components, so JInternalFrames are not really a good idea for j3d.
#  This can be fixed for popup menus but not for JInternalFrames.
*  The Swing team says it may fix this in the next version of Swing.   That would be cool.  see
*  http://java.sun.com/products/jfc/tsc/articles/mixing/index.html
*
*
*  You can rotate the scenes in each window by left mouse clicking and moving the mouse
*
*
*  Copyright: Karl Meissner
*  meissner-sd@ieee.org
*  http://meissner.v0.net/java.html
*
* Karl Meissner grants you ("Licensee") a non-exclusive, royalty free, license to use,
* modify and redistribute this software in source and binary code form,
* provided that this copyright notice and license appear on all copies of
* the software;
*
* This software is provided "AS IS," without a warranty of any kind. ALL
* EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
* IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
* NON-INFRINGEMENT, ARE HEREBY EXCLUDED. Karl Meissner SHALL NOT BE
* LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
* OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL Karl Meissner
* BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
* INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
* CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
* OR INABILITY TO USE SOFTWARE, EVEN IF Karl Meissner HAS BEEN ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGES.
*
* This software is not designed or intended for use in on-line control of
* aircraft, air traffic, aircraft navigation or aircraft communications; or in
* the design, construction, operation or maintenance of any nuclear
* facility. Licensee represents and warrants that it will not use or
* redistribute the Software for such purposes.
*  this code demonstrates a interaction between swing and and j3d
*
*  This code should be used with JDK 1.3 download from Sun with j3d 1.2.1 also from Sun
*
*/

package java_in_swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;

// import j3d stuff - note they must be correctly installed on your system - typicall in ~\jdk1.3\jre\lib\ext\
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;


/**
 * Title: Label3D
 * Description: Places a Text2D in a scene, allows the text to be seen from the back
 * Copyright:    Copyright (c) 2001
 * Company:      Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */

class Label3D
  extends TransformGroup
 {

  public Label3D( float x, float y, float z, String msg )
  {
      super();

      // place it in the scene graph
      Transform3D offset = new Transform3D();
      offset.setTranslation( new Vector3f( x, y, z ));
      this.setTransform( offset );

      // face it in the scene graph
      Transform3D rotation = new Transform3D();
      TransformGroup rotation_group = new TransformGroup( rotation );
      this.addChild( rotation_group );

      // make a texture mapped polygon
      Text2D msg_poly = new Text2D( msg, new
                              Color3f( 1.0f, 1.0f, 1.0f),
                              "Helvetica", 18, Font.PLAIN );


      // set it to draw both the front and back of the poly
      PolygonAttributes msg_attributes = new PolygonAttributes();
      msg_attributes.setCullFace( PolygonAttributes.CULL_NONE );
      msg_attributes.setBackFaceNormalFlip( true );
      msg_poly.getAppearance().setPolygonAttributes( msg_attributes );

      // attach it
      rotation_group.addChild( msg_poly );
    }

}


/**
 * Title: Wrap3D
 * Description: this holds a j3d rendered image in a Swing container.
 *              Now you can plug this into anything that takes Swing objects
 *
 * Copyright:    Copyright (c) 2001
 * Company:      Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */

class Wrap3D
  extends JPanel
{

  // make a scene with a cube and a label
  BranchGroup createSceneGraph( int scene_type ) {

    BranchGroup objRoot = new BranchGroup();
    TransformGroup root_group = new TransformGroup(  );
    root_group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); // allow the mouse behavior to rotate the scene
    root_group.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    objRoot.addChild( root_group );  // this is the local origin  - everyone hangs off this - moving this move every one

    switch( scene_type )  // decide what to draw
    {
      case 0 :
        root_group.addChild( new ColorCube(0.2) );  // add a color cube
        root_group.addChild( new Label3D( 0.2f, 0.2f, 0.0f, "ColorCube") ); // add a label to the scene
        break;
      case 1 :
        Transform3D sphere_transform = new Transform3D();
        sphere_transform.setScale( 0.2);       // shrink the sphere so our view point is not inside it
        TransformGroup sphere_group = new TransformGroup( sphere_transform );
        sphere_group.addChild( new Sphere() );  // add a sphere
        root_group.addChild( sphere_group );

        root_group.addChild( new Label3D( 0.2f, 0.2f, 0.0f, "sphere") ); // add a label to the scene

        BoundingSphere bounds = new BoundingSphere(); // the render will only light objects inside this volume
        bounds.setRadius(1000.0);

        // add a light so we can see the sphere
        DirectionalLight lightD = new DirectionalLight();
        lightD.setInfluencingBounds(bounds);
        lightD.setDirection(new Vector3f(.7f, -.7f, -.7f));  // point the light right, down, into the screen
        objRoot.addChild(lightD);
        break;
    }


    // another J3D bug with JInternalFrames - if there are a lot of Text2D obects and several scenes,
    // there is a strange interaction
    // between selecting windows and texture memory.  After selecting several
    // JInternalFrames with Canvas3D items, Text2D items start
    // drawing as opaque white rectangles.
    // the only work around I can find is to either only have a single scene or
    // not to use Text2D
    // I would appriciate any other workarounds for JInternalFrames people
    // can come up with - meissner-sd@ieee.org
    // reproduce bug HERE
/*
        int Num_Text_Msg;
        Num_Text_Msg=20;

        int h;
        for ( h = 0; h<Num_Text_Msg; ++h) {
          Transform3D msg_trans = new Transform3D();
          msg_trans.setTranslation( new Vector3d( Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5 ) );

          TransformGroup msg_group = new TransformGroup( msg_trans );
          Text2D center_msg  = new Text2D( "text" + h,
                                    new Color3f( 1.0f, 1.0f, 1.0f),
                                  "Helvetica", 24, Font.PLAIN );
          msg_group.addChild( center_msg );
          objRoot.addChild( msg_group );
        }
*/


    MouseRotate mouseRotate = new MouseRotate( root_group );  // add the mouse behavior
    mouseRotate.setSchedulingBounds( new BoundingSphere() );
    objRoot.addChild( mouseRotate);
    return objRoot;
  }



  public Wrap3D( int scene_type)
  {
      // construct the 3D image
      Canvas3D canvas3D = new Canvas3D( null );
      SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
      BranchGroup scene = createSceneGraph( scene_type );
      simpleU.getViewingPlatform().setNominalViewingTransform();       // This will move the ViewPlatform back a bit so the
      simpleU.addBranchGraph(scene);

      this.setLayout( new BorderLayout() );
      this.setOpaque( false );
      this.add("Center", canvas3D);   // <-- HERE IT IS - tada! j3d in swing

    }
}



//##############################################################################




/**
 * Title: ProblemChild
 * Description: this is an internal window to the desktop
 *
 *              I included it to show problems with JInternalFrame and J3D.
 *
 *              Since JInternalFrame is lightweight
 *              and the Canvas3D class if heavy weight, the Canvas3D will *ALWAYS*
 *              draw on top, reguardless of the window order in swing
 *
 *              see http://java.sun.com/products/jfc/tsc/articles/mixing/index.html
 *
 *              The only really workaround is UI design.  Lay out your screens so the
 *              3D elements can not be repositioned.   So the user will never see the over draw problem.
 *              It may be possible to use AWT style heavyweight components to get around this.
 *
 *
 * Copyright:    Copyright (c) 2001
 * Company:      Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */

class ProblemChild
  extends JInternalFrame {

  public ProblemChild( String msg, int x, int y, int scene_type ) {
      super(msg, true, true, true);        // title, resizable, closable, maximizable
      Dimension mySize = new Dimension( );
      mySize.height = j3d_in_swing.theOuterframe.screenSize.height / 5; // make a window based on local screen resolution
      mySize.width = j3d_in_swing.theOuterframe.screenSize.width / 5;
      this.setSize( mySize );
      this.setLocation( x, y);
      this.getContentPane().add( new Wrap3D( scene_type ) );  // add the 3D rendered object to the frame
      this.show();  // make it appear in the Desktop - needed for JDK1.3
 }
}
// the JInternalFrame is a lightweight component - the AWT button is heavy weight
class WimpyChild
  extends JInternalFrame {

  public WimpyChild( int x, int y) {
      super("i get overdrawn", true, true, true);  // title, resizable, closable, maximizable
      Dimension mySize = new Dimension( );
      mySize.height = j3d_in_swing.theOuterframe.screenSize.height / 5; // make a window based on local screen resolution
      mySize.width = j3d_in_swing.theOuterframe.screenSize.width / 5;
      this.setSize( mySize );
      this.getContentPane().setLayout( new FlowLayout());
      this.setLocation( x, y);
      Button btn = new Button( "Heavyweight AWT Button");
      btn.setSize( new Dimension( 50, 30) );
      this.getContentPane().add( "North", btn );  // add the 3D rendered object to the frame
      this.show();  // make it appear in the Desktop - needed for JDK1.3
  }
}


/**
 * Title:  Desktop
 * Description:  the desktop - put JInternalFrames into this
 * Company: Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */

class Desktop extends JDesktopPane {

  public Desktop( ) {
    super();
    this.setPreferredSize( j3d_in_swing.theOuterframe.screenSize );
    this.setBackground( Color.gray );

    // hardwired to create our two sample frames
    this.add( new ProblemChild( "a window", 0, 0, 0) );
    this.add( new ProblemChild( "some other window", 340, 0, 1 ) );
    this.add( new WimpyChild( 0, 300 ) );
    j3d_in_swing.theDesktop = this; // make this a global variable

  }
}

//##############################################################################

/**
 * Title:        Tabbed
 * Description:  this is a better way to hold Wrap3D since it does not suffer from over draw
 * Company: Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */
class Tabbed
  extends JTabbedPane{

  public Tabbed() {
    this.addTab( "cube", new Wrap3D( 0 ) );
    this.addTab( "sphere", new Wrap3D( 1 ) );


    // another J3D bug work around
    // for some reason the first time the tabbed item is drawn,
    // tab 0 is selected but the sphere (tab 1) is drawn
    // manually selecting the tabs synchs the tab and the draw order
    // it is harmless but it is still annoying
    // so we force tab 1 to be selected and hide the bug from the user
    // note that selecting 0 does not work....
    this.setSelectedIndex( 1 );
  }
}


//##############################################################################


/**
 * Title:        CwareIDE
 * Description:  Break the Frame into two parts
 * Company: Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */
class SplitFrame
  extends JSplitPane
{

  public SplitFrame( ) {
    super( JSplitPane.VERTICAL_SPLIT );
    this.setDividerLocation( j3d_in_swing.theOuterframe.screenSize.height / 2 );
    add( new Desktop() );
    add( new Tabbed() );


  }
}

//##############################################################################

/**
 * Title:        CreateWindowAction
 * Description:  respond to a menu selection
 * Company: Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */
class CreateWindowAction extends AbstractAction {

  int window_type;
  public CreateWindowAction  ( String label, int w ) {
    super( label );
    window_type = w;
  }

  public void actionPerformed(ActionEvent ev) {
    j3d_in_swing.theDesktop.add( new ProblemChild( "some other window", 340, 200, window_type ) );
  }
}



/**
 * Title: MenuMgr
 * Description:   this class holds the menus
 * Company: Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */
class MenuMgr extends JMenuBar
{

  JMenu[] menu;

// the id of the menus
  final int kNUM_MENUS=2;
  final int kSUB0=0;
  final int kSUB1=1;

  public MenuMgr() {

    super(); // do the parent stuff


    // this makes all popups heavy weight - this is a speed an resource hit
    // it ensures that all your menus get draw on top of the the Canvas3D
    JPopupMenu.setDefaultLightWeightPopupEnabled( false );


//-----------------------------------------------------
    menu = new JMenu[ kNUM_MENUS ];  //hold each menu - typically do this so it
                                      // easy to dynamically shuffle the items in the menu - though I do not do it in this code


    //I am purposefully putting a lot of items into the menu to demonstrate that menus, unlike
    // other swing objects can be drawn on top of Canvas3D
    menu[ kSUB0 ] = new JMenu( "Menu0" );
    menu[ kSUB0 ].add( new CreateWindowAction( "make cube", 0) );
    menu[ kSUB0 ].add( new CreateWindowAction( "make cube", 0) );
    menu[ kSUB0 ].add( new CreateWindowAction( "make cube", 0) );
    menu[ kSUB0 ].add( new CreateWindowAction( "make cube", 0) );
    menu[ kSUB0 ].add( new CreateWindowAction( "make cube", 0) );
    add( menu[ kSUB0 ] );
//-----------------------------------------------------

    menu[ kSUB1 ] = new JMenu( "Menu1" );
    menu[ kSUB1 ].add( new CreateWindowAction( "make sphere", 1) );
    menu[ kSUB1 ].add( new CreateWindowAction( "make sphere", 1) );
    menu[ kSUB1 ].add( new CreateWindowAction( "make sphere", 1) );
    menu[ kSUB1 ].add( new CreateWindowAction( "make sphere", 1) );
    menu[ kSUB1 ].add( new CreateWindowAction( "make sphere", 1) );
    add( menu[ kSUB1 ] );
//-----------------------------------------------------

    j3d_in_swing.theOuterframe.setJMenuBar(this);  // make this the menu


  } //  public MenuMgr()
}// public class MenuMgr extends JMenuBar




/**
 * Title: OuterFrame
 * Description:   The outer frame that is a window app
 * Company: Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */
class OuterFrame
  extends JFrame
{

  public static Dimension screenSize;
  public OuterFrame() {
    super();

    try { // use the local look and feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace( System.err );
    }


    screenSize = Toolkit.getDefaultToolkit().getScreenSize();  // what does the user have his resolution set too?
    this.setSize(screenSize  );  // lets be full screen


    j3d_in_swing.theOuterframe = this; // make this a global variable

   // we need to manually catch the kill window event so all our threads get completely shut down
    WindowListener l = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0); // kill all threads including the render and swing
            }
		};
    this.addWindowListener(l);


    this.getContentPane().add( new SplitFrame() ); // make the window two sub frames

    new MenuMgr(); // make the menus

    this.show(); // start drawing itself
  }


}


/**
 * Title: j3d_in_swing
 * Description:  the main procedure
 * Company: Meissner Software Development, LLC
 * @author Karl Meissner
 * @version 1.0
 */
public class j3d_in_swing {

  // these are singleton classes.   Make them public static so everyone can use them
  // and we do not need to pass them all over the place
  public static OuterFrame theOuterframe;
  public static Desktop theDesktop;

  public static void main(String[] args) {
    new OuterFrame(); // make the window
  }


}



