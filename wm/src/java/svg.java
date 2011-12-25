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

public class svg extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Calendar cal = new GregorianCalendar();
            String todayIs= cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH+1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + " ";

            String rfrsh = "2";
            try {
                rfrsh = request.getParameter("r");
            } catch (Exception e) {
            }
            String path = "";
            double lat = 0;
            double lng = 0;
            double dir = 0;
            double rol = 0;
            double til = 0;
            double sc_til = 0;
            String time = "";

            String err = "";
            String sqlReq = " select "
                    + "             srvtime, dvctime, orient1, orient2, orient3, X(\"position\"),Y(\"position\"),ext "
                    + " from timeline line order by srvtime desc limit 100; ";
            try {
                Class.forName("org.postgresql.Driver");
//                String url = "jdbc:postgresql://127.0.0.1:5432/gisdb";
//                String username = "pgsql";
//                String password = "pgsql";
                String url = "jdbc:postgresql://localhost:5432/gisdb_fps";
                String username = "postgres";
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
                            time = rs.getString(1);
                            sc_til = (til - (-90)) / (90 - (-90)) * (1.1 - 0.7) + 0.7;
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




            //data for view 
            //
            //lng lat til  path
            //<script type="application/ecmascript"> <![CDATA[ window.onload = function(){  document.getElementById('circle').onclick=function(){ var a = document.getElementById('edge2').getElementsByTagName('a')[0];  a.setAttributeNS( 'http://www.w3.org/1999/xlink', 'title', 'hi' ); } } ]]> </script>




            String doc = "<?xml version=\"1.0\" encoding=\"utf-8\"?> "
                    + "\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" ["
                    + "\n   <!ENTITY ns_imrep \"http://ns.adobe.com/ImageReplacement/1.0/\">"
                    + "\n   <!ENTITY ns_svg \"http://www.w3.org/2000/svg\">"
                    + "\n   <!ENTITY ns_xlink \"http://www.w3.org/1999/xlink\">"
                    + "\n ]>"
//                    + "\n <script type=\"application/ecmascript\">"
//                    + "\n <![CDATA[ "
//                    + "\n window.onload = function(){  "
//                    + "\n var ctime=document.getElementById('ctime');"
//                    + "\n ctime.InnerText=\"!\";"
//                    + "\n var textNodeLeftAligned = document.createElementNS(svgNS,\"text\");"
//                    + "\n textNodeLeftAligned.setAttributeNS(null,\"x\",0);"
//                    + "\n textNodeLeftAligned.setAttributeNS(null,\"y\",50);"
//                    + "\n textNodeLeftAligned.setAttributeNS(null,\"font-size\",10);"
//                    + "\n textNodeLeftAligned.setAttributeNS(null,\"font-family\",\"Arial,Helvetica\");"
//                    + "\n document.documentElement.appendChild(textNodeLeftAligned);"
//                    + "\n } ]]> "
//                    + "\n </script> "
                    + "\n <svg "
                    //+ "viewBox=\"0 0 270 400\""
                    + " viewBox=\"0 0 300 500\" "
                    + " width=\"100%\" height=\"100%\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
                    //+ "overflow=\"visible\" "
                    //+ "enable-background=\"new 0 0 300 300\" xml:space=\"preserve\"> "
                    + "\n "
                    + "\n <text id=\"ctime\" x='0' y='80'>" + todayIs + "</text> "
                    + "\n <text x='0' y='110'>" + time + "</text> "
                    + "\n <text x='0' y='130'>lng</text> "
                    + "\n <text x='30' y='130'>" + lng + "</text> "
                    + "\n <text x='0' y='150'>lat</text> "
                    + "\n <text x='30' y='150'>" + lat + "</text> "
                    + "\n <text x='0' y='170'>dir</text> "
                    + "\n <text x='30' y='170'>" + dir + "</text> "
                    + "\n <text x='0' y='190'>til</text> "
                    + "\n <text x='30' y='190'>" + til + "</text> "
                    + "\n <text x='0' y='210'>rol</text> "
                    + "\n <text x='30' y='210'>" + rol + "</text> "
                    + "\n <text x='0' y='230'>path</text> "
                    + "\n <text x='30' y='230'>" + path + "</text> "
                    //+ "\n <polyline fill=\"none\" stroke=\"red\" stroke-width=\"2\" points="+path+"/>"
                    //+ "\n<rect x=\"100\" y=\"0\" width=\"400\" height=\"400\" rx = \"10\" ry=\"5\" "
                    //+ "\n fill= \"none\" stroke=\"blue\" stroke-width=\"3\" > </rect>"
                    + "\n  </svg>";

            out.println(doc);

            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet svg</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet svg at " + request.getContextPath () + "</h1>");
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
