package cc.NYT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class App {

	public static void main(String[] args) {

		JSONArray totalArticles = new JSONArray();
		
		JSONObject result = new JSONObject();
		HttpEntity entity = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			int perCallCount = 10;
			int pageNumber = 0;
			int callCount = 0;
			
			while (perCallCount == 10 && pageNumber < 100) {
				String callString = "http://api.nytimes.com/svc/search/v2/articlesearch.json?"
						+ "q=Climate+Change&begin_date=20150301&end_date=20150430&page=" + pageNumber
						+ "9&fields=geo_facet&api-key=a2d0fec8cd7b4490b884c3bbfa53a26b";
				HttpGet httpGetRequest = new HttpGet(callString);
				HttpResponse httpResponse = httpClient.execute(httpGetRequest);
				System.out.println("----------------------------------------");
				System.out.println(httpResponse.getStatusLine());
				System.out.println("----------------------------------------");

				// Get hold of the response entity
				entity = httpResponse.getEntity();

				result = new JSONObject(EntityUtils.toString(entity));
				JSONObject response = result.getJSONObject("response");
				JSONArray articles = (JSONArray) response.get("docs");

				pageNumber++;
				callCount++;
				perCallCount = articles.length();
				for (int i = 0; i < articles.length(); i++) {
					totalArticles.put(articles.get(i));
				}

				System.out.println("Call made for page number : " + pageNumber);
				System.out.println("Total Articles till now : " + totalArticles.length());
				System.out.println("Total calls till now : " + callCount);
				TimeUnit.MILLISECONDS.sleep(1200);
			}

			List<NYTArticle> articleObjs = getArticleObjs(totalArticles);
			writeArticlesToDatabase(articleObjs);
			System.out.println("Number of articles is " + totalArticles.length());
		} catch (Exception e) {
			e.printStackTrace();
			List<NYTArticle> articleObjs = getArticleObjs(totalArticles);
			writeArticlesToDatabase(articleObjs);
		}
	}

	private static void writeArticlesToDatabase(List<NYTArticle> articleObjs) {
	 Boolean status = 	DBUtils.insertRecords(articleObjs);		

	}

	private static List<NYTArticle> getArticleObjs(JSONArray totalArticles) {
		List<NYTArticle> objs = new ArrayList<NYTArticle>();
		try {
			for (int i = 0; i < totalArticles.length(); i++) {

				JSONObject article = totalArticles.getJSONObject(i);
				NYTArticle nytArticle = new NYTArticle();

				if (article.has("web_url") && (article.getString("web_url") != null)) {
					nytArticle.setUrl(article.getString("web_url"));
				} else {
					nytArticle.setUrl("");
				}

				if (article.has("snippet") && (article.getString("snippet") != null)) {
					nytArticle.setSnippet(article.getString("snippet"));
				} else {
					nytArticle.setSnippet("");
				}

				if (article.has("abstract") && (article.get("abstract") != JSONObject.NULL)) {
					nytArticle.setAbs(article.getString("abstract"));
				} else {
					nytArticle.setAbs("");
				}

				if (article.has("pub_date") && (article.getString("pub_date") != null)) {
					nytArticle.setDate(article.getString("pub_date"));
				} else {
					nytArticle.setDate("");
				}

				if (article.has("lead_paragraph") && (article.getString("lead_paragraph") != null)) {
					nytArticle.setLeadpara(article.getString("lead_paragraph"));
				} else {
					nytArticle.setLeadpara("");
				}

				if (article.has("_id") && (article.getString("_id") != null)) {
					nytArticle.setId(article.getString("_id"));
				} else {
					nytArticle.setId("");
				}

				if (article.has("headline") && (article.getJSONObject("headline") != null)
						&& article.getJSONObject("headline").has("main")
						&& (article.getJSONObject("headline").getString("main") != null)) {
					nytArticle.setHeadline(article.getJSONObject("headline").getString("main"));
				} else {
					nytArticle.setHeadline("");
				}

				objs.add(nytArticle);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return objs;
	}

}

class NYTArticle {

	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLeadpara() {
		return leadpara;
	}

	public void setLeadpara(String leadpara) {
		this.leadpara = leadpara;
	}

	public String getAbs() {
		return abs;
	}

	public void setAbs(String abs) {
		this.abs = abs;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = null;
	}

	String url;
	String leadpara;
	String abs;
	String headline;
	String snippet;
	String date;
}

