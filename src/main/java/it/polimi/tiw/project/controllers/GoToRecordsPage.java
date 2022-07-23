package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utilities.ConnectionHandler;
import it.polimi.tiw.project.utilities.MeetingForm;

/**
 * This servlet manages the meeting form validation and
 * the first access to the records page.
 */
@WebServlet("/GoToRecordsPage")
public class GoToRecordsPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * Class constructor.
	 */
	public GoToRecordsPage() {
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
	 * Checks the validity of the meeting form. If valid, redirects to the records page;
	 * displays the errors otherwise.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		//redirect to the login if the user is not logged in
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		//create and initialize the form bean with user's input
		String title = request.getParameter("title");
		String startDate = request.getParameter("date");
		String startTime  = request.getParameter("time");
		Integer duration = 0;
		Integer maxPart = 0;
		
		try {
			duration = Integer.parseInt(request.getParameter("duration"));
			maxPart = Integer.parseInt(request.getParameter("maxPart"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request was syntactically incorrect");
			return;
		}
		
		MeetingForm meetF = new MeetingForm(title, startDate, startTime, duration, maxPart);
		
		if (meetF.isValid()) {
			//redirect to the RecordsPage.html to select participants
			session.setAttribute("attempt", 1);
			session.setAttribute("meetF", meetF);
			
			UserDAO uDAO = new UserDAO(connection);
			List<User> rUsers = new ArrayList<>();
			
			try {
				rUsers = uDAO.getRegisteredUsers();
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
				return;
			}
			
			//redirect adding registered users to the parameters
			String path = "/WEB-INF/RecordsPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			ctx.setVariable("rUsers", rUsers);
			ctx.setVariable("attempt", 1);
			
			templateEngine.process(path, ctx, response.getWriter());
			
		} else {
			//redirect to homepage and display the format errors
			String path = "/GoToHomepage";
			
			request.setAttribute("errors", meetF.getErrors());
			
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
		}

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
