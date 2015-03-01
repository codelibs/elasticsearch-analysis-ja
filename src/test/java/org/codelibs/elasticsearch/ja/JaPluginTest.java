package org.codelibs.elasticsearch.ja;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JaPluginTest {

    private ElasticsearchClusterRunner runner;

    private File[] userDictFiles;

    private int numOfNode = 2;

    private int numOfDocs = 1000;

    @Before
    public void setUp() throws Exception {
        runner = new ElasticsearchClusterRunner();
        runner.onBuild(new ElasticsearchClusterRunner.Builder() {
            @Override
            public void build(final int number, final Builder settingsBuilder) {
                settingsBuilder.put("http.cors.enabled", true);
                settingsBuilder.put("index.number_of_replicas", 0);
                settingsBuilder.put("index.number_of_shards", 3);
            }
        }).build(newConfigs().ramIndexStore().numOfNode(numOfNode));

        userDictFiles = null;
    }

    private void updateDictionary(File file, String content)
            throws IOException, UnsupportedEncodingException,
            FileNotFoundException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            bw.write(content);
            bw.flush();
        }
    }

    @After
    public void cleanUp() throws Exception {
        runner.close();
        runner.clean();
        if (userDictFiles != null) {
            for (File file : userDictFiles) {
                file.deleteOnExit();
            }
        }
    }

    @Test
    public void test_kuromoji() throws Exception {
        userDictFiles = new File[numOfNode];
        for (int i = 0; i < numOfNode; i++) {
            String confPath = runner.getNode(i).settings().get("path.conf");
            userDictFiles[i] = new File(confPath, "userdict_ja.txt");
            updateDictionary(userDictFiles[i],
                    "東京スカイツリー,東京 スカイツリー,トウキョウ スカイツリー,カスタム名詞");
        }

        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";
        final String type = "item";

        final String indexSettings = "{\"index\":{\"analysis\":{"
                + "\"tokenizer\":{"//
                + "\"kuromoji_user_dict\":{\"type\":\"kuromoji_tokenizer\",\"mode\":\"extended\",\"user_dictionary\":\"userdict_ja.txt\"},"
                + "\"kuromoji_user_dict_reload\":{\"type\":\"reloadable_kuromoji_tokenizer\",\"mode\":\"extended\",\"user_dictionary\":\"userdict_ja.txt\",\"reload_interval\":\"1s\"}"
                + "},"//
                + "\"analyzer\":{"
                + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"kuromoji_user_dict\",\"filter\":[\"kuromoji_stemmer\"]},"
                + "\"ja_reload_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"kuromoji_user_dict_reload\",\"filter\":[\"kuromoji_stemmer\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index,
                ImmutableSettings.builder().loadFromSource(indexSettings)
                        .build());

        // create a mapping
        final XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()//
                .startObject()//
                .startObject(type)//
                .startObject("properties")//

                // id
                .startObject("id")//
                .field("type", "string")//
                .field("index", "not_analyzed")//
                .endObject()//

                // msg1
                .startObject("msg1")//
                .field("type", "string")//
                .field("analyzer", "ja_reload_analyzer")//
                .endObject()//

                // msg2
                .startObject("msg2")//
                .field("type", "string")//
                .field("analyzer", "ja_analyzer")//
                .endObject()//

                .endObject()//
                .endObject()//
                .endObject();
        runner.createMapping(index, type, mappingBuilder);

        final IndexResponse indexResponse1 = runner.insert(index, type, "1",
                "{\"msg1\":\"東京スカイツリー\", \"msg2\":\"東京スカイツリー\", \"id\":\"1\"}");
        assertTrue(indexResponse1.isCreated());
        runner.refresh();

        for (int i = 0; i < 1000; i++) {
            assertDocCount(1, index, type, "msg1", "東京スカイツリー");
            assertDocCount(1, index, type, "msg2", "東京スカイツリー");

            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_reload_analyzer").body("東京スカイツリー")
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals("東京", tokens.get(0).get("token").toString());
                assertEquals("スカイツリ", tokens.get(1).get("token").toString());
            }

            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_reload_analyzer").body("朝青龍")
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals("朝", tokens.get(0).get("token").toString());
                assertEquals("青龍", tokens.get(1).get("token").toString());
            }
        }

        // changing a file timestamp
        Thread.sleep(2000);

        for (int i = 0; i < numOfNode; i++) {
            updateDictionary(userDictFiles[i],
                    "東京スカイツリー,東京 スカイ ツリー,トウキョウ スカイ ツリー,カスタム名詞\n"
                            + "朝青龍,朝青龍,アサショウリュウ,人名");
        }

        final IndexResponse indexResponse2 = runner.insert(index, type, "2",
                "{\"msg1\":\"東京スカイツリー\", \"msg2\":\"東京スカイツリー\", \"id\":\"2\"}");
        assertTrue(indexResponse2.isCreated());
        runner.refresh();

        for (int i = 0; i < 1000; i++) {
            assertDocCount(1, index, type, "msg1", "東京スカイツリー");
            assertDocCount(2, index, type, "msg2", "東京スカイツリー");

            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_reload_analyzer").body("東京スカイツリー")
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals("東京", tokens.get(0).get("token").toString());
                assertEquals("スカイ", tokens.get(1).get("token").toString());
                assertEquals("ツリー", tokens.get(2).get("token").toString());
            }

            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_reload_analyzer").body("朝青龍")
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals("朝青龍", tokens.get(0).get("token").toString());
            }
        }
    }

    private void assertDocCount(int expected, final String index,
            final String type, final String field, final String value) {
        final SearchResponse searchResponse = runner.search(index, type,
                QueryBuilders.matchQuery(field, value).type(Type.PHRASE), null,
                0, numOfDocs);
        assertEquals(expected, searchResponse.getHits().getTotalHits());
    }
}
