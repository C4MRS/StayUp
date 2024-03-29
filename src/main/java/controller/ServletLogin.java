package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.utils.PasswordEncryptionUtil;
import model.user.UserBean;
import model.user.UserBeanFacade;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class ServletLogin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ID = request.getParameter("emailLog");
        String password = request.getParameter("passwordLog");
        try {
            if(!ID.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,})+$"))
                throw new Exception("Email format is not respected");
            if(ID.length()<6 || ID.length()>40)
                throw new Exception("Email length not respected");
            if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,24}$"))
                throw new Exception("Password format is not respected");
            password = PasswordEncryptionUtil.encryptPassword(password);
            UserBeanFacade usDAO = new UserBeanFacade();
            UserBean us=usDAO.loginUser(ID,password);
            if(!us.getEmail().equals("ERRORE")){
                HttpSession sess=request.getSession(true);
                sess.setAttribute("email",ID);
                sess.setAttribute("name",us.getNome());
                sess.setAttribute("surname",us.getCognome());
                sess.setAttribute("role",us.getRole());
                String sessID=sess.getId();
                Cookie sessionIdCk=new Cookie("JSESSIONID",sessID);
                sessionIdCk.setMaxAge(60*60*24);
                response.addCookie(sessionIdCk);
                if(us.getRole().equalsIgnoreCase("admin")){
                    request.setAttribute("success","./admin_home.jsp");
                    request.getRequestDispatcher("./infopages/success.jsp").forward(request,response);
                }else{
                    request.setAttribute("success","./index.jsp");
                    request.getRequestDispatcher("./infopages/success.jsp").forward(request,response);
                }
            }else{
                throw new Exception("Utente non esistente");
            }
        }catch(Exception e){
            request.setAttribute("exception",e);
            request.setAttribute("exceptionURL","./login.jsp");
            request.getRequestDispatcher("./infopages/error.jsp").forward(request,response);
        }
    }
}
