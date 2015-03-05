package org.codelibs.elasticsearch.ja.analysis;

import java.io.Reader;

import org.codelibs.analysis.ja.ProlongedSoundMarkCharFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;

public class ProlongedSoundMarkCharFilterFactory extends
        AbstractCharFilterFactory {
    private char replacement;

    @Inject
    public ProlongedSoundMarkCharFilterFactory(Index index,
            @IndexSettings Settings indexSettings, @Assisted String name,
            @Assisted Settings settings) {
        super(index, indexSettings, name);
        String value = settings.get("replacement");
        if (value == null || value.length() == 0) {
            replacement = '\u30fc';
        } else {
            replacement = value.charAt(0);
        }
    }

    @Override
    public Reader create(Reader tokenStream) {
        return new ProlongedSoundMarkCharFilter(tokenStream, replacement);
    }

}
