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

    @Inject
    public AlphaNumWordFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new AlphaNumWordFilter(tokenStream);
    }
}
