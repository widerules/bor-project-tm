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
/**
 *
 * @author Bor
 */
public class cmnd extends HttpServlet {
   
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
        String cmndR="";
           String cmnd = request.getParameter("cmnd");

           String sqlReq = "INSERT INTO cmnds(cmnd) VALUES ("+cmnd+");";
            try {
                try{
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://92.63.96.27:5432/gisdb";
                String username = "pgsql";
                String password = "pgsql";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                st.executeUpdate(sqlReq);
                st.close();
                con.close();

            } catch (Exception e) {
                out.print("Exception!:"+e);
            }

           sqlReq = "SELECT cmnd FROM cmnds order by id desc limit 1;";
                try{
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://92.63.96.27:5432/gisdb";
                String username = "pgsql";
                String password = "pgsql";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlReq);
                while (rs.next()) {
                    cmndR=rs.getString(1);
                }
                rs.close();
                st.close();
                con.close();

            } catch (Exception e) {
                out.print("Exception!:"+e);
            }
           

            //TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet cmnd</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet cmnd at " + request.getContextPath () + "</h1>");

            out.println("<form action='/cmnd'>");
            out.println("<input type=text name=cmnd value='"+cmndR+"'/>");
            out.println("<input type=submit />");
            out.println("</form>");


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
