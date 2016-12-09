package org.codelibs.elasticsearch.ja.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.en.FlexiblePorterStemFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class FlexiblePorterStemFilterFactory extends AbstractTokenFilterFactory {

    private boolean step1;

    private boolean step2;

    private boolean step3;

    private boolean step4;

    private boolean step5;

    private boolean step6;

    public FlexiblePorterStemFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);

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