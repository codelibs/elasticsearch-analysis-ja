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

public class CharTypeFilterFactoryTest {

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
        }).build(newConfigs().ramIndexStore().numOfNode(numOfNode));

    }

    @After
    public void cleanUp() throws Exception {
        runner.close();
        runner.clean();
    }

    @Test
    public void test_alphabetic() throws Exception {
        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{"
                + "\"filter\":{"
                + "\"alphabetic_filter\":{\"type\":\"char_type\",\"alphabetic\":true,\"digit\":false,\"letter\":false}"
                + "},"//
                + "\"analyzer\":{"
                + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\"},"
                + "\"ja_alphabetic_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\",\"filter\":[\"alphabetic_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index,
                ImmutableSettings.builder().loadFromSource(indexSettings)
                        .build());

        {
            String text = "aaa aa1 aaあ aa! 111 11あ 11- あああ ああ- ---";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_alphabetic_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(4, tokens.size());
                assertEquals("aaa", tokens.get(0).get("token").toString());
                assertEquals("aa1", tokens.get(1).get("token").toString());
                assertEquals("aaあ", tokens.get(2).get("token").toString());
                assertEquals("aa!", tokens.get(3).get("token").toString());
            }
        }
    }

    @Test
    public void test_digit() throws Exception {
        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{"
                + "\"filter\":{"
                + "\"alphabetic_filter\":{\"type\":\"char_type\",\"alphabetic\":false,\"digit\":true,\"letter\":false}"
                + "},"//
                + "\"analyzer\":{"
                + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\"},"
                + "\"ja_alphabetic_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\",\"filter\":[\"alphabetic_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index,
                ImmutableSettings.builder().loadFromSource(indexSettings)
                        .build());

        {
            String text = "aaa aa1 aaあ aa! 111 11あ 11- あああ ああ- ---";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_alphabetic_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(4, tokens.size());
                assertEquals("aa1", tokens.get(0).get("token").toString());
                assertEquals("111", tokens.get(1).get("token").toString());
                assertEquals("11あ", tokens.get(2).get("token").toString());
                assertEquals("11-", tokens.get(3).get("token").toString());
            }
        }
    }

    @Test
    public void test_letter() throws Exception {
        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{"
                + "\"filter\":{"
                + "\"alphabetic_filter\":{\"type\":\"char_type\",\"alphabetic\":false,\"digit\":false,\"letter\":true}"
                + "},"//
                + "\"analyzer\":{"
                + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\"},"
                + "\"ja_alphabetic_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\",\"filter\":[\"alphabetic_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index,
                ImmutableSettings.builder().loadFromSource(indexSettings)
                        .build());

        {
            String text = "aaa aa1 aaあ aa! 111 11あ 11- あああ ああ- ---";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_alphabetic_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(7, tokens.size());
                assertEquals("aaa", tokens.get(0).get("token").toString());
                assertEquals("aa1", tokens.get(1).get("token").toString());
                assertEquals("aaあ", tokens.get(2).get("token").toString());
                assertEquals("aa!", tokens.get(3).get("token").toString());
                assertEquals("11あ", tokens.get(4).get("token").toString());
                assertEquals("あああ", tokens.get(5).get("token").toString());
                assertEquals("ああ-", tokens.get(6).get("token").toString());
            }
        }
    }

    @Test
    public void test_digitOrLetter() throws Exception {
        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{"
                + "\"filter\":{"
                + "\"alphabetic_filter\":{\"type\":\"char_type\",\"alphabetic\":false,\"digit\":true,\"letter\":true}"
                + "},"//
                + "\"analyzer\":{"
                + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\"},"
                + "\"ja_alphabetic_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\",\"filter\":[\"alphabetic_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index,
                ImmutableSettings.builder().loadFromSource(indexSettings)
                        .build());

        {
            String text = "aaa aa1 aaあ aa! 111 11あ 11- あああ ああ- ---";
            try (CurlResponse response = Curl
                    .post(node, "/" + index + "/_analyze")
                    .param("analyzer", "ja_alphabetic_analyzer").body(text)
                    .execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response
                        .getContentAsMap().get("tokens");
                assertEquals(9, tokens.size());
                assertEquals("aaa", tokens.get(0).get("token").toString());
                assertEquals("aa1", tokens.get(1).get("token").toString());
                assertEquals("aaあ", tokens.get(2).get("token").toString());
                assertEquals("aa!", tokens.get(3).get("token").toString());
                assertEquals("111", tokens.get(4).get("token").toString());
                assertEquals("11あ", tokens.get(5).get("token").toString());
                assertEquals("11-", tokens.get(6).get("token").toString());
                assertEquals("あああ", tokens.get(7).get("token").toString());
                assertEquals("ああ-", tokens.get(8).get("token").toString());
            }
        }
    }
}