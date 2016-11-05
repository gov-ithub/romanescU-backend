package romanescu.backend.application;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElasticsearchController {

	private static final String API_PREFIX = "/api/v0/";

	@Autowired
	private Client client;
	@Autowired
	private ElasticsearchTemplate elasticSearchTemplate;
	
	@RequestMapping(API_PREFIX + "getDocument/{index}/{type}/{id}")
	public Document getById(@PathVariable(value="index") String index, 
			@PathVariable(value="type") String type,
			@PathVariable(value="id") String id) {
		GetResponse response = client.prepareGet(index, type, id).get();
		return new Document(response.getId(), response.getIndex(), response.getType(), response.getSource());
	}
	
	@RequestMapping(value = API_PREFIX + "{index}/_create", method=RequestMethod.POST)
	public IndexCreated createIndex(@PathVariable(value="index") String index) {
		String indexCreated = "true";
		if(!elasticSearchTemplate.indexExists(index)) {
			elasticSearchTemplate.createIndex(index);
			indexCreated = "true";
		} else 
			indexCreated = "false";
		return new IndexCreated(indexCreated);
	}
	
	@RequestMapping(value = API_PREFIX + "indexDocument/{index}/{type}/{id}", method=RequestMethod.POST)
	public void indexDocument(@PathVariable(value="index") String index,
			@PathVariable(value="type") String type,
			@PathVariable(value="id") String id,
			@RequestBody String source) {
		client.prepareIndex(index, type, id).setSource(source).get();
	}
	
	@RequestMapping(value = API_PREFIX + "deleteDocument/{index}/{type}/{id}", method=RequestMethod.DELETE)
	public void deleteDocument(@PathVariable(value="index") String index,
			@PathVariable(value="type") String type,
			@PathVariable(value="id") String id) {
		client.prepareDelete(index, type, id).get();
	}
	
	@RequestMapping(value = API_PREFIX + "updateDocument/{index}/{type}/{id}", method=RequestMethod.PATCH)
	public void updateDocument(@PathVariable(value="index") String index,
			@PathVariable(value="type") String type,
			@PathVariable(value="id") String id,
			@RequestBody String source) {
		client.prepareUpdate(index, type, id).setDoc(source).get();
	}
	
	@RequestMapping(value = API_PREFIX + "getDocuments/{index}/_all", method=RequestMethod.GET)
	public List<Document> getAllDocuments(@PathVariable(value="index") String index) {
		SearchRequestBuilder requestBuilder = client.prepareSearch(index).setQuery(QueryBuilders.matchAllQuery());
		SearchHitIterator hitIterator = new SearchHitIterator(requestBuilder);
		List<Document> documentList = new ArrayList<Document>();
		while (hitIterator.hasNext()) {
		    SearchHit hit = hitIterator.next();
		    documentList.add(new Document(hit.getId(), hit.getIndex(), hit.getType(), hit.getSource()));
		}
		return documentList;
	}
	
}
