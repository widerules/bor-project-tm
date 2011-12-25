/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

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
        String cmndR = "";
        String cmndR_fwd = "0";
        String cmnd = request.getParameter("cmnd");
        cmndR_fwd = request.getParameter("cmnd_fwd");
        String fwd = request.getParameter("fwd");
        String command_state = "";
        try{ int temp=0; temp=Integer.parseInt(cmndR_fwd); cmndR_fwd=temp+""; }catch(Exception e){cmndR_fwd="0";}

        String sqlReq = "INSERT INTO cmnds(cmnd,cntrl_dirt,srvtime,cntrl_te) VALUES ('" + cmnd + "',"+cmnd+",now(),"+cmndR_fwd+");";
        String cmndR_R = cmnd;
        String cmndR_L = cmnd;
        try {
            try {
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
                out.print("Exception insert:" + e);
            }

            sqlReq = "SELECT * FROM cmnds order by id desc limit 1;";
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://92.63.96.27:5432/gisdb";
                String username = "pgsql";
                String password = "pgsql";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sqlReq);
                while (rs.next()) {
                    cmndR = rs.getString(9);
                    for (int i=1;i<11;i++){
                        String temp="";
                        try{temp=rs.getString(i).trim();}catch(Exception e){};
                    command_state+="|"+temp;
                    }
                }
                rs.close();
                st.close();
                con.close();
                //String fwd="";

                int int_cmndR = 0;
                int_cmndR = Integer.parseInt(cmndR);


                int r = int_cmndR + 20;
                int l = int_cmndR - 20;
                cmndR_R = r + "";
                cmndR_L = l + "";


            } catch (Exception e) {
                out.print("Exception select:" + e);
            }



            //TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet cmnd</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet cmnd at " + request.getContextPath() + "</h1>");
            out.println("<hr/>command state:<br/>" + command_state + "<hr/>");
            out.println("<form action='/wm/cmnd'>");
            out.println("<input type=text name=cmnd value='" + cmndR + "'></input>");
            out.println("<br/><input type=text name=cmnd_fwd value='" + 0 + "'></input>");
            String fwd_s=" ";
            try{
            if (fwd.equals("on")){
                fwd_s="checked='checked'";
            }else{
                fwd_s="checked=''";
            }}catch(Exception e){}
            out.println("<input type=checkbox name=fwd "+fwd_s+" />");
            out.println("<input type=submit />");
            out.println("<hr/>");
            out.println("<a href='http://92.63.96.27:8180/wm/cmnd?cmnd=" + cmndR_L + "'> toLEFT  </a>");
            out.println("_____________________");
            out.println("<a href='http://92.63.96.27:8180/wm/cmnd?cmnd=" + cmndR_R + "'> toRIGHT </a>");
            out.println("<hr/>");
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
