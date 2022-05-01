package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.beansform.MeetingForm;
import it.polimi.tiw.project.DAO.*;
import it.polimi.tiw.project.utils.ConnectionHandler;

@WebServlet("/CreateMeeting")
public class CreateMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	
	public CreateMeeting() {
		super();
	}

	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
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

		//create and initialize the form bean with user's input
		String title = request.getParameter("title");
		String startDate = request.getParameter("date");
		String startTime  = request.getParameter("time");
		Integer duration = Integer.parseInt(request.getParameter("duration"));
		Integer maxPart = Integer.parseInt(request.getParameter("maxPart"));
		
		MeetingForm meetF = new MeetingForm(title,startDate,startTime,duration,maxPart);
		
		//the first half is done
		if (meetF.isValid()) {
			//redirect to the records page to select participants
			//save the list of participantsID
			
			//somehow get back here
			try {
				MeetingDAO mDAO = new MeetingDAO(connection);
				int key = mDAO.createMeeting(meetF.getTitle(),meetF.getDate(),meetF.getTime(),meetF.getDuration(),meetF.getMaxPart(),(String)session.getAttribute("user.username"));
				
				//and then also update the Participation table
				//something like
				//mDAO.sendInvitation(list of participants, ''KEY'')
				
				//somehow refresh page
				//maybe like this
				String ctxpath = getServletContext().getContextPath();
				String path = ctxpath + "/Homepage";
				response.sendRedirect(path);
			} catch(SQLException e3) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
				return;
			}
		} else {
			//we should display the errors
			String path = "/WEB-INF/createmessage.jsp";
			request.setAttribute("meetingForm", meetF);
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
			return;
		}

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
