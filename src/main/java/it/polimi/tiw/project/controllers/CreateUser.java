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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.utilities.ConnectionHandler;
import it.polimi.tiw.project.utilities.UserForm;

/**
 * This servlet controls the registration of a user.
 */
@WebServlet("/CreateUser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	/**
	 * Class constructor.
	 */
	public CreateUser() {
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
	 * Gets all the data from the SignUp.html form and creates a user with that
	 * information; displays errors in case of invalid input.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//create and initialize the form bean with user's input
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String city = request.getParameter("city");
		Integer age = 0;
		
		try {
			age = Integer.parseInt(request.getParameter("age"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request was syntactically incorrect");
			return;
		}
		
		UserForm userF = new UserForm(email, username, password, password2, name, surname, age, city);
		
		if (userF.isValid()) {
			if (checkUsername(username)) {
				try {
					//the form is valid and the username is available
					UserDAO uDAO = new UserDAO(connection);
					uDAO.createUser(email, username, name, surname, password, age, city);
					String path = getServletContext().getContextPath() + "/index.html";
					response.sendRedirect(path);
					return;
				} catch(SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
					return;
				}
			} else {
				//the form is valid but the username is already taken
				String path = "/SignUp.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("errorMsg", "The username you chose is already taken");
				templateEngine.process(path, ctx, response.getWriter());
			}
			
		} else {
			//display the format errors
			String path = "/SignUp.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errors", userF.getErrors());
			templateEngine.process(path, ctx, response.getWriter());
		}

	}
	
	
	/**
	 * States whether the chosen username is available or not.
	 * @param username	the username to check.
	 * @return			a boolean whose value is:
	 * 					<p>
	 * 					-{@code true} if it's available;
	 * 					</p> <p>
	 * 					-{@code false} otherwise.
	 * 					</p>
	 */
	private boolean checkUsername(String username) {
		boolean result = false;
		
		try {
			UserDAO uDAO = new UserDAO(connection);
			result = uDAO.checkAvailability(username);
		} catch(SQLException ignored) {}
		
		return result;
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
