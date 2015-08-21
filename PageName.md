# Introduction #

/*** TmviewView.java
  * 
package tmview;**

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.util.**;
import java.sql.**;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;




/
  * The application's main frame.
  * 
public class TmviewView extends FrameView {

> private float m0;
> private float m1;
> private float m2;
> private float a0;
> private float a1;
> private float a2;
> private float lX;
> private float lY;
> private String ctime;

> public TmviewView(SingleFrameApplication app) {
> > super(app);


> initComponents();
> ActionListener actionListener = new ActionListener() {

> public void actionPerformed(ActionEvent actionEvent) {
> > loadData();

> }
> };
> Timer timer = new Timer(1000, actionListener);
> timer.start();

> // status bar initialization - message timeout, idle icon and busy animation, etc
> ResourceMap resourceMap = getResourceMap();
> int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
> messageTimer = new Timer(messageTimeout, new ActionListener() {

> public void actionPerformed(ActionEvent e) {
> > statusMessageLabel.setText("");

> }
> });
> messageTimer.setRepeats(false);
> int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
> for (int i = 0; i < busyIcons.length; i++) {
> > busyIcons[i](i.md) = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");

> }
> busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

> public void actionPerformed(ActionEvent e) {
> > busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
> > statusAnimationLabel.setIcon(busyIcons[busyIconIndex](busyIconIndex.md));

> }
> });
> idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
> statusAnimationLabel.setIcon(idleIcon);
> progressBar.setVisible(false);

> // connecting action tasks to status bar via TaskMonitor
> TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
> taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

> public void propertyChange(java.beans.PropertyChangeEvent evt) {
> > String propertyName = evt.getPropertyName();
> > if ("started".equals(propertyName)) {
> > > if (!busyIconTimer.isRunning()) {
> > > > statusAnimationLabel.setIcon(busyIcons[0](0.md));
> > > > busyIconIndex = 0;
> > > > busyIconTimer.start();

> > > }
> > > progressBar.setVisible(true);
> > > progressBar.setIndeterminate(true);

> > } else if ("done".equals(propertyName)) {
> > > busyIconTimer.stop();
> > > statusAnimationLabel.setIcon(idleIcon);
> > > progressBar.setVisible(false);
> > > progressBar.setValue(0);

> > } else if ("message".equals(propertyName)) {
> > > String text = (String) (evt.getNewValue());
> > > statusMessageLabel.setText((text == null) ? "" : text);
> > > messageTimer.restart();

> > } else if ("progress".equals(propertyName)) {
> > > int value = (Integer) (evt.getNewValue());
> > > progressBar.setVisible(true);
> > > progressBar.setIndeterminate(false);
> > > progressBar.setValue(value);

> > }

> }
> });
> }

> @Action
> public void showAboutBox() {
> > if (aboutBox == null) {
> > > JFrame mainFrame = TmviewApp.getApplication().getMainFrame();
> > > aboutBox = new TmviewAboutBox(mainFrame);
> > > aboutBox.setLocationRelativeTo(mainFrame);

> > }
> > TmviewApp.getApplication().show(aboutBox);

> }


> @Action
> public void loadData() {
> > try {


> int i = 0;
> String sqlReq = "select ctime,X(\"position\"),Y(\"position\"),m,m0,m1,m2,a0,a1,a2 from tm001 order by ctime desc limit 1;";
> Class.forName("org.postgresql.Driver");
> String url = "jdbc:postgresql://62.109.8.227:5432/gisdb";/**127.0.0.1**/
> String username = "pgsql";
> String password = "pgsql";
> Connection con = DriverManager.getConnection(url, username, password);
> Statement st = con.createStatement();
> ResultSet rs = st.executeQuery(sqlReq);
> jLabel1.setText("start");
> while (rs.next()) {
> > i++;
> > String intotext = ("===================\n");
> > intotext+= i + "\n";
> > intotext+="-------------------\n";


> ctime = rs.getString(1).trim();
> lX = Float.parseFloat(rs.getString(2).trim());
> lY = Float.parseFloat(rs.getString(3).trim());
> m0 = Float.parseFloat(rs.getString(5).trim());
> m1 = Float.parseFloat(rs.getString(6).trim());
> m2 = Float.parseFloat(rs.getString(6).trim());
> for (int j = 1; j <= 8; j++) {
> > intotext+=rs.getString(j).trim() + "|";

> }
> intotext+="===================\n";
> jTextArea1.insert(intotext, 0);
> jTextArea1.setCaretPosition(0);
> jTextArea1.repaint();
> }
> } catch (Exception e) {
> > jTextArea1.append("" + e);

> }
> jLabel2.setText("stop");
> }


> / This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    * 
> @SuppressWarnings("unchecked")
> // 

&lt;editor-fold defaultstate="collapsed" desc="Generated Code"&gt;


