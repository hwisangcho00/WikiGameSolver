import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WikipediaPage {
    private String title;
    private String url;
    private Document htmlDocument;

    public WikipediaPage(String title) {
        try {

            this.title = title.toLowerCase();
            this.url = "https://en.wikipedia.org/wiki/" + title.replace(" ", "_");
            this.htmlDocument = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setHtmlDocument(Document htmlDocument) {
        this.htmlDocument = htmlDocument;
    }

    public Document getHtmlDocument() {
        return htmlDocument;
    }
}
