import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class WikipediaGameBoard {
    private WikipediaPage startPage;
    private WikipediaPage endPage;

    public WikipediaGameBoard(WikipediaPage start, WikipediaPage end) {
        this.startPage = start;
        this.endPage = end;
    }

    public WikipediaPage getStartPage() {
        return startPage;
    }

    public WikipediaPage getEndPage() {
        return endPage;
    }

    public boolean isRelated() {
        Queue<WikipediaPage> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<WikipediaPage, WikipediaPage> parentMap = new HashMap<>();

        queue.offer(this.startPage);
        visited.add(startPage.getTitle());

        while (!queue.isEmpty()) {
            WikipediaPage currentPage = queue.poll();
            Document currentDocument = currentPage.getHtmlDocument();
            System.out.println(currentDocument.location());
            if (currentDocument.location().equals(endPage.getUrl())) {
                // We've found a path from the start to the end page!
                // Backtrack from the end page to the start page to construct the path.
                List<WikipediaPage> path = new ArrayList<>();
                WikipediaPage page = currentPage;
                while (page != null) {
                    path.add(page);
                    page = parentMap.get(page);
                }
                for (WikipediaPage p : path) {
                    System.out.println(p.getTitle());
                }
                return true;
            }

            Elements links = currentDocument.select("p a[href]");

            for (Element link : links) {
                String href = link.attr("href");
                if (href.startsWith("/wiki/") && !href.contains(":")) {
                    String title = link.attr("title");
                    System.out.println(title);
                    WikipediaPage linkedPage = new WikipediaPage(title);
                    if (!visited.contains(linkedPage.getTitle())) {
                        visited.add(title.toLowerCase());
                        parentMap.put(linkedPage, currentPage);
                        queue.offer(linkedPage);
                    }
                }
            }
        }
        return false;
    }


}
