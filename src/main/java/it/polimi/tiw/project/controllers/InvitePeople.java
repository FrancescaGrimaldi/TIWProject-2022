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
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.DAO.MeetingDAO;
import it.polimi.tiw.project.beansform.MeetingForm;

//****** INCOMPLETE ******
@WebServlet("/InvitePeople")
public class InvitePeople extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
		
		// arrived at this point we should already have the correct information and we can proceed creating the meeting and inviting people to it
		try {
			MeetingDAO mDAO = new MeetingDAO(connection);
			// all the meetF. needs to be changed with some request.getAttribute...
			int key = mDAO.createMeeting(meetF.getTitle(),meetF.getDate(),meetF.getTime(),meetF.getDuration(),meetF.getMaxPart(),(String)session.getAttribute("user.username"));
				
			//and then also update the Participation table
			//something like
			//forall u in usersToBeInvited -> mDAO.sendInvitation(''KEY'', u)
				
			//somehow refresh page
			//maybe like this
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/WEB-INF/Homepage.html";
			response.sendRedirect(path);
		} catch(SQLException e3) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
			return;
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