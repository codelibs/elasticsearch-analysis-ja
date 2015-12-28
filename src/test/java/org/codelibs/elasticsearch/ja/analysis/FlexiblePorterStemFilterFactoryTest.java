package org.codelibs.elasticsearch.ja.analysis;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FlexiblePorterStemFilterFactoryTest {

    private ElasticsearchClusterRunner runner;

    private int numOfNode = 1;

    @Before
    public void setUp() throws Exception {
        runner = new ElasticsearchClusterRunner();
        runner.onBuild(new ElasticsearchClusterRunner.Builder() {
            @Override
            public void build(final int number, final Builder settingsBuilder) {
                settingsBuilder.put("http.cors.enabled", true);
                settingsBuilder.put("index.number_of_replicas", 0);
                settingsBuilder.put("index.number_of_shards", 1);
            }
        }).build(newConfigs().ramIndexStore().numOfNode(numOfNode));

    }

    @After
    public void cleanUp() throws Exception {
        runner.close();
        runner.clean();
    }

    @Test
    public void test_step1() throws Exception {
        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{" + "\"filter\":{"
                + "\"stem1_filter\":{\"type\":\"flexible_porter_stem\",\"step1\":true,\"step2\":false,\"step3\":false,\"step4\":false,\"step5\":false,\"step6\":false}"
                + "},"//
                + "\"analyzer\":{" + "\"default_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\"},"
                + "\"stem1_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\",\"filter\":[\"stem1_filter\"]}" + "}"//
                + "}}}";
        runner.createIndex(index, ImmutableSettings.builder().loadFromSource(indexSettings).build());
        runner.ensureYellow();

        {
            String text = "consist consisted consistency consistent consistently consisting consists";
            try (CurlResponse response =
                    Curl.post(node, "/" + index + "/_analyze").param("analyzer", "stem1_analyzer").body(text).execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContentAsMap().get("tokens");
                assertEquals(7, tokens.size());
                assertEquals("consist", tokens.get(0).get("token").toString());
                assertEquals("consist", tokens.get(1).get("token").toString());
                assertEquals("consistency", tokens.get(2).get("token").toString());
                assertEquals("consistent", tokens.get(3).get("token").toString());
                assertEquals("consistently", tokens.get(4).get("token").toString());
                assertEquals("consist", tokens.get(5).get("token").toString());
                assertEquals("consist", tokens.get(6).get("token").toString());
            }
        }
    }

}
