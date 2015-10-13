package home.greg;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by greg on 09.10.15.
 */
@WebServlet("/api")
public class MainServlet extends HttpServlet {

    /*
     * method that handles GET request
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Document document = new Document();
        String q = req.getParameter("q");
        if (q == null) {
            q = "";
        }
        int length;
        if (req.getParameter("length") == null) {
            length = 0;
        } else {
            length = Integer.parseInt(req.getParameter("length"));
        }
        int limit;
        if (req.getParameter("limit") == null) {
            limit = 0;
        } else {
            limit = Integer.parseInt(req.getParameter("limit"));
        }

        boolean meta;
        String includeMetaData = req.getParameter("includeMetaData");
        if (includeMetaData == null) {
            meta = false;
        } else {
            meta = new Boolean(includeMetaData);
        }
        document = getDocument(q, length, limit, meta);
        PrintWriter out = resp.getWriter();
        out.println('{');
        out.println("\"text\":[");
        int counter = 0;
        List<String> lines = document.getText();
        for (int i = 0; i < lines.size(); i++) {
            if (i < lines.size() - 1) {
                out.println("\"" + lines.get(i) + "\",");
                counter++;
            } else {
                out.println("\"" + lines.get(i) + "\"");
            }
        }
        if (meta) {
            out.println("],");
            out.println("\"metaData\":{");
            out.println("\"fileName\": " + "\"" + document.getMetaData().getName() + "\",");
            out.println("\"fileSize\": " + "\"" + document.getMetaData().getSize() + "KB\",");
            SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy 'at' hh:mm aaa");
            out.println("\"fileCreationDate\": " + "\"" + sdf.format(document.getMetaData().getDate()) + "\"");
            out.println("}");
        } else {
            out.println("]");
        }

        out.println("}");
        out.flush();
    }

    /**
     *
     * @param q queried text
     * @param length length of the line
     * @param limit quantity of lines
     * @param needMeta is there a need for MetaData
     * @return ready for sending document with needed only parameters
     */
    private Document getDocument(String q, int length, int limit, boolean needMeta) {
        File file = new File(System.getProperty("user.dir")+"/test.txt");
        Document document = new Document();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // enters an optimized search loop for concrete query
            if (!(q.isEmpty()) && length > 0 && limit > 0) {
                document = searchOnQnLengthnLimit(q, length, br, limit);
            } else if (!(q.isEmpty()) && length > 0) {
                document = searchOnQnLengthnLimit(q, length, br, 1000);
            } else if (!(q.isEmpty()) && limit > 0) {
                document = searchOnQnLimit(q, br, limit);
            } else if (length > 0 && limit > 0) {
                document = searchOnLengthnLimit(length, br, limit);
            } else if (!(q.isEmpty())) {
                document = searchOnQnLimit(q, br, 1000);
            } else if (length > 0) {
                document = searchOnLengthnLimit(length, br, 1000);
            } else {
                document = searchOnLengthnLimit(100000, br, 1000);
            }
            if (needMeta) {
                MetaData metaData = new MetaData();
                metaData.setDate(file.lastModified());//unfortunately my filesystem does not keep the creation date I took lastModified
                metaData.setSize(file.length() / 1024);//size transfers to KB
                metaData.setName(file.getName());
                document.setMetaData(metaData);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    private Document searchOnQnLimit(String q, BufferedReader br, int limit) throws IOException {
        Document document = new Document();
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null && document.getText().size() < limit) {
            if (sCurrentLine.contains(q)) {
                document.addText(sCurrentLine);
            }
        }
        return document;
    }

    private Document searchOnQnLengthnLimit(String q, int length, BufferedReader br, int limit) throws IOException {
        Document document = new Document();
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null && document.getText().size() < limit) {
            if (sCurrentLine.length() <= length) {
                if (sCurrentLine.contains(q)) {
                    document.addText(sCurrentLine);
                }
            }
        }
        return document;
    }

    private Document searchOnLengthnLimit(int length, BufferedReader br, int limit) throws IOException {
        Document document = new Document();
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null && document.getText().size() < limit) {
            document.addText(sCurrentLine);
        }
        return document;
    }

}
