/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;

//import javax.servlet.*;
//import javax.servlet.http.*;

import java.util.*;
import java.sql.*;
/*
 *
 * @author Bor
 */

public class planekml extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/vnd.google-earth.kml+xml");
        PrintWriter out = response.getWriter();
        try {
            String rfrsh="2";
            try{
             rfrsh  = request.getParameter("r");
            }catch(Exception e){}
            String path = "";
            double lat = 0;
            double lng = 0;
            double dir = 0;
            double rol = 0;
            double til = 0;
            double sc_til = 0;

            String err = "";
            String sqlReq = " select "
                    + "             srvtime, dvctime, orient1, orient2, orient3, X(\"position\"),Y(\"position\"),ext "
                    + " from timeline line order by srvtime desc limit 100; ";
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://127.0.0.1:5432/gisdb";
                String username = "pgsql";
                String password = "pgsql";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlReq);


                int j = 0;
                while (rs.next()) {

                    for (int i = 1; i < 9; i++) {
                        err += rs.getString(i) + "|";
                    }
                    path += "" + Double.valueOf(rs.getString(6)) + "," + Double.valueOf(rs.getString(7)) + ",0 \n ";
                    if (j == 0) {

                        try {
                            dir = Double.valueOf(rs.getString(3));
                            lat = Double.valueOf(rs.getString(7));
                            lng = Double.valueOf(rs.getString(6));
                            rol = Double.valueOf(rs.getString(5));
                            til = Double.valueOf(rs.getString(4));
                            sc_til=(til-(-90))/(90-(-90))*(1.1-0.7)+0.7;
                        } catch (Exception er) {
                        }

                    }
                    j++;
                    //dir = Double.valueOf(rs.getString(2));
                    //rs.getString(2);
                    //out.println(j+":"+tmpStr[j]+"<br>");
                }
                st.close();
                con.close();

            } catch (Exception e) {
                err = e.toString();
                //out.print("Exception!:" + e);
            }

            //Random rand = new Random();
            //double r = rand.nextDouble() / 1000;
            //double lat = 41 + r;
            //double lng = 45 + r;

            double p1_x = lng;
            double p1_y = lat + 0.002;

            double p2_x = lng + 0.001;
            double p2_y = lat - 0.001;

            double p3_x = lng - 0.001;
            double p3_y = lat - 0.001;


            double p1_x1 = lng;
            double p1_y1 = lat + 0.002 / 2;

            double p2_x1 = lng + 0.001 / 2;
            double p2_y1 = lat - 0.001 / 2;

            double p3_x1 = lng - 0.001 / 2;
            double p3_y1 = lat - 0.001 / 2;

            String plane = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">"
                    + "<Document>"
                    + "<Placemark>"
                    + "	<name>"+path+"</name>"
                    + "    <Style id=\"randomLabelColor\">"
                    + "     <IconStyle>"
                    + "        <Icon>"
                    + "          <href>http://maps.google.com/mapfiles/kml/pal4/icon28.png</href>"
                    + "        </Icon>"
                    + "      </IconStyle>"
                    + "      <LabelStyle>"
                    + "         <color>ffff00cc</color>"
                    + "         <colorMode>random</colorMode>"
                    + "         <scale>1.5</scale>"
                    + "      </LabelStyle>"
                    + "      <LineStyle>"
                    + "        <color>9900ff00</color>"
                    + "        <width>7</width>"
                    + "      </LineStyle>"
                    + "   </Style>"
                    + "        <styleUrl>#randomLabelColor</styleUrl>"
                   + "                    <MultiGeometry>"
                    + "	<Model id=\"model_1\">"
                    + "		<altitudeMode>relativeToGround</altitudeMode>"
                    + "		<Location>"
                    + "			<longitude>" + lng + "</longitude>"
                    + "			<latitude>" + lat + "</latitude>"
                    + "			<altitude>100</altitude>"
                    + "		</Location>"
                    + "		<Orientation>"
                    + "			<heading>" + dir + "</heading>"
                    + "			<tilt>" + til + "</tilt>"
                    + "			<roll>" + rol + "</roll>"
                    + "		</Orientation>"
                    + "		<Scale>"
                    + "			<x>1</x>"
                    + "			<y>1</y>"
                    + "			<z>1</z>"
                    + "		</Scale>"
                    + "		<Link>"
                    + "			<href>files/untitled.dae</href>"
                    + "		</Link>"
                    + "		<ResourceMap>"
                    + "		</ResourceMap>"
                    + "	</Model>"
                    + "    <Point>      "
                    + "<extrude>1</extrude>"
                    + "      <altitudeMode>relativeToGround</altitudeMode>"
                    + "      <coordinates>" + lng + "," + lat + ",350</coordinates>"
                    + "    </Point> "
                    + "        <Style>"
                    + "         <LineStyle>"
                    + " <color>ff00ff00</color>"
                    + "            <width>16</width>"
                    + "          </LineStyle>"
                    + "         </Style>"
                    + "                        <LineString> "
                    + "      <tessellate>1</tessellate>"
                    + " <extrude>1</extrude>"
                    + "      <altitudeMode>relativeToGround</altitudeMode>"
                    + "      <coordinates>"
                    + path
                    + "      </coordinates>"
                    + "    </LineString>"
                    + "</MultiGeometry>"
                    + "</Placemark>"
                    + "  <LookAt>"
                    //+ "<flyToMode>smooth</flyToMode>"
                    + "      <longitude>" + lng + "</longitude>"
                    + "      <latitude>" + lat + "</latitude>"
                    + "      <altitude>200</altitude>"
                    + "      <heading>"+dir+"</heading>"
                    + "      <tilt>75</tilt>"
                    + "      <range>500</range>"
                    + "      <altitudeMode>relativeToGround</altitudeMode>"
                    + "    </LookAt>"
                    + " <ScreenOverlay id=\"khScreenOverlay756\">"
                    + "  <name>Simple crosshairs</name>"
                    + "  <Icon>"
                    + "    <href>http://92.63.96.27:8180/wm/files/kren.png</href>"
                    + "  </Icon>"
                    + "  <overlayXY x=\"0.28\" y=\"0.8\" xunits=\"fraction\" yunits=\"fraction\"/>"
                    + "  <screenXY x=\"0\" y=\"0.9\" xunits=\"fraction\" yunits=\"fraction\"/>"
                    + "  <rotation>"+rol+"</rotation>"
                    + "  <size x=\"600\" y=\"0\" xunits=\"pixels\" yunits=\"pixels\"/>"
                    + "</ScreenOverlay>"
                    + " <ScreenOverlay id=\"khScreenOverlay757\">"
                    + "  <name>Simple crosshairs</name>"
                    + "  <Icon>"
                    + "    <href>http://92.63.96.27:8180/wm/files/base.png</href>"
                    + "  </Icon>"
                    + "  <overlayXY x=\"0.28\" y=\"0.8\" xunits=\"fraction\" yunits=\"fraction\"/>"
                    + "  <screenXY x=\"0\" y=\"0.9\" xunits=\"fraction\" yunits=\"fraction\"/>"
                    + "  <rotation>0</rotation>"
                    + "  <size x=\"600\" y=\"0\" xunits=\"pixels\" yunits=\"pixels\"/>"
                    + "</ScreenOverlay>"
                    + " <ScreenOverlay id=\"khScreenOverlay759\">"
                    + "  <name>Simple crosshairs</name>"
                    + "  <Icon>"
                    + "    <href>http://92.63.96.27:8180/wm/files/ground.png</href>"
                    + "  </Icon>"
                    + "  <overlayXY x=\"0.28\" y=\"0.8\" xunits=\"fraction\" yunits=\"fraction\"/>"
                    + "  <screenXY x=\"0\" y=\""+sc_til+"\" xunits=\"fraction\" yunits=\"fraction\"/>"
                    + "  <rotation>0</rotation>"
                    + "  <size x=\"600\" y=\"0\" xunits=\"pixels\" yunits=\"pixels\"/>"
                    + "</ScreenOverlay>"
                    + "</Document>"
                    + "</kml>";



            // out.println("'Content-Type: application/vnd.google-earth.kml+xml\n'");
            out.println(plane);

            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet kml</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet kml at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
