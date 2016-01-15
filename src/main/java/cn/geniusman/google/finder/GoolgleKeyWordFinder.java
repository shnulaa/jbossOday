package cn.geniusman.google.finder;

import static cn.geniusman.constant.Constant.HTTP_SOCKET_TIMEOUT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geniusman.constant.Constant;
import cn.geniusman.threadpool.ThreadPool;

public class GoolgleKeyWordFinder implements Callable<Map<String, URL>> {

	private static final Logger log = LoggerFactory
			.getLogger(GoolgleKeyWordFinder.class);

	private final String searchUrl;

	/**
	 * 
	 * @param results
	 * @param keyword
	 */
	public GoolgleKeyWordFinder(final String searchUrl) {
		this.searchUrl = searchUrl;
	}

	public Map<String, URL> call() {

		try {
			final String response = getResponseString();
			if (!response.isEmpty()) {
				return parseResponse(response);
			}
			return new HashMap<String, URL>();
		} catch (Exception ex) {
			log.error("Exception occurred, thread id:"
					+ Thread.currentThread().getId(), ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * get Response String
	 * 
	 * @return the search result response body
	 */
	private final String getResponseString() {
		HttpClientParams hcp = new HttpClientParams();
		hcp.setSoTimeout(HTTP_SOCKET_TIMEOUT);
		final HttpClient client = new HttpClient(hcp);
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(HTTP_SOCKET_TIMEOUT);

		final StringBuilder body = new StringBuilder();
		GetMethod gm = null;
		BufferedReader br = null;
		try {
			gm = new GetMethod(searchUrl);

			gm.setRequestHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			gm.setRequestHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
			gm.setRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
			gm.setRequestHeader("Accept-Language",
					"ja,en-US;q=0.8,en;q=0.6,zh;q=0.4,zh-CN;q=0.2");
			gm.setRequestHeader("Connection", "keep-alive");
			gm.setRequestHeader("Host", "www.google.com.hk");
			gm.setRequestHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.97 Safari/537.22");
			gm.setRequestHeader("X-Chrome-Variations",
					"CPy1yQEIhLbJAQiYtskBCKa2yQEIp7bJAQiptskBCK+2yQEI7oPKAQ==");

			log.info("ready to get from google, search url: {}", searchUrl);
			client.executeMethod(gm);
			log.info("finish to get from google, search url: {}", searchUrl);

			final int statusCode = gm.getStatusCode();
			log.info(String.valueOf(statusCode));

			if (statusCode != HttpStatus.SC_OK) {
				return body.toString();
			}

			br = new BufferedReader(new InputStreamReader(
					gm.getResponseBodyAsStream()));

			String line;
			while ((line = br.readLine()) != null) {
				body.append(line);
			}
		} catch (HttpException e) {
			log.error("HttpException occurred..", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error("IOException occurred..", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}

			if (gm != null) {
				gm.releaseConnection();
			}
		}
		return body.toString();
	}

	/**
	 * parse the response
	 * 
	 * @param response
	 * @throws URISyntaxException
	 */
	private Map<String, URL> parseResponse(final String response)
			throws URISyntaxException {
		final CleanerProperties cp = new CleanerProperties();
		cp.setUseCdataForScriptAndStyle(true);
		cp.setRecognizeUnicodeChars(true);
		cp.setUseEmptyElementTags(true);
		cp.setAdvancedXmlEscape(true);

		final HtmlCleaner hc = new HtmlCleaner(cp);
		final TagNode node = hc.clean(response);

		System.out.println("parse the data from response..");
		Map<String, URL> ret = new HashMap<String, URL>();
		addAllTagNode(node, ret);
		return ret;
	}

	/**
	 * printTagNode
	 * 
	 * @param node
	 */
	private void addTagNode(final TagNode node, final Map<String, URL> results) {
		String url = node.getText().toString();
		final String protocol = "http://";

		if (url.indexOf(protocol) <= 0) {
			url = protocol + url;
		}

		URL netUrl = null;
		try {
			netUrl = new URL(url);
			final String host = netUrl.getHost();
			if (!results.containsKey(host)) {
				results.put(host, netUrl);
			}
		} catch (MalformedURLException ex) {
			System.out.println(netUrl == null ? ""
					: (netUrl.toString() + " error"));
		}
	}

	/**
	 * 
	 * @param node
	 * @throws URISyntaxException
	 */
	private void addAllTagNode(final TagNode node,
			final Map<String, URL> results) throws URISyntaxException {
		final TagNode[] tagArray = node.getAllElements(true);
		final TagNode n = node.findElementByName("cite", false);
		if (n != null) {
			addTagNode(n, results);
		}

		for (final TagNode o : tagArray) {
			addAllTagNode(o, results);
		}
	}

	public static void main(String[] args) throws Throwable {

		final Map<String, URL> result = new HashMap<String, URL>();
		ThreadPool threadPoll = null;
		try {
			threadPoll = ThreadPool.getInstance();

			List<Future<Map<String, URL>>> futureList = new ArrayList<Future<Map<String, URL>>>();
			for (int i = 0; i < 1; i++) {
				final Callable<Map<String, URL>> r = new GoolgleKeyWordFinder(
						Constant.SEARCH_URL + "&start=" + 100 * i);
				futureList.add(threadPoll.submitTask(r));
			}

			for (final Future<Map<String, URL>> f : futureList) {
				result.putAll(f.get());
			}

			for (Entry<String, URL> entry : result.entrySet()) {
				log.info(entry.getKey());
			}

		} finally {
			if (threadPoll != null) {
				threadPoll.shutdown();
			}
		}
	}

}
