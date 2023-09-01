import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Primary class that computes and outputs the path of links.
 *
 * */
public class WikipediaGameBoard {
    final private WikipediaPage startPage;
    final private WikipediaPage endPage;
    // For efficiency, limit the number of links visited per step
    final private int MAX_TFIDF_LINK_NUMBER = 300;
    final private int MAX_BFS_LINK_NUMBER = 800;


    public WikipediaGameBoard(WikipediaPage start, WikipediaPage end) {
        this.startPage = start;
        this.endPage = end;
    }

    /**
     *
     * Find the path using tf-idf
     * returns true if a path is found, false otherwise
     *
     * */
    public boolean solveTFIDF(WikipediaPage currentPage, Set<String> visited, Map<WikipediaPage, WikipediaPage> parentMap) {

        Document currentDocument = currentPage.getHtmlDocument();

        // add the first page to the visited
        visited.add(startPage.getLowerTitle());

        while (true) {
            ArrayList<WikipediaPage> pages = new ArrayList<>();

            // always add the end page to the corpus as the first element
            pages.add(endPage);

            // find all links
            Elements links = currentDocument.select("p a[href]");

            // redirect links do not have a p tag. Find for links within a list
            if (links.size() == 0) {
                links = currentDocument.select("div.mw-parser-output ul li a[href]");
            }

            System.out.println("=============================================================");
            System.out.println("Currently looking at \"" + currentPage.getTitle() + "\" wikipedia page.");
            System.out.println("Finding the most relevant link... Calculating " + links.size() + " links");

            for (Element link : links.subList(0, Math.min(links.size(), MAX_TFIDF_LINK_NUMBER))) {
                String href = link.attr("href");
                if (href.startsWith("/wiki/") && !href.contains(":")) {
                    String title = link.attr("title");
                    WikipediaPage linkedPage;
                    // Is this a valid link? If so, create the linkedPage
                    try {
                        linkedPage = new WikipediaPage(title, link.text());
                    } catch (IOException e) {
                        continue;
                    }

                    // If we have found our target page within this link, output the path
                    if (endPage.getLowerTitle().equals(title.toLowerCase())) {
                        parentMap.put(linkedPage, currentPage);

                        List<WikipediaPage> path = new ArrayList<>();
                        WikipediaPage page = linkedPage;
                        while (page != null) {
                            path.add(page);
                            page = parentMap.get(page);
                        }

                        System.out.printf("Path from %s to %s : \n", startPage.getTitle(), endPage.getTitle());

                        Collections.reverse(path);


                        for (WikipediaPage p : path) {
                            // Output the text so that it's easier to find using Control + f
                            System.out.println(p.getText());
                        }

                        return true;

                    }

                    // Only add the page if it has not been visited yet. This prevents any never ending loops
                    // Also we don't want to visit redirect page
                    if (!visited.contains(linkedPage.getLowerTitle())) {
                        visited.add(linkedPage.getLowerTitle());
                        pages.add(linkedPage);
                    }
                }
            }

            // Calculate the tf-idf value

            PageCorpus pageCorpus = new PageCorpus(pages);

            VectorSpaceModel vsm = new VectorSpaceModel(pageCorpus);

            double maxSimilarity = Double.MIN_VALUE;
            int maxIndex = -1;

            for (int i = 1; i < pages.size(); i++) {
                WikipediaPage page = pages.get(i);

                double currValue = vsm.cosineSimilarity(endPage, page);

                // Find the max cosine value
                if (maxSimilarity < currValue) {
                    maxSimilarity = currValue;
                    maxIndex = i;
                }

            }

            // Sometimes, we encounter a page is formatted differently. We take care of this edge case so that our program does not halt unintentionally
            if (maxIndex == -1) {
                System.out.println("Unexpected Error occurred: This page does not have any link or maybe a redirect page.");
                return false;
            }

            // Set the currDocument to the most similar document and continue the loop

            WikipediaPage linkedPage = pages.get(maxIndex);

            parentMap.put(linkedPage, currentPage);

            currentPage = linkedPage;

            currentDocument = currentPage.getHtmlDocument();

        }

    }

