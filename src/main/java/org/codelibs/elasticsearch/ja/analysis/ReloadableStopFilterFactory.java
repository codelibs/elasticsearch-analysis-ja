package org.codelibs.elasticsearch.ja.analysis;

import java.nio.file.Path;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.en.ReloadableStopFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettingsService;

public class ReloadableStopFilterFactory extends AbstractTokenFilterFactory {

    private final Path stopwordPath;

    private final long reloadInterval;

    private final boolean ignoreCase;

    @Inject
    public ReloadableStopFilterFactory(Index index, final IndexSettingsService indexSettingsService, final Environment env,
            @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);

        final String path = settings.get("stopwords_path");
        if (path != null) {
            stopwordPath = env.configFile().resolve(path);
        } else {
            stopwordPath = null;
        }

        ignoreCase = settings.getAsBoolean("ignore_case", false);
        reloadInterval = settings.getAsTime("reload_interval", TimeValue.timeValueMinutes(1)).getMillis();
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (stopwordPath == null) {
            return tokenStream;
        }
        return new ReloadableStopFilter(tokenStream, stopwordPath, ignoreCase, reloadInterval);
    }

}