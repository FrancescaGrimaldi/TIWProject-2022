package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utilities.ConnectionHandler;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * This servlet controls the login.
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	/**
	 * Class constructor.
	 */
	public CheckLogin() {
		super();
	}

	
	/**
	 * Initializes the connection to the database.
	 */
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		
		connection = ConnectionHandler.getConnection(servletContext);
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext); //we are going to retrieve our template files as resources from the servlet context
		templateResolver.setTemplateMode(TemplateMode.HTML);												  //set even though HTML is the default mode
		templateResolver.setSuffix(".html");																  //modifies the template names that we will be passing to the engine for obtaining the real resource names to be used
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
	}
	
	
	/**
	 * Checks the validity of the inserted username and password and authenticates
	 * the user (querying the db), redirecting to the homepage or displaying the
	 * errors (if any occurs).
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String usrn = null;
		String pwd = null;
		
		try {
			usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pwd = StringEscapeUtils.escapeJava(request.getParameter("password"));
			
			if (isInvalid(usrn) || isInvalid(pwd)) {
				throw new Exception("Missing or empty credential value");
			}

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}

		//authenticate for user
		UserDAO uDAO = new UserDAO(connection);
		User u = null;
		try {
			u = uDAO.checkCredentials(usrn, pwd);		//returns User u || null
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible to check credentials");
			return;
		}

		String path;
		if (u == null) {
			//if the user does not exist, show error message
			path = "/index.html";
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect username or password");
			templateEngine.process(path, ctx, response.getWriter());
			
		} else {
			//if the user exists, add info to the session and go to home page
			request.getSession().setAttribute("user", u);
			request.getSession().setAttribute("user.username", u.getUsername());
			
			path = getServletContext().getContextPath() + "/GoToHomepage";
			response.sendRedirect(path);
		}

	}
	
	
	/**
	 * Checks whether the given string is null or empty.
	 * Used in {@link #doPost(HttpServletRequest, HttpServletResponse) doPost}
	 * to check if the credentials are valid.
	 * @param str		the String to check.
	 * @return			a boolean whose value is:
	 * 					<p>
	 * 					-{@code true} if it's incorrect;
	 * 					</p> <p>
	 * 					-{@code false} otherwise.
	 * 					</p>
	 */
	private boolean isInvalid(String str) {
		return ( str==null || str.isEmpty() );
	}
	
	
	/**
	 * Closes the connection to the database.
	 */
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
}