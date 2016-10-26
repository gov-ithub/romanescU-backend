package romanescu.backend.application;

import java.util.Iterator;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

public class SearchHitIterator implements Iterator<SearchHit> {

    private final SearchRequestBuilder initialRequest;

    private int searchHitCounter;
    private SearchHit[] currentPageResults;
    private int currentResultIndex;

    public SearchHitIterator(SearchRequestBuilder initialRequest) {
        this.initialRequest = initialRequest;
        this.searchHitCounter = 0;
        this.currentResultIndex = -1;
    }

    @Override
    public boolean hasNext() {
        if (currentPageResults == null || currentResultIndex + 1 >= currentPageResults.length) {
            SearchRequestBuilder paginatedRequestBuilder = initialRequest.setFrom(searchHitCounter);
            SearchResponse response = paginatedRequestBuilder.execute().actionGet();
            currentPageResults = response.getHits().getHits();

            if (currentPageResults.length < 1) return false;

            currentResultIndex = -1;
        }

        return true;
    }

    @Override
    public SearchHit next() {
        if (!hasNext()) return null;

        currentResultIndex++;
        searchHitCounter++;
        return currentPageResults[currentResultIndex];
    }

}