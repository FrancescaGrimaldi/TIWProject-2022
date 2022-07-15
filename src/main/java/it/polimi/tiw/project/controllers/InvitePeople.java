package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.project.DAO.MeetingDAO;
import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utilities.MeetingForm;

@WebServlet("/InvitePeople")
public class InvitePeople extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	
	public InvitePeople() {
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
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user.username") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		if(session.getAttribute("attempt")!=null && (int)session.getAttribute("attempt") <= 3) {
			String[] sUsersIDs = request.getParameterValues("id");
			MeetingForm meetF = (MeetingForm)session.getAttribute("meetF");
			int maxPart = meetF.getMaxPart();
			
			if(sUsersIDs.length <= maxPart) {
				// arrived at this point we should already have the correct information and we can proceed creating the meeting and inviting people to it
				MeetingDAO mDAO = new MeetingDAO(connection);
				
				try {
					int key = mDAO.createMeeting(meetF.getTitle(),meetF.getDate(),meetF.getTime(),meetF.getDuration(),meetF.getMaxPart(),(String)session.getAttribute("user.username"));
					
					for(String s : sUsersIDs) {
						mDAO.sendInvitation(key, Integer.parseInt(s));
					}
					
					session.removeAttribute("meetF");
					session.removeAttribute("attempt");
					
					String ctxpath = getServletContext().getContextPath();
					String path = ctxpath + "/GoToHomepage";
					response.sendRedirect(path);
				} catch(SQLException e3) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
					return;
				}
				
			} else if (sUsersIDs.length > maxPart && (int)session.getAttribute("attempt")==3) {
				String ctxpath = getServletContext().getContextPath(); 
				String path = ctxpath + "/WEB-INF/CancellationPage.html";
				response.sendRedirect(path);
				
			} else {

				int attempt = (int)session.getAttribute("attempt");
				session.setAttribute("attempt", attempt+1);
				
				UserDAO uDAO = new UserDAO(connection);
				List<User> rUsers = new ArrayList<>();
				
				try {
					rUsers = uDAO.getRegisteredUsers();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				List<User> sUsers = new ArrayList<>();
				
				try {
					for(String s : sUsersIDs) {
						sUsers.add(uDAO.getRegisteredUserByID(Integer.parseInt(s)));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				int toDeselect = sUsersIDs.length-maxPart;
				
				String path = "/WEB-INF/RecordsPage.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("rUsers", rUsers);
				ctx.setVariable("sUsers", sUsers);
				ctx.setVariable("toDeselect", toDeselect);
				// ctx.setVariable("attempt", 1);
				templateEngine.process(path, ctx, response.getWriter());
				
			}
			
		}
		
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