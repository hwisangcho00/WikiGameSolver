import org.jsoup.nodes.Document;

import java.util.*;

/**
 * This modified class representation of a corpus of Wikipedia pages.
 * It will create an inverted index for these documents.
 * @author swapneel
 * Modified by hwisang
 */
public class PageCorpus {
    private ArrayList<WikipediaPage> pages;
    private HashMap<String, Set<WikipediaPage>> invertedIndex;

    public PageCorpus(ArrayList<WikipediaPage> pages) {
        this.pages = pages;
        invertedIndex = new HashMap<>();

        createInvertedIndex();
    }

    private void createInvertedIndex() {
        for (WikipediaPage page : this.pages) {
            Set<String> terms = page.getTermList();

            for (String term : terms) {
                if (invertedIndex.containsKey(term)) {
                    Set<WikipediaPage> list = invertedIndex.get(term);
                    list.add(page);
                } else {
                    Set<WikipediaPage> list = new HashSet<>();
                    list.add(page);
                    invertedIndex.put(term, list);
                }
            }
        }
    }

    public double getInverseDocumentFrequency(String term) {
        if (invertedIndex.containsKey(term)) {
            double size = pages.size();
            Set<WikipediaPage> list = invertedIndex.get(term);
            double documentFrequency = list.size();

            return Math.log10(size / documentFrequency);
        } else {
            return 0;
        }
    }

    public ArrayList<WikipediaPage> getDocuments() {
        return pages;
    }

    public HashMap<String, Set<WikipediaPage>> getInvertedIndex() {
        return invertedIndex;
    }
}
