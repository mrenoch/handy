/**
 * Title:       ResizeBehavior
 * Description:
 *
 * $Header: /home/users/jonah/cvs/handy/Interaction/ResizeBehavior.java,v 1.2 2001/07/29 07:56:31 jonah Exp $
 * $Version: $
 */

package Interaction;

import java.applet.Applet;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.swing.*;
import java.util.Enumeration;
import java.text.*;

import FirstHand;
import HandyApplet;

public class ResizeBehavior extends Behavior implements ActionListener {

  private HandyApplet handyApplet;
  private WakeupCriterion criterion;

  private  JButton resizeButton = new JButton("Resize");
  private  JLabel innerRadiusLabel = new JLabel("inner Radius");
  private  JTextField innerRadius = new JTextField(String.valueOf(HandyApplet.innerInit), 4);
  private  JLabel outerRadiusLabel = new JLabel("outer Radius");
  private  JTextField outerRadius = new JTextField(String.valueOf(HandyApplet.outerInit), 4);
  private  JLabel spanLabel = new JLabel("Span");
  private  JTextField span = new JTextField(String.valueOf(HandyApplet.spanInit), 4);

 /**
  * constructor
  */
  public ResizeBehavior(HandyApplet handyApplet) {
      resizeButton.addActionListener(this);
      innerRadius.addActionListener(this);
      outerRadius.addActionListener(this);
      span.addActionListener(this);
      this.handyApplet = handyApplet;
  }

  /**
   * initialize
   */
  public void initialize() {
    /**@todo: implement this javax.media.j3d.Behavior abstract method*/
    criterion = new WakeupOnBehaviorPost(this, MouseEvent.MOUSE_CLICKED);
    wakeupOn(criterion);
  }

  /**
   *
  public void setOldDynamicGroup(BranchGroup oldGroup) {
      this.oldGroup = oldGroup;
  }
   */

  /**
   * processStimulus
   */
  public void processStimulus(Enumeration criteria) {
    /**@todo: implement this javax.media.j3d.Behavior abstract method*/
    double innerVal = 0, outerVal = 0 , spanVal = 0;


    try {
      outerVal = Double.parseDouble(outerRadius.getText());
      innerVal = Double.parseDouble(innerRadius.getText());
      spanVal  = Double.parseDouble(span.getText());
    } catch (NumberFormatException e) {
      System.err.println("bad numbers");
      wakeupOn(criterion);
      return;
    }

    /**
     *
       add a cylyndrical thumb
       Appearance ap = new Appearance();
       Color3f color = new Color3f(0f, 1.0f, 0f);
       ap.setColoringAttributes(new ColoringAttributes(color, ColoringAttributes.FASTEST));
       objRotate.addChild(new Cylinder(0.1f, 4f, ap));
    */

    System.out.println("I have arisen: " + outerVal + "/" + innerVal + "/" + spanVal);

    BranchGroup shapes = new BranchGroup();
    shapes.addChild(new FirstHand(innerVal, outerVal, spanVal, Math.PI * 3/2, FirstHand.UP));
    handyApplet.replaceDynamicGroup(shapes);

    wakeupOn(criterion);
  }

  /**
   * when the mouse is clicked, postId for the behavior
   */
  public void actionPerformed(ActionEvent e) {
    postId(MouseEvent.MOUSE_CLICKED);
  }

  /**
   * getPanel
   */
   public JPanel getPanel() {
    JPanel resizePanel = new JPanel();

    resizePanel.setLayout(new FlowLayout());
    resizePanel.add(resizeButton);
    resizePanel.add(innerRadiusLabel);
    resizePanel.add(innerRadius);
    resizePanel.add(outerRadiusLabel);
    resizePanel.add(outerRadius);
    resizePanel.add(spanLabel);
    resizePanel.add(span);

    /*
    GridBagLayout gridBag = new GridBagLayout();
    resizePanel.setLayout(gridBag);

    JLabel[] labels = { innerRadiusLabel, outerRadiusLabel, spanLabel };
    JTextField[] feilds = { innerRadius, outerRadius, span };
    resizePanel.add(resizeButton);
    addLabelTextRows(labels, feilds, gridBag, resizePanel);
    */
    return resizePanel;
  }

  /**
   * copied from sun's sample
   */
  private void addLabelTextRows(JLabel[] labels,
                                JTextField[] textFields,
                                GridBagLayout gridbag,
                                Container container) {
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.EAST;
    int numLabels = labels.length;

    for (int i = 0; i < numLabels; i++) {
      c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
      c.fill = GridBagConstraints.NONE;      //reset to default
      c.weightx = 0.0;                       //reset to default
      gridbag.setConstraints(labels[i], c);
      container.add(labels[i]);

      c.gridwidth = GridBagConstraints.REMAINDER;     //end row
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      gridbag.setConstraints(textFields[i], c);
      container.add(textFields[i]);
    }
  }
}



