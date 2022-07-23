package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import it.polimi.tiw.project.DAO.MeetingDAO;
import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.beans.Meeting;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utilities.ConnectionHandler;

/**
 * This servlet handles the access to the homepage.
 */
@WebServlet("/GoToHomepage")
public class GoToHomepage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * Class constructor.
	 */
	public GoToHomepage() {
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
	 * Gets created and invited meetings to display to the specific user.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		//redirect to the login if the user is not logged in
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		User u = (User)session.getAttribute("user");
		MeetingDAO mDAO = new MeetingDAO(connection);
		UserDAO uDAO = new UserDAO(connection);
		
		List<Meeting> cMeetings = new ArrayList<>();
		List<Meeting> iMeetings = new ArrayList<>();
		
		try {
			cMeetings = mDAO.findCreatedMeetings(u);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover created meetings");
			return;
		}
		
		try {
			iMeetings = mDAO.findInvitedMeetings(u);
			for (Meeting m : iMeetings) {
				m.setCreatorUsername(uDAO.getNickByID(m.getCreatorID()));
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover invited meetings");
			return;
		}

		//redirect to the homepage adding the meetings to the parameters
		String path = "/WEB-INF/Homepage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("CreatedMeetings", cMeetings);
		ctx.setVariable("InvitedMeetings", iMeetings);
		
		//checks if there are also errors to display
		String errors = (String)request.getAttribute("errors");
		if(errors!=null && !errors.equals(" ")) {
			ctx.setVariable("errors", errors);
		}
		
		templateEngine.process(path, ctx, response.getWriter());
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
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
}
