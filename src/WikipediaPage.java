import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Class that manages the Wikipedia page
 * Saves information such as url, title, htmlDocument, categories, and termFrequency
 */

public class WikipediaPage {
    private String title;
    private String lowerTitle;
    private String url;
    private String text;
    private Document htmlDocument;
    private HashSet<String> categories;
    private HashMap<String, Integer> termFrequency;

    public WikipediaPage(String title, String text) throws IOException {
        try {
            this.title = title;
            this.lowerTitle = title.toLowerCase();
            this.url = "https://en.wikipedia.org/wiki/" + title.replace(" ", "_");
            this.text = text;
            this.htmlDocument = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
            this.categories = new HashSet<>();
            termFrequency = new HashMap<String, Integer>();
            createCategories();
            readPageAndPreProcess();

        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     * Modified version of readFileAndPreProcess() from hw4
     * Instead of working with .txt files, it parses the text elements of the htmlDocument into tokens
     * */
    private void readPageAndPreProcess() {
        String content = htmlDocument.text();

        String text = content.replaceAll("[^A-Za-z0-9]", " ").toLowerCase();

        String[] words = text.split("\\s+");

        for (String word : words) {
            if (!(word.equalsIgnoreCase(""))) {
                if (termFrequency.containsKey(word)) {
                    termFrequency.put(word, termFrequency.get(word) + 1);
                } else {
                    termFrequency.put(word, 1);
                }
            }
        }

    }

    /**
     * Finds the categories of the wikipedia page and saves them
     * */
    private void createCategories() {
        Elements lis = htmlDocument.select("div.mw-normal-catlinks ul li a");

        for (Element li : lis) {
            categories.add(li.attr("title"));
        }

    }

    public String getTitle() {
        return title;
    }

    public void setHtmlDocument(Document htmlDocument) {
        this.htmlDocument = htmlDocument;
    }

    public Document getHtmlDocument() {
        return htmlDocument;
    }

    public String getLowerTitle() {
        return lowerTitle;
    }

    public HashSet<String> getCategories() {
        return categories;
    }

    public double getTermFrequency(String word) {
        if (termFrequency.containsKey(word)) {
            return termFrequency.get(word);
        } else {
            return 0;
        }
    }

    public Set<String> getTermList() {
        return termFrequency.keySet();
    }

    public String getText() {
        return text;
    }
}
