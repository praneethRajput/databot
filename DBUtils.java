package cc.NYT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class DBUtils {


	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/guardian";


	static final String USER = "root";
	static final String PASS = "root";

	private static Connection getDBConnection(){

		Connection conn = null;
		try{

			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

		}catch(Exception e){
			e.printStackTrace();
		}

		return conn;
	}

	public static Boolean insertRecords(List<NYTArticle> articleObjs){

		System.out.println("Starting to insert records");
		Boolean insertOperationDone = false;

		try{

			Connection connection = getDBConnection();

			for(NYTArticle article: articleObjs){

				String insertTableSQL = "INSERT INTO nyt_articles"
						+ "(id, url, leadpara, abstract, headline, date, snippet) VALUES"
						+ "(?,?,?,?,?,?,?)";
				PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
				preparedStatement.setString(1, article.getId());
				preparedStatement.setString(2, article.getUrl());
				preparedStatement.setString(3, article.getLeadpara());
				preparedStatement.setString(4, article.getAbs());
				preparedStatement.setString(5, article.getHeadline());
				preparedStatement.setString(6, article.getDate());
				preparedStatement.setString(7,article.getSnippet());
				preparedStatement.executeUpdate();

				System.out.println("Inserted DB record Successfully");
			}

			insertOperationDone = true;
			connection.close();

		}catch(Exception e){
			System.out.println("Exceptions while storing the database records" +  e);
		}





		return insertOperationDone;
	}
}