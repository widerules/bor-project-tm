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

public class testkml extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            Calendar cal = new GregorianCalendar();
            out.println(cal.get(Calendar.YEAR) + " " + cal.get(Calendar.MONTH) + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.SECOND) + " <br/>");

            Double lat = 41.0+cal.get(Calendar.SECOND)/1000;
            Double lng = 41.0;
            Double v0 = 1.0;
            Double v1 = 1.0;
            Double v2 = 1.0;
            //Double ms =
            String err = "";
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

                while (rs.next()) {
                    for (int i = 1; i < 9; i++) {
                        err += rs.getString(i) + "|";
                    }
                    try {
                    err =rs.getString(1);
                        lat = Double.valueOf(rs.getString(7))+cal.get(Calendar.SECOND)/1000;
                        lng = Double.valueOf(rs.getString(6));
                        v0 = Double.valueOf(rs.getString(3));
                        v1 = Double.valueOf(rs.getString(4));
                        v2 = Double.valueOf(rs.getString(5));
                    } catch (Exception er) {
                    }
                    //dir = Double.valueOf(rs.getString(2));
                    //rs.getString(2);
                    //out.println(j+":"+tmpStr[j]+"<br>");
                }
                st.close();
                con.close();


                //view
                out.println("<head> "
                        + "    <meta http-equiv=\"refresh\" content=\"1;url=http://localhost:8080/testkml\">"
                        + "</head>");

                out.println("st " + err + "<br/>");
                out.println("lat " + lat + "<br/>");
                out.println("lng " + lng + "<br/>");
                out.println("v0 " + v0 + "<br/>");
                out.println("v1 " + v1 + "<br/>");
                out.println("v2 " + v2 + "<br/>");

            } catch (Exception e) {
                err = e.toString();
                //out.print("Exception!:" + e);
            }
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
