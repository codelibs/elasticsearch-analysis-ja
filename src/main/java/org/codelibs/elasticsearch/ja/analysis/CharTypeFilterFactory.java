package org.codelibs.elasticsearch.ja.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.CharTypeFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class CharTypeFilterFactory extends AbstractTokenFilterFactory {

    private final boolean alphabetic;

    private final boolean digit;

    private final boolean letter;

    @Inject
    public CharTypeFilterFactory(Index index,
            IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);

        alphabetic = settings.getAsBoolean("alphabetic", true);
        digit = settings.getAsBoolean("digit", true);
        letter = settings.getAsBoolean("letter", true);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new CharTypeFilter(tokenStream, alphabetic, digit, letter);
    }
}
