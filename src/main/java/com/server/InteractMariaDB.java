package com.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class InteractMariaDB implements InteractDB{

	private static InteractMariaDB instance = null;
	Connection connection = null;
	String query = "SELECT text_info FROM\n" +
            "information INNER JOIN information_topics \n" +
            "ON information.information_id = information_topics.information_id\n" +
            "INNER JOIN persons_topics\n" +
            "ON  information_topics.topic_id = persons_topics.topic_id\n" +
            "WHERE  persons_topics.person_id IN\n" +
            "(SELECT DISTINCT persons_devices.person_id \n" +
            "from devices \n" +
            "INNER JOIN persons_devices \n" +
            "ON persons_devices.device_id = devices.device_id\n" +
            "WHERE devices.address = ?) \n" +
            "AND\n" +
            "(information.time_frame_end >= NOW() \n" +
            "OR information.time_frame_end IS NULL);";
	
	
	private InteractMariaDB() {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			String url = "jdbc:mariadb://127.0.0.1/information_db";
            String user = "root";
            String pass = "dayoneday";
            connection = DriverManager.getConnection(url, user, pass);
            
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public static InteractMariaDB getInstance() {
		if (instance == null) {
			instance = new InteractMariaDB();
		}
		return instance;
	}


	@Override
	public void getInfoFomDB(String address){
		PreparedStatement preparedStatement = null;
		try {
			System.out.println("Ssearch in database: " + address);
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, address);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        while (resultSet.next()) {
	        	System.out.println(resultSet.getString("text_info"));
	        }
		} catch (SQLException e) {
			if (connection != null) {
	            try {
	            	e.printStackTrace();
	                System.err.print("Transaction is being rolled back");
	                connection.rollback();
	            } catch(SQLException excep) {
	            	excep.printStackTrace();
	            }
			}
		} finally {
			if(preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }


}
