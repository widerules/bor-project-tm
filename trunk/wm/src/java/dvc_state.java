/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Bor
 */
public class dvc_state extends HttpServlet {

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
        String command_state = "";
        String device_state = "";

        String cD="";
        String tD="";
        String tE="";

            String sqlReq = "SELECT now(),* FROM cmnds order by id desc limit 1;";
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/gisdb_fps";
                String username = "postgres";
                String password = "pgsql";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlReq);
                while (rs.next()) {
                    //cmndR = rs.getString(9);
                    for (int i = 1; i < 6; i++) {
                        String temp = "";
                        try {
                            temp = rs.getString(i).trim();
                            if (i==3){tD=temp;}
                            if (i==4){tE=temp;}
                        } catch (Exception e) {
                        }
                        ;
                        command_state += "|" + temp;
                    }
                }
                rs.close();
                st.close();
                con.close();
                //String fwd="";
            } catch (Exception e) {
                out.print("Exception select:" + e);
            }
            sqlReq = "SELECT id, srvtime, dvctime, orient1, orient2, orient3, X(\"position\"), Y(\"position\"),ext FROM timeline order by id desc limit 1;";
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://localhost:5432/gisdb_fps";
                String username = "postgres";
                String password = "pgsql";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlReq);
                while (rs.next()) {
                    //cmndR = rs.getString(9);
                    for (int i = 1; i < 9; i++) {
                        String temp = "";
                        try {
                            temp = rs.getString(i).trim();
                             if (i==4){cD=temp;}
                       } catch (Exception e) {
                        }
                        ;
                        device_state += "|" + temp;
                    }
                }
                rs.close();
                st.close();
                con.close();
                //String fwd="";
            } catch (Exception e) {
                out.print("Exception select:" + e);
            }
            //WEB OUT
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet cmnd</title>");
            out.println("<meta http-equiv=\"refresh\" content=\"3\">");
            out.println("</head>");
            out.println("<body>");
            out.println("device and command state: <br/>");
            out.println(command_state + "|" + device_state);
            out.println("<br/> tD|cD|tE <br/>");
            out.println( tD+" | "+cD+" | " + tE + " <br/>");
            out.println("</body>");
            out.println("</html>");

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
