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

/**
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
            String doc = "<?xml version=\"1.0\" encoding=\"utf-8\"?> "
                    + "\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" ["
                    + "\n   <!ENTITY ns_imrep \"http://ns.adobe.com/ImageReplacement/1.0/\">"
                    + "\n   <!ENTITY ns_svg \"http://www.w3.org/2000/svg\">"
                    + "\n   <!ENTITY ns_xlink \"http://www.w3.org/1999/xlink\">"
                    + "\n ]>"
                    + "\n <svg xmlns=\"&ns_svg;\" xmlns:xlink=\"&ns_xlink;\" width=\"132.72\" height=\"127.219\""
                    + " viewBox=\"0 0 700 700\" overflow=\"visible\" "
                    + "enable-background=\"new 0 0 300 300\" xml:space=\"preserve\"> "
                    + "\n "
                    + "\n<rect x=\"100\" y=\"0\" width=\"400\" height=\"400\" rx = \"10\" ry=\"5\" "
                    + "\n fill= \"none\" stroke=\"blue\" stroke-width=\"3\" > </rect>"
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
