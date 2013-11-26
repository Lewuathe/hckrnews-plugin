package com.lewuathe.plugins.hckrnews;

import static java.util.regex.Pattern.compile;
import hudson.Extension;
import hudson.model.PeriodicWork;
import hudson.model.Hudson;
import hudson.widgets.Widget;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.*;

@Extension
public class HckrnewsWidget extends Widget {

    private Date lastUpdated;

	private List<News> newsList;

	public List<News> getNewslist() {
		System.out.println("+++++++++++++++++++ getNewsList  +++++++++++++++++");
		System.out.println(this.newsList.size());
		return this.newsList;
	}

	public void setNewslist(List<News> newsList) {
		this.newsList = newsList;
	}

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String formatNumber(int value) {
        return DecimalFormat.getNumberInstance().format(value);
    }

	public static class News {
		private String title;
		private String url;
		private String points;
		private String postedBy;

		public News(String title, String url, String points, String postedBy) {
			this.title     = title;
			this.url       = url;
			this.points    = points;
			this.postedBy  = postedBy;
		}

		public News() {
			this.title    = "No title";
			this.url      = "No url";
			this.points   = "No points";
			this.postedBy = "Nobody";
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setPoints(String points) {
			this.points = points;
		}

		public void setPostedBy(String postedBy) {
			this.postedBy = postedBy;
		}

		public String getTitle() { return this.title; }
		public String getUrl() { return this.url; }
		public String getPoints() { return this.points; }
		public String getPostedBy() { return this.postedBy; }
	}

	@Extension 
	public static class NewsDownloader extends PeriodicWork {
		private static final String API_URL = "http://api.ihackernews.com/page";


		@Override
		public long getRecurrencePeriod() {
			return 10 * 60 * 1000;
		}

		@Override public long getInitialDelay() {
			return 0;
		}

		@Override
		protected void doRun() throws Exception {
			List<News> newsList = new ArrayList<News>();
			String json = loadNews();

			System.out.println(json);
			JsonFactory factory = new JsonFactory();
			JsonParser parser = factory.createJsonParser(json);

			while (parser.nextToken() != JsonToken.END_OBJECT) {
				String name = parser.getCurrentName();
				if (name != null) {
					if (name.equals("items")) {
						while (parser.nextToken() != JsonToken.END_ARRAY) {
							name = parser.getCurrentName();
							if (name != null) {
								News news = new News();
								while (parser.nextToken() != JsonToken.END_OBJECT) {
									name = parser.getCurrentName();
									if (name != null) {
										if(name.equals("title")) {
											// title
											news.setTitle(parser.getText());
										} else if(name.equals("url")) {
											// url
											news.setUrl(parser.getText());
										} else if(name.equals("points")) {
											// points
											news.setPoints(parser.getText());
										} else if(name.equals("postedBy")) {
											// postedBy
											news.setPostedBy(parser.getText());
										} else {
											//想定外のものは無視して次へ
											parser.skipChildren();
										}
									}
								}
								System.out.println("Added: " + news.getTitle() + ", " + news.getPoints());
								newsList.add(news);
							}
						}
					}
				}
			}
			System.out.println("-------------- " + newsList.size() + " ---------------------");
			for (Widget w : Hudson.getInstance().getWidgets()) {
				if (w instanceof HckrnewsWidget) {
					HckrnewsWidget hw = (HckrnewsWidget) w;
					hw.setNewslist(newsList);
				}
            }
		}
			
        protected String loadNews() {
			InputStream is = null;
            try {
                is = new URL(API_URL).openStream();
                return IOUtils.toString(is, "Shift_JIS");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

	}
}
