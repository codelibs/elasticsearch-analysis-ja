package org.codelibs.elasticsearch.ja.analysis;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PatternConcatenationFilterFactoryTest {

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
                settingsBuilder.put("index.number_of_shards", 3);
            }
        }).build(newConfigs().ramIndexStore().numOfNode(numOfNode)
                .clusterName(UUID.randomUUID().toString()));
    }

    @After
    public void cleanUp() throws Exception {
        runner.close();
        runner.clean();
    }

    @Test
    public void test_basic() throws Exception {

        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{"
                + "\"filter\":{"
                + "\"pattern_concat_filter\":{\"type\":\"pattern_concat\",\"pattern1\":\"昭和|平成\",\"pattern2\":\"[0-9]+年\"}"
                + "},"//
                + "\"analyzer\":{"
                + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\"},"
                + "\"ja_concat_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\",\"filter\":[\"pattern_concat_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index,
                ImmutableSettings.builder().loadFromSource(indexSettings)
                        .build());

        {
            String text = "平成 12年";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_concat_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(1, tokens.size());
                assertEquals("平成12年", tokens.get(0).get("token").toString());
            }
        }

        {
            String text = "aaa 昭和 3年 bbb";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_concat_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(3, tokens.size());
                assertEquals("aaa", tokens.get(0).get("token").toString());
                assertEquals("昭和3年", tokens.get(1).get("token").toString());
                assertEquals("bbb", tokens.get(2).get("token").toString());
            }
        }

        {
            String text = "大正 10年";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_concat_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(2, tokens.size());
                assertEquals("大正", tokens.get(0).get("token").toString());
                assertEquals("10年", tokens.get(1).get("token").toString());
            }
        }

        {
            String text = "昭和 10";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_concat_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(2, tokens.size());
                assertEquals("昭和", tokens.get(0).get("token").toString());
                assertEquals("10", tokens.get(1).get("token").toString());
            }
        }
    }

}
