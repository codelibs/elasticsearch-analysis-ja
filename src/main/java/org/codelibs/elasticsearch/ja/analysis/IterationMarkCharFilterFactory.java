package org.codelibs.elasticsearch.ja.analysis;

import java.io.Reader;

import org.codelibs.analysis.ja.IterationMarkCharFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class IterationMarkCharFilterFactory extends AbstractCharFilterFactory {
    @Inject
    public IterationMarkCharFilterFactory(Index index,
            IndexSettingsService indexSettingsService, @Assisted String name,
            @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name);
    }

    @Override
    public Reader create(Reader tokenStream) {
        return new IterationMarkCharFilter(tokenStream);
    }

}
