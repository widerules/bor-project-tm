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

public class kml extends HttpServlet {

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
            double lat = 0;
            double lng = 0;
            double dir = 0;
            /*
             *                     + "<Placemark>\n "
            + " <name>View-centered placemark</name>\n "
            + "         <LineString> <tessellate>1</tessellate> "
            + " <coordinates> "
            + " " + lng + ", " +lat+ " ,0 "
            + " " + lng2 + ", " +lat2+ " ,0 "
            + "  </coordinates> </LineString> "
            //+ "<Point>\n "
            //+ " <coordinates>45.0,45.0</coordinates>\n "
            //+ " </Point>\n "

            + "  </Placemark>\n "

             * */
             String err ="";
            String sqlReq = " select "
                    + "             srvtime, dvctime, orient1, orient2, orient3, X(\"position\"),Y(\"position\"),ext "
                    + " from timeline line order by srvtime desc limit 1; ";
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://127.0.0.1:5432/gisdb";
                String username = "postgres";
                String password = "postgres";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlReq);

                while(rs.next()){
                    for (int i=1;i<9;i++){
                        err += rs.getString(i)+"|";
                    }
                    try {
                lat = Double.valueOf(rs.getString(7));
                lng = Double.valueOf(rs.getString(6));
                    }catch(Exception er){}
                //dir = Double.valueOf(rs.getString(2));
                //rs.getString(2);
                    //out.println(j+":"+tmpStr[j]+"<br>");
                }
                st.close();
                con.close();

            } catch (Exception e) {
                err=e.toString();
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


            String plm = "    <Style id=\"yellowLineGreenPoly\">"
                    + "      <LineStyle>"
                    + "        <color>7f00ffff</color>"
                    + "        <width>14</width>"
                    + "      </LineStyle>"
                    + "      <PolyStyle>"
                    + "        <color>7f00ff00</color>"
                    + "      </PolyStyle>"
                    + "    </Style> "
                    + "<Placemark> "
                    + "<name>The Pentagon"+err+"</name> "
                    + "<styleUrl>#yellowLineGreenPoly</styleUrl> "
                    // + "    <Polygon>"
                    //  + "      <extrude>1</extrude>"
                    + " <LineString>"
                    + "        <extrude>1</extrude>"
                    + "        <tessellate>1</tessellate>"
                    + "      <altitudeMode>relativeToGround</altitudeMode>"
                    // + "      <outerBoundaryIs>"
                    // + "        <LinearRing>"
                    + "          <coordinates>"
                    + "            " + p1_x + "," + p1_y + ",10 "
                    + "            " + p2_x + "," + p2_y + ",10 "
                    + "            " + p3_x + "," + p3_y + ",10 "
                    + "            " + p1_x + "," + p1_y + ",10 "
                    + "          </coordinates>"
                    //  + "        </LinearRing>"
                    //  + "      </outerBoundaryIs>"
                    //  + "      <innerBoundaryIs>"
                    //  + "        <LinearRing>"
                    //  + "          <coordinates>"
                    //  + "            " + p1_x1 + "," + p1_y1 + ",100 "
                    //  + "            " + p2_x1 + "," + p2_y1 + ",100 "
                    //   + "            " + p3_x1 + "," + p3_y1 + ",100 "
                    //   + "            " + p1_x1 + "," + p1_y1 + ",100 "
                    //   + "          </coordinates>"
                    //   + "        </LinearRing>"
                    //   + "      </innerBoundaryIs>"
                    //   + "    </Polygon>"
                    + " </LineString> "
                    + "  </Placemark>";


            String kml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
                    + "<Document>"
                    + plm
                    + "</Document>"
                    + "</kml>";

            // out.println("'Content-Type: application/vnd.google-earth.kml+xml\n'");
            out.println(kml);

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
