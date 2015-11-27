package org.codelibs.elasticsearch.ja.analysis;

import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.PatternConcatenationFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class PatternConcatenationFilterFactory extends
        AbstractTokenFilterFactory {

    private Pattern pattern1;

    private Pattern pattern2;

    @Inject
    public PatternConcatenationFilterFactory(Index index,
            IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings, Environment env) {
        super(index, indexSettingsService.getSettings(), name, settings);

        String pattern1Str = settings.get("pattern1");
        String pattern2Str = settings.get("pattern2", ".*");

        if (logger.isDebugEnabled()) {
            logger.debug("pattern1: {}, pattern2: {}", pattern1Str, pattern2Str);
        }
        if (pattern1Str != null) {
            pattern1 = Pattern.compile(pattern1Str);
            pattern2 = Pattern.compile(pattern2Str);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new PatternConcatenationFilter(tokenStream, pattern1, pattern2);
    }
}
