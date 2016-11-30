package com.cc.guardian

import java.sql.Connection
import java.sql.DriverManager
import scala.collection.mutable.ArrayBuffer
import org.joda.time.DateTime
import java.sql.Timestamp
import java.sql.PreparedStatement;
object DBConnection {

	val url = "jdbc:mysql://localhost:3306/guardian"
			val pingSQL = "SELECT guardian_id FROM guardian_master ORDER BY currentDate LIMIT 1"
			val driver = "com.mysql.jdbc.Driver"
			val username = "root"
			val password = "root"
			var connection:Connection = _

			try {
				Class.forName(driver)
				connection = DriverManager.getConnection(url, username, password)
			} catch {
			case e: Exception => e.printStackTrace
			}

	def pingService (): String = {

			var latestId = ""
					val statement = connection.createStatement()
					try{

						val resultSet = statement.executeQuery(pingSQL)
								while(resultSet.next()){
									latestId = resultSet.getString("guardian_id")
								}

					}catch{
					case e: Exception  =>
					println("Exception while fetching latest guardian record")
					}

			return latestId
	}

	def insertArticle(guardianRecordBuffer: ArrayBuffer[GuardianRecord]): Boolean ={

			var status = true


					try{

						for(x <- guardianRecordBuffer){
							var insertSQL: String = "INSERT INTO GUARDIAN_MASTER  (type, sectionId, sectionName, publicationDate, title, webUrl, apiUrl, guardianId) VALUES " +
									"(?, ? , ? , ?, ?, ?, ?, ?)"

              var statement = connection.prepareStatement(insertSQL.toString())
              statement.setString(1, x.articleType)
              statement.setString(2, x.sectionId)
              statement.setString(3, x.sectionName)
              statement.setTimestamp(4, new Timestamp(x.pubDate.toDateTime().getMillis))
              statement.setString(5, x.title)
              statement.setString(6, x.webUrl)
              statement.setString(7, x.apiUrl)
              statement.setString(8, x.id)
              println(insertSQL)

              val resultSet = statement.executeUpdate()

						}
					}catch{
					case e: Exception =>
					println(e)
					status = false;
					}
			return status
	}
}