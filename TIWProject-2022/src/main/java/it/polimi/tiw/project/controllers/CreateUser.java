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
import it.polimi.tiw.project.beansform.UserForm;
import it.polimi.tiw.project.DAO.*;
import it.polimi.tiw.project.utils.ConnectionHandler;

@WebServlet("/CreateUser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	
	public CreateUser() {
		super();
	}

	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
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
		
		UserForm userF = new UserForm(username, email, password, password2, name, surname, age, city);
		
		//the first half is done
		if (userF.isValid()) {
			if (this.checkUsername(username)) {
				try {
					UserDAO uDAO = new UserDAO(connection);
					uDAO.createUser(email, username, name, surname, password, age, city);
					String path = "index.html";
					response.sendRedirect(path);
				} catch(SQLException e3) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
					return;
				}
			} else {
				//TODO:show that the error is that the username already exists
			}
			
		} else {
			//we should display the format errors
			String path = "SignUp.html";
			request.setAttribute("userForm", userF);
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
			return;
		}

	}
	
	
	public boolean checkUsername(String username) {
		boolean result = false;
		
		try {
			UserDAO uDAO = new UserDAO(connection);
			result = uDAO.checkExistence(username);
		} catch(SQLException e3) {
			e3.printStackTrace();
		}
		
		return result;
	}

	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
