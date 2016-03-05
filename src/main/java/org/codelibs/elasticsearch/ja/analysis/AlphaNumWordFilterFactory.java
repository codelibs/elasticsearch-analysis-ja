package org.codelibs.elasticsearch.ja.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.en.AlphaNumWordFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class AlphaNumWordFilterFactory extends AbstractTokenFilterFactory {

    private final int maxTokenLength;

    @Inject
    public AlphaNumWordFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);

        maxTokenLength = settings.getAsInt("max_token_length", AlphaNumWordFilter.DEFAULT_MAX_TOKEN_LENGTH);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        AlphaNumWordFilter alphaNumWordFilter = new AlphaNumWordFilter(tokenStream);
        alphaNumWordFilter.setMaxTokenLength(maxTokenLength);
        return alphaNumWordFilter;
    }
}