    /**
     * This implementation uses a BFS to find a valid path.
     * However, due to memory limitations, we only add the link to the queue if it has the same category as our target link.
     * If no link shares the same category, we randomly a new link to jump to.
     * */
    public boolean solveBFS() {
        Queue<WikipediaPage> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<WikipediaPage, WikipediaPage> parentMap = new HashMap<>();

        queue.offer(this.startPage);
        visited.add(startPage.getTitle());

        while (!queue.isEmpty()) {

            // This boolean value keeps track whether we find a link that shares a common categories
            boolean flag = false;

            WikipediaPage currentPage = queue.poll();
            Document currentDocument = currentPage.getHtmlDocument();


            Elements links = currentDocument.select("p a[href]");

            if (links.size() == 0) {
                links = currentDocument.select("div.mw-parser-output ul li a[href]");
            }

            System.out.println("=============================================================");
            System.out.println("Currently looking at \"" + currentPage.getTitle() + "\" wikipedia page.");
            System.out.println("Looking for all links that has the same category... Searching " + links.size() + " links");

            for (Element link : links.subList(0, Math.min(links.size(), MAX_BFS_LINK_NUMBER))) {

                String href = link.attr("href");
                if (href.startsWith("/wiki/") && !href.contains(":")) {
                    String title = link.attr("title");
                    WikipediaPage linkedPage = null;
                    try {
                        linkedPage = new WikipediaPage(title, link.text());
                    } catch (IOException e) {
                        continue;
                    }

                    if (!checkSameCategories(endPage, linkedPage)) {
                        continue;
                    }

                    if (endPage.getLowerTitle().equals(title.toLowerCase())) {
                        parentMap.put(linkedPage, currentPage);

                        List<WikipediaPage> path = new ArrayList<>();
                        WikipediaPage page = linkedPage;
                        while (page != null) {
                            path.add(page);
                            page = parentMap.get(page);
                        }

                        System.out.printf("Path from %s to %s : \n", startPage.getLowerTitle(), endPage.getLowerTitle());

                        Collections.reverse(path);

                        for (WikipediaPage p : path) {
                            System.out.println(p.getText());
                        }
                        return true;
                    }

                    if (!visited.contains(linkedPage.getLowerTitle())) {
                        visited.add(title.toLowerCase());
                        parentMap.put(linkedPage, currentPage);
                        queue.offer(linkedPage);
                        flag = true;
                    }
                }
            }

            // We haven't found any relevant links.
            if (!flag) {
                while (true) {
                    if (links.isEmpty()) {
                        break;
                    }

                    System.out.println("No link with relevant category. Choosing a random link to follow");

                    // We will choose a random link that we will jump to
                    Element link = links.get(new Random().nextInt(links.size()));

                    String href = link.attr("href");
                    if (href.startsWith("/wiki/") && !href.contains(":")) {
                        String title = link.attr("title");
                        WikipediaPage linkedPage = null;
                        try {
                            linkedPage = new WikipediaPage(title, link.text());
                        } catch (IOException e) {
                            continue;
                        }

                        if (!visited.contains(linkedPage.getLowerTitle())) {
                            visited.add(title.toLowerCase());
                            parentMap.put(linkedPage, currentPage);
                            queue.offer(linkedPage);
                            break;
                        }
                    }
                }
            }

        }
        return false;
    }

    private boolean checkSameCategories(WikipediaPage endPage, WikipediaPage linkPage) {

        HashSet<String> endCategories = endPage.getCategories();

        for (String linkCategories : linkPage.getCategories()) {
            if (endCategories.contains(linkCategories)) {
                return true;
            }
        }

        return false;

    }

}
