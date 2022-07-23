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
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utilities.ConnectionHandler;
import it.polimi.tiw.project.utilities.MeetingForm;

/**
 * This servlet manages the attempts the user has to select the participants for
 * their meeting and creates it if the information is complete and correct.
 */
@WebServlet("/InviteToMeeting")
public class InviteToMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * Class constructor.
	 */
	public InviteToMeeting() {
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
	 * Gets the number and usernames of the users selected as participants for the meeting about to be
	 * created, checks the validity of the information and creates the meeting as soon as the information 
	 * is complete and correct (in accordance with the number of attempts the user has for making this choice).
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
		
		if(session.getAttribute("attempt")!=null && (int)session.getAttribute("attempt") <= 3) {
			String[] sUsernames = request.getParameterValues("id");				//get the usernames of the users selected as participants
			MeetingForm meetF = (MeetingForm)session.getAttribute("meetF");
			int maxPart = meetF.getMaxPart();
			
			if(sUsernames.length <= maxPart) {
				//information is correct (both for the meeting and the participants) -> the meeting can be created and people can be invited to it
				MeetingDAO mDAO = new MeetingDAO(connection);
				
				try {
					int key = mDAO.createMeeting(meetF.getTitle(),meetF.getDate(),meetF.getTime(),meetF.getDuration(),meetF.getMaxPart(),(String)session.getAttribute("user.username"));
					
					for(String s : sUsernames) {
						mDAO.sendInvitation(key, s);
					}
					
					session.removeAttribute("meetF");
					session.removeAttribute("attempt");
					
					String ctxpath = getServletContext().getContextPath();
					String path = ctxpath + "/GoToHomepage";
					response.sendRedirect(path);
				} catch(SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
					return;
				}
				
			} else if (sUsernames.length > maxPart && (int)session.getAttribute("attempt")==3) {
				//the user made three attempts and they always selected an invalid number of participants
				session.removeAttribute("meetF");
				session.removeAttribute("attempt");
				
				String path = "/WEB-INF/CancellationPage.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
				
			} else {
				//information is not correct but it's not the last attempt for the user
				int attempt = (int)session.getAttribute("attempt")+1;
				session.setAttribute("attempt", attempt);
				
				UserDAO uDAO = new UserDAO(connection);
				List<User> rUsers = new ArrayList<>();
				List<String> sUsers = new ArrayList<>();
				
				try {
					rUsers = uDAO.getRegisteredUsers();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				for(String s : sUsernames) {
					sUsers.add(s);
				}
				
				int toDeselect = sUsernames.length-maxPart;
				
				String path = "/WEB-INF/RecordsPage.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("rUsers", rUsers);
				ctx.setVariable("sUsers", sUsers);
				ctx.setVariable("toDeselect", toDeselect);
				ctx.setVariable("attempt", attempt);
				templateEngine.process(path, ctx, response.getWriter());
				
			}
			
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