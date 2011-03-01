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
public class wm_s extends HttpServlet {

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
            //out.println(cal.get(Calendar.YEAR) + " " + cal.get(Calendar.MONTH) + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.SECOND) + " ");



            //get params
            String lat = request.getParameter("lX");
            String lng = request.getParameter("lY");
            String v0 = request.getParameter("v0");
            String v1 = request.getParameter("v1");
            String v2 = request.getParameter("v2");
            String a0 = request.getParameter("a0");
            String a1 = request.getParameter("a1");
            String a2 = request.getParameter("a2");
            String ext  = request.getParameter("ext");
            //out.println(a1);
            /*
             *

            try {

            //prepare result(to need format)

            Class.forName("org.postgresql.Driver");
            String url="jdbc:postgresql://78.24.222.84:5432/ptgis";
            String username = "pgsql";
            String password = "pgsql";

            Connection con = DriverManager.getConnection(url,username,password);
            Statement st = con.createStatement();

            //exec sqlReq
            ResultSet rs = st.executeQuery(sqlReq);
            //rs.first();

            int j=0;
            String[] tmpStr = new String[10]; for (i=0;i<10;i++){tmpStr[i]="";}
            i=0;

            //maxCountOfRecords
            int maxRec = 16;


            while(rs.next()){
            if(i==0){
            respStr+="{\n";
            } else{
            respStr+=",{\n";
            }


            if(i<=maxRec){
            for (j=1;j<=rs.getMetaData().getColumnCount();j++){
            tmpStr[j]=rs.getString(j);
            //out.println(j+":"+tmpStr[j]+"<br>");
            }
            respStr+="\"id\":\""+tmpStr[1]+"\",\n";

            double x0 =- 21337.9761900000;
            double y0 = 7288.6904760000;
            double kwm = 0.337;

            //convert coords
            String oldCoord =tmpStr[3];
            String newCoord="";
            //dbg
            //out.println(tmpStr[3].replace(",","."));
            try{
            double oCrdX =Double.parseDouble(tmpStr[3].replace(",","."));
            double nCrdX = 0;
            double oCrdY =Double.parseDouble(tmpStr[4].replace(",","."));
            double nCrdY = 0;
            nCrdX = (oCrdX-x0)*kwm-300;
            nCrdY = (oCrdY-y0)*kwm-300;

            //mod coord
            //                        respStr+="\"wx\":\""+ Double.toString(nCrdX)  +"\",\n";
            //                        respStr+="\"wy\":\""+ Double.toString(nCrdY) +"\",\n";
            //native coord
            respStr+="\"wx\":\""+ Double.toString(oCrdX)  +"\",\n";
            respStr+="\"wy\":\""+ Double.toString(oCrdY) +"\",\n";
            }catch(Exception e){}
            respStr+="\"sdescr\":\""+ tmpStr[2].replace("\"","") +"\"\n";
            respStr+="}\n";
            //out.println(respStr+"<br>");
            } else {

            respStr+="\"id\":\""+tmpStr[1]+"\",\n";
            respStr+="\"wx\":\""+ -3000  +"\",\n";
            respStr+="\"wy\":\""+ -1000 +"\",\n";
            respStr+="\"sdescr\":\""+ " ...Рќ Р• Рџ Рћ Р› Рќ Р« Р™  Р  Р• Р— РЈ Р› Р¬ Рў Рђ Рў... " +"\"\n";
            respStr+="}\n";
            break;
            }
            i++;
            }
            rs.close();
            st.close();
            con.close();
            //OUTPUT!

            }catch(Exception e){out.println(e);}finally {

            }


             * http://192.168.0.102:8080/wm_s
            INSERT INTO timeline
            (srvtime, dvctime, orient1, orient2, orient3, "position",ext)
            VALUES
            (now(),   now(),   3.7,     3.5,     3.0,     GeomFromText('POINT(45.1 45.1)',32769),'111');

             */
            String sqlReq = " INSERT INTO timeline "
                    + "             (srvtime, dvctime, orient1, orient2, orient3, \"position\",ext)"
                    + " VALUES "
                    + " (now(),   now(),   " + v0+ ",     " + v1+ ",     " + v2+ ",     GeomFromText('POINT(" + lng + " " + lat + ")',32769),'" + ext + "'); ";
            try {
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://92.63.96.27:5432/gisdb";
                String username = "pgsql";
                String password = "pgsql";
                Connection con = DriverManager.getConnection(url, username, password);
                Statement st = con.createStatement();
                st.executeUpdate(sqlReq);
                /*
                while (rs.next()) {}
                rs.close();
                 */
                st.close();
                con.close();

            } catch (Exception e) {
                out.print("Exception!:"+e);
            }

                       sqlReq = "SELECT cmnd FROM cmnds order by id desc limit 1;";
                               String cmndR="";

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

            out.println(cmndR);

            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet wm_s</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet wm_s at " + request.getContextPath () + "</h1>");
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
