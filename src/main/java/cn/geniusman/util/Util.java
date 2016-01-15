package cn.geniusman.util;

import java.io.IOException;
import static cn.geniusman.constant.Constant.HTTP_POST_TIMEOUT;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import cn.geniusman.exception.CommunicationException;

public final class Util {

	/**
	 * post the url target
	 * 
	 * @param url
	 * @param body
	 * @return
	 * @throws IOException
	 */
	public static String post(String url, String body) throws IOException {
		String responseBody = null;
		HttpClientParams hcp = new HttpClientParams();
		hcp.setSoTimeout(HTTP_POST_TIMEOUT);
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
				int retCode = client.executeMethod(post);
				if (retCode != HttpStatus.SC_OK) {
					throw new CommunicationException("post the url:" + url
							+ ", and ret code is:" + retCode);
				} else {
					responseBody = post.getResponseBodyAsString();
				}
			}
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		return responseBody;
	}

}
