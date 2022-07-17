package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.utilities.UserForm;

@WebServlet("/CreateUser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	
	public CreateUser() {
		super();
	}

	
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			
			ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
			templateResolver.setTemplateMode(TemplateMode.HTML);
			this.templateEngine = new TemplateEngine();
			this.templateEngine.setTemplateResolver(templateResolver);
			templateResolver.setSuffix(".html");

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//create and initialize the form bean with user's input
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		Integer age = Integer.parseInt(request.getParameter("age"));
		String city = request.getParameter("city");
		
		UserForm userF = new UserForm(email, username, password, password2, name, surname, age, city);
		
		//the first half is done
		if (userF.isValid()) {
			if (this.checkUsername(username)) {
				try {
					UserDAO uDAO = new UserDAO(connection);
					uDAO.createUser(email, username, name, surname, password, age, city);
					String path = getServletContext().getContextPath() + "/index.html";
					response.sendRedirect(path);
					return;
				} catch(SQLException e3) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
					return;
				}
			} else {
				//the form is correct but the username is already taken
				
				/*
				String path = "/SignUp.html";
				request.setAttribute("userForm", userF);
				request.setAttribute("errorMsg", "The username you chose is already taken");
				RequestDispatcher dispatcher = request.getRequestDispatcher(path);
				dispatcher.forward(request, response);
				return;
				*/
				
				String path = "/SignUp.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("errorMsg", "The username you chose is already taken");
				templateEngine.process(path, ctx, response.getWriter());
			}
			
		} else {
			//we should display the format errors
			String path = "/SignUp.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errors", userF.getErrors());
			System.out.println("\nERRORS: "+userF.getErrors());
			templateEngine.process(path, ctx, response.getWriter());
		}

	}
	
	
	public boolean checkUsername(String username) {
		boolean result = false;
		
		try {
			UserDAO uDAO = new UserDAO(connection);
			result = uDAO.checkAvailability(username);
		} catch(SQLException e3) {
			e3.printStackTrace();
		}
		
		return result;
	}

	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
	
}
