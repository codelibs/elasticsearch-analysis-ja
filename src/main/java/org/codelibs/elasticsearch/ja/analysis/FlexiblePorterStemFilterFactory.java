package org.codelibs.elasticsearch.ja.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.en.FlexiblePorterStemFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class FlexiblePorterStemFilterFactory extends AbstractTokenFilterFactory {

    private boolean step1;

    private boolean step2;

    private boolean step3;

    private boolean step4;

    private boolean step5;

    private boolean step6;

    @Inject
    public FlexiblePorterStemFilterFactory(final Index index, final IndexSettingsService indexSettingsService, final Environment env,
            @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);

        step1 = settings.getAsBoolean("step1", true);
        step2 = settings.getAsBoolean("step2", true);
        step3 = settings.getAsBoolean("step3", true);
        step4 = settings.getAsBoolean("step4", true);
        step5 = settings.getAsBoolean("step5", true);
        step6 = settings.getAsBoolean("step6", true);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new FlexiblePorterStemFilter(tokenStream, step1, step2, step3, step4, step5, step6);
    }

}