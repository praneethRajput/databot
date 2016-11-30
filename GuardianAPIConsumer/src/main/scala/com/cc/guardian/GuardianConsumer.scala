package com.cc.guardian

import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.DateTime
import com.gu.contentapi.client.GuardianContentClient
import com.gu.contentapi.client.model._
import scala.collection.mutable.ArrayBuffer
import java.util.Date

/**
 * @author Praneeth Rajput
 */

case class GuardianRecord(id: String, articleType: String, sectionId: String, sectionName: String, 
		pubDate: DateTime, title: String, webUrl: String, apiUrl: String)

		object GuardianConsumer extends App{

	val APIKey = "367b92dd-bcab-4f99-808f-ac863c06b560"
			val sectionId = "environment"
			val queryId = "climate-change"
			val fromDate = "2006-01-01"
			val orderBy = "newest"

			val client = new GuardianContentClient(APIKey)
	var counter: Int = 1


	val countQuery = SearchQuery().section(sectionId).q(queryId).
	orderBy(orderBy).fromDate(new DateTime(fromDate))
	client.getResponse(countQuery) map {
				countResponse =>

				println("Total Count of articles since " + fromDate + " is = " + countResponse.total)
				var pageSize = countResponse.pageSize
				var pageCount = countResponse.pages
				var latestId:String = DBConnection.pingService()

				while(counter <= pageCount){
					val searchQuery = SearchQuery().section(sectionId).q(queryId).
							orderBy(orderBy).fromDate(new DateTime(fromDate)).page(counter)
							client.getResponse(searchQuery) map {
						searchResponse =>
						println("Iterating results for Page Number " + counter)
						var guardianRecords = new ArrayBuffer[GuardianRecord]()
						var results = searchResponse.results
						var resultIterator = results.iterator
						while(resultIterator.hasNext){

							var result = resultIterator.next()

									if( latestId == null || !latestId.equalsIgnoreCase(result.id)){
										guardianRecords += GuardianRecord(result.id,result.`type`.name,result.sectionId.get,result.sectionName.get,
												new DateTime(result.webPublicationDate.get.dateTime),result.webTitle,result.webUrl,result.apiUrl)
									}else{
                       println("Update Completed Successfully")
                       System.exit(1)
									}
						}
						DBConnection.insertArticle(guardianRecords)
					}
          counter += 1
          Thread.sleep(1000);
				}

			}

}