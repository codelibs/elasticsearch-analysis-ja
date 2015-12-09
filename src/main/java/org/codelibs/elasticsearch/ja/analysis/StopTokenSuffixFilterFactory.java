package org.codelibs.elasticsearch.ja.analysis;

import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.StopTokenSuffixFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;
import org.elasticsearch.index.settings.IndexSettingsService;

public class StopTokenSuffixFilterFactory extends AbstractTokenFilterFactory {

    private final String[] stopwords;

    private boolean ignoreCase;

    @Inject
    public StopTokenSuffixFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings, Environment env) {
        super(index, indexSettingsService.getSettings(), name, settings);

        List<String> wordList = Analysis.getWordList(env, settings, "stopwords");
        if (wordList != null) {
            stopwords = wordList.toArray(new String[wordList.size()]);
        } else {
            stopwords = new String[0];
        }

        ignoreCase = settings.getAsBoolean("ignore_case", Boolean.FALSE).booleanValue();
        if (ignoreCase) {
            for (int i = 0; i < stopwords.length; i++) {
                stopwords[i] = stopwords[i].toLowerCase(Locale.ROOT);
            }
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new StopTokenSuffixFilter(tokenStream, stopwords, ignoreCase);
    }
}