> private void initComponents() {

> mainPanel = new javax.swing.JPanel();
> jLabel1 = new javax.swing.JLabel();
> jLabel2 = new javax.swing.JLabel();
> jLabel3 = new javax.swing.JLabel();
> jButton1 = new javax.swing.JButton();
> canvas1 = new java.awt.Canvas();
> jScrollPane1 = new javax.swing.JScrollPane();
> jTextArea1 = new javax.swing.JTextArea();
> menuBar = new javax.swing.JMenuBar();
> javax.swing.JMenu fileMenu = new javax.swing.JMenu();
> javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
> javax.swing.JMenu helpMenu = new javax.swing.JMenu();
> javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
> statusPanel = new javax.swing.JPanel();
> javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
> statusMessageLabel = new javax.swing.JLabel();
> statusAnimationLabel = new javax.swing.JLabel();
> progressBar = new javax.swing.JProgressBar();

> mainPanel.setName("mainPanel"); // NOI18N

> org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(tmview.TmviewApp.class).getContext().getResourceMap(TmviewView.class);
> jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
> jLabel1.setName("jLabel1"); // NOI18N

> jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
> jLabel2.setName("jLabel2"); // NOI18N

> jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
> jLabel3.setName("jLabel3"); // NOI18N

> javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(tmview.TmviewApp.class).getContext().getActionMap(TmviewView.class, this);
> jButton1.setAction(actionMap.get("loadData")); // NOI18N
> jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
> jButton1.setName("jButton1"); // NOI18N

> canvas1.setName("canvas1"); // NOI18N

> jScrollPane1.setName("jScrollPane1"); // NOI18N

> jTextArea1.setColumns(20);
> jTextArea1.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
> jTextArea1.setRows(5);
> jTextArea1.setName("jTextArea1"); // NOI18N
> jScrollPane1.setViewportView(jTextArea1);

> javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
> mainPanel.setLayout(mainPanelLayout);
> mainPanelLayout.setHorizontalGroup(
> > mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
> > .addGroup(mainPanelLayout.createSequentialGroup()
> > > .addContainerGap()
> > > .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
> > > > .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT\_SIZE, 380, Short.MAX\_VALUE)
> > > > .addGroup(mainPanelLayout.createSequentialGroup()
> > > > > .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
> > > > > > .addComponent(jLabel1)
> > > > > > .addComponent(jLabel3)
> > > > > > .addComponent(jLabel2))

> > > > > .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
> > > > > .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
> > > > > > .addComponent(canvas1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT\_SIZE, 336, Short.MAX\_VALUE)
> > > > > > .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT\_SIZE, 336, Short.MAX\_VALUE))))

> > > .addContainerGap())

> );
> mainPanelLayout.setVerticalGroup(
> > mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
> > .addGroup(mainPanelLayout.createSequentialGroup()
> > > .addGap(22, 22, 22)
> > > .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
> > > > .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT\_SIZE, javax.swing.GroupLayout.DEFAULT\_SIZE, Short.MAX\_VALUE)
> > > > .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT\_SIZE, javax.swing.GroupLayout.DEFAULT\_SIZE, Short.MAX\_VALUE))

> > > .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
> > > .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
> > > > .addGroup(mainPanelLayout.createSequentialGroup()
> > > > > .addComponent(jLabel2)
> > > > > .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
> > > > > .addComponent(jLabel3))

> > > > .addComponent(canvas1, 0, 0, Short.MAX\_VALUE))

> > > .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
> > > .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT\_SIZE, 137, Short.MAX\_VALUE)
> > > .addContainerGap())

> );

> menuBar.setName("menuBar"); // NOI18N

> fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
> fileMenu.setName("fileMenu"); // NOI18N

> exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
> exitMenuItem.setName("exitMenuItem"); // NOI18N
> fileMenu.add(exitMenuItem);

> menuBar.add(fileMenu);

> helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
> helpMenu.setName("helpMenu"); // NOI18N

> aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
> aboutMenuItem.setName("aboutMenuItem"); // NOI18N
> helpMenu.add(aboutMenuItem);

> menuBar.add(helpMenu);

> statusPanel.setName("statusPanel"); // NOI18N

> statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

> statusMessageLabel.setName("statusMessageLabel"); // NOI18N

> statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
> statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

> progressBar.setName("progressBar"); // NOI18N

> javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
> statusPanel.setLayout(statusPanelLayout);
> statusPanelLayout.setHorizontalGroup(
> > statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
> > .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT\_SIZE, 400, Short.MAX\_VALUE)
> > .addGroup(statusPanelLayout.createSequentialGroup()
> > > .addContainerGap()
> > > .addComponent(statusMessageLabel)
> > > .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX\_VALUE)
> > > .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED\_SIZE, javax.swing.GroupLayout.DEFAULT\_SIZE, javax.swing.GroupLayout.PREFERRED\_SIZE)
> > > .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
> > > .addComponent(statusAnimationLabel)
> > > .addContainerGap())

> );
> statusPanelLayout.setVerticalGroup(
> > statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
> > .addGroup(statusPanelLayout.createSequentialGroup()
> > > .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED\_SIZE, 2, javax.swing.GroupLayout.PREFERRED\_SIZE)
> > > .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT\_SIZE, Short.MAX\_VALUE)
> > > .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
> > > > .addComponent(statusMessageLabel)
> > > > .addComponent(statusAnimationLabel)
> > > > .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED\_SIZE, javax.swing.GroupLayout.DEFAULT\_SIZE, javax.swing.GroupLayout.PREFERRED\_SIZE))

> > > .addGap(3, 3, 3))

> );

> setComponent(mainPanel);
> setMenuBar(menuBar);
> setStatusBar(statusPanel);
> }// 

&lt;/editor-fold&gt;


> // Variables declaration - do not modify
> private java.awt.Canvas canvas1;
> private javax.swing.JButton jButton1;
> private javax.swing.JLabel jLabel1;
> private javax.swing.JLabel jLabel2;
> private javax.swing.JLabel jLabel3;
> private javax.swing.JScrollPane jScrollPane1;
> private javax.swing.JTextArea jTextArea1;
> private javax.swing.JPanel mainPanel;
> private javax.swing.JMenuBar menuBar;
> private javax.swing.JProgressBar progressBar;
> private javax.swing.JLabel statusAnimationLabel;
> private javax.swing.JLabel statusMessageLabel;
> private javax.swing.JPanel statusPanel;
> // End of variables declaration
> private final Timer messageTimer;
> private final Timer busyIconTimer;
> private final Icon idleIcon;
> private final Icon[.md](.md) busyIcons = new Icon[15](15.md);
> private int busyIconIndex = 0;
> private JDialog aboutBox;
}