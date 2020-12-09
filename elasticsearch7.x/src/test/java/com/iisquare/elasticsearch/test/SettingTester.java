package com.iisquare.elasticsearch.test;

import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Setting.Property;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.mapper.AllFieldMapper;
import org.elasticsearch.index.translog.Translog;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SettingTester {

    public Setting<String> DEFAULT_FIELD_SETTING = new Setting<>(
            "index.query.default_field", AllFieldMapper.NAME,
            Function.identity(), Property.IndexScope);
    public Setting<Boolean> QUERY_STRING_LENIENT_SETTING = Setting.boolSetting(
            "index.query_string.lenient", false, Property.IndexScope);
    public Setting<Boolean> QUERY_STRING_ANALYZE_WILDCARD = Setting
            .boolSetting("indices.query.query_string.analyze_wildcard", false,
                    Property.NodeScope);
    public Setting<Boolean> QUERY_STRING_ALLOW_LEADING_WILDCARD = Setting
            .boolSetting("indices.query.query_string.allowLeadingWildcard",
                    true, Property.NodeScope);
    public Setting<Boolean> ALLOW_UNMAPPED = Setting.boolSetting(
            "index.query.parse.allow_unmapped_fields", true,
            Property.IndexScope);
    public Setting<TimeValue> INDEX_TRANSLOG_SYNC_INTERVAL_SETTING = Setting
            .timeSetting("index.translog.sync_interval",
                    TimeValue.timeValueSeconds(5),
                    TimeValue.timeValueMillis(100), Property.IndexScope);
    public Setting<Translog.Durability> INDEX_TRANSLOG_DURABILITY_SETTING = new Setting<>(
            "index.translog.durability", Translog.Durability.REQUEST.name(), (
            value) -> Translog.Durability.valueOf(value
            .toUpperCase(Locale.ROOT)), Property.Dynamic,
            Property.IndexScope);
    public Setting<Boolean> INDEX_WARMER_ENABLED_SETTING = Setting
            .boolSetting("index.warmer.enabled", true, Property.Dynamic,
                    Property.IndexScope);
    public Setting<Boolean> INDEX_TTL_DISABLE_PURGE_SETTING = Setting
            .boolSetting("index.ttl.disable_purge", false, Property.Dynamic,
                    Property.IndexScope);
    public Setting<String> INDEX_CHECK_ON_STARTUP = new Setting<>(
            "index.shard.check_on_startup",
            "false",
            (s) -> {
                switch (s) {
                    case "false":
                    case "true":
                    case "fix":
                    case "checksum":
                        return s;
                    default:
                        throw new IllegalArgumentException(
                                "unknown value for [index.shard.check_on_startup] must be one of [true, false, fix, checksum] but was: "
                                        + s);
                }
            }, Property.IndexScope);

    /**
     * Index setting describing the maximum value of from + size on a query. The
     * Default maximum value of from + size on a query is 10,000. This was
     * chosen as a conservative default as it is sure to not cause trouble.
     * Users can certainly profile their cluster and decide to set it to 100,000
     * safely. 1,000,000 is probably way to high for any cluster to set safely.
     */
    public Setting<Integer> MAX_RESULT_WINDOW_SETTING = Setting.intSetting(
            "index.max_result_window", 10000, 1, Property.Dynamic,
            Property.IndexScope);
    /**
     * Index setting describing the maximum size of the rescore window. Defaults
     * to {@link #MAX_RESULT_WINDOW_SETTING} because they both do the same
     * thing: control the size of the heap of hits.
     */
    public Setting<Integer> MAX_RESCORE_WINDOW_SETTING = Setting.intSetting(
            "index.max_rescore_window", MAX_RESULT_WINDOW_SETTING, 1,
            Property.Dynamic, Property.IndexScope);
    public TimeValue DEFAULT_REFRESH_INTERVAL = new TimeValue(1,
            TimeUnit.SECONDS);
    public Setting<TimeValue> INDEX_REFRESH_INTERVAL_SETTING = Setting
            .timeSetting("index.refresh_interval", DEFAULT_REFRESH_INTERVAL,
                    new TimeValue(-1, TimeUnit.MILLISECONDS), Property.Dynamic,
                    Property.IndexScope);
    public Setting<ByteSizeValue> INDEX_TRANSLOG_FLUSH_THRESHOLD_SIZE_SETTING = Setting
            .byteSizeSetting("index.translog.flush_threshold_size",
                    new ByteSizeValue(512, ByteSizeUnit.MB), Property.Dynamic,
                    Property.IndexScope);

    public Setting<TimeValue> INDEX_SEQ_NO_CHECKPOINT_SYNC_INTERVAL = Setting
            .timeSetting("index.seq_no.checkpoint_sync_interval",
                    new TimeValue(30, TimeUnit.SECONDS), new TimeValue(-1,
                            TimeUnit.MILLISECONDS), Property.Dynamic,
                    Property.IndexScope);

    /**
     * Index setting to enable / disable deletes garbage collection. This
     * setting is realtime updateable
     */
    public TimeValue DEFAULT_GC_DELETES = TimeValue.timeValueSeconds(60);
    public Setting<TimeValue> INDEX_GC_DELETES_SETTING = Setting.timeSetting(
            "index.gc_deletes", DEFAULT_GC_DELETES, new TimeValue(-1,
                    TimeUnit.MILLISECONDS), Property.Dynamic,
            Property.IndexScope);
    /**
     * The maximum number of refresh listeners allows on this shard.
     */
    public Setting<Integer> MAX_REFRESH_LISTENERS_PER_SHARD = Setting
            .intSetting("index.max_refresh_listeners", 1000, 0,
                    Property.Dynamic, Property.IndexScope);

    /**
     * The maximum number of slices allowed in a scroll request
     */
    public Setting<Integer> MAX_SLICES_PER_SCROLL = Setting.intSetting(
            "index.max_slices_per_scroll", 1024, 1, Property.Dynamic,
            Property.IndexScope);

    public static void main(String[] args) {
        SettingTester tester = new SettingTester();
        System.out.println(tester);
    }

    @Override
    public String toString() {
        return "SettingTester [DEFAULT_FIELD_SETTING=" + DEFAULT_FIELD_SETTING
                + ", QUERY_STRING_LENIENT_SETTING="
                + QUERY_STRING_LENIENT_SETTING
                + ", QUERY_STRING_ANALYZE_WILDCARD="
                + QUERY_STRING_ANALYZE_WILDCARD
                + ", QUERY_STRING_ALLOW_LEADING_WILDCARD="
                + QUERY_STRING_ALLOW_LEADING_WILDCARD + ", ALLOW_UNMAPPED="
                + ALLOW_UNMAPPED + ", INDEX_TRANSLOG_SYNC_INTERVAL_SETTING="
                + INDEX_TRANSLOG_SYNC_INTERVAL_SETTING
                + ", INDEX_TRANSLOG_DURABILITY_SETTING="
                + INDEX_TRANSLOG_DURABILITY_SETTING
                + ", INDEX_WARMER_ENABLED_SETTING="
                + INDEX_WARMER_ENABLED_SETTING
                + ", INDEX_TTL_DISABLE_PURGE_SETTING="
                + INDEX_TTL_DISABLE_PURGE_SETTING + ", INDEX_CHECK_ON_STARTUP="
                + INDEX_CHECK_ON_STARTUP + ", MAX_RESULT_WINDOW_SETTING="
                + MAX_RESULT_WINDOW_SETTING + ", MAX_RESCORE_WINDOW_SETTING="
                + MAX_RESCORE_WINDOW_SETTING + ", DEFAULT_REFRESH_INTERVAL="
                + DEFAULT_REFRESH_INTERVAL
                + ", INDEX_REFRESH_INTERVAL_SETTING="
                + INDEX_REFRESH_INTERVAL_SETTING
                + ", INDEX_TRANSLOG_FLUSH_THRESHOLD_SIZE_SETTING="
                + INDEX_TRANSLOG_FLUSH_THRESHOLD_SIZE_SETTING
                + ", INDEX_SEQ_NO_CHECKPOINT_SYNC_INTERVAL="
                + INDEX_SEQ_NO_CHECKPOINT_SYNC_INTERVAL
                + ", DEFAULT_GC_DELETES=" + DEFAULT_GC_DELETES
                + ", INDEX_GC_DELETES_SETTING=" + INDEX_GC_DELETES_SETTING
                + ", MAX_REFRESH_LISTENERS_PER_SHARD="
                + MAX_REFRESH_LISTENERS_PER_SHARD + ", MAX_SLICES_PER_SCROLL="
                + MAX_SLICES_PER_SCROLL + "]";
    }

}
