/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ContextParser;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.adjacency.AdjacencyMatrixAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.adjacency.ParsedAdjacencyMatrix;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilters;
import org.elasticsearch.search.aggregations.bucket.geogrid.ParsedGeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.ParsedGlobal;
import org.elasticsearch.search.aggregations.bucket.histogram.AutoDateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedAutoDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedHistogram;
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.ParsedMissing;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedReverseNested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.GeoDistanceAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ParsedBinaryRange;
import org.elasticsearch.search.aggregations.bucket.range.ParsedDateRange;
import org.elasticsearch.search.aggregations.bucket.range.ParsedGeoDistance;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.sampler.InternalSampler;
import org.elasticsearch.search.aggregations.bucket.sampler.ParsedSampler;
import org.elasticsearch.search.aggregations.bucket.significant.ParsedSignificantLongTerms;
import org.elasticsearch.search.aggregations.bucket.significant.ParsedSignificantStringTerms;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantLongTerms;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBoundsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.geobounds.ParsedGeoBounds;
import org.elasticsearch.search.aggregations.metrics.geocentroid.GeoCentroidAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.geocentroid.ParsedGeoCentroid;
import org.elasticsearch.search.aggregations.metrics.mad.MedianAbsoluteDeviationAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.mad.ParsedMedianAbsoluteDeviation;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.InternalHDRPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.InternalHDRPercentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.ParsedHDRPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.ParsedHDRPercentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.tdigest.InternalTDigestPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.percentiles.tdigest.InternalTDigestPercentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.tdigest.ParsedTDigestPercentileRanks;
import org.elasticsearch.search.aggregations.metrics.percentiles.tdigest.ParsedTDigestPercentiles;
import org.elasticsearch.search.aggregations.metrics.scripted.ParsedScriptedMetric;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.ParsedStats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ParsedExtendedStats;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.weighted_avg.ParsedWeightedAvg;
import org.elasticsearch.search.aggregations.metrics.weighted_avg.WeightedAvgAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.*;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.InternalBucketMetricValue;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.ParsedBucketMetricValue;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile.ParsedPercentilesBucket;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile.PercentilesBucketPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.ParsedStatsBucket;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.StatsBucketPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.extended.ExtendedStatsBucketPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.extended.ParsedExtendedStatsBucket;
import org.elasticsearch.search.aggregations.pipeline.derivative.DerivativePipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.derivative.ParsedDerivative;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;

/**
 * <p>
 * Original implementation source {@link org.elasticsearch.client.RestHighLevelClient#getDefaultNamedXContents()} by
 * {@literal Elasticsearch} (<a href="https://www.elastic.co">https://www.elastic.co</a>) licensed under the Apache
 * License, Version 2.0. The latest version used from Elasticsearch is 7.10.2.
 * </p>
 * Modified for usage with {@link ReactiveElasticsearchClient}.
 * <p>
 * Only intended for internal use.
 *
 * @author Russell Parry
 * @since 4.0
 */
public class NamedXContents {

	private NamedXContents() {
		// contains only utility methods
	}

	public static List<NamedXContentRegistry.Entry> getDefaultNamedXContents() {
		Map<String, ContextParser<Object, ? extends Aggregation>> map = new HashMap();
		map.put("cardinality", (p, c) -> {
			return ParsedCardinality.fromXContent(p, (String)c);
		});
		map.put("hdr_percentiles", (p, c) -> {
			return ParsedHDRPercentiles.fromXContent(p, (String)c);
		});
		map.put("hdr_percentile_ranks", (p, c) -> {
			return ParsedHDRPercentileRanks.fromXContent(p, (String)c);
		});
		map.put("tdigest_percentiles", (p, c) -> {
			return ParsedTDigestPercentiles.fromXContent(p, (String)c);
		});
		map.put("tdigest_percentile_ranks", (p, c) -> {
			return ParsedTDigestPercentileRanks.fromXContent(p, (String)c);
		});
		map.put("percentiles_bucket", (p, c) -> {
			return ParsedPercentilesBucket.fromXContent(p, (String)c);
		});
		map.put("median_absolute_deviation", (p, c) -> {
			return ParsedMedianAbsoluteDeviation.fromXContent(p, (String)c);
		});
		map.put("min", (p, c) -> {
			return ParsedMin.fromXContent(p, (String)c);
		});
		map.put("max", (p, c) -> {
			return ParsedMax.fromXContent(p, (String)c);
		});
		map.put("sum", (p, c) -> {
			return ParsedSum.fromXContent(p, (String)c);
		});
		map.put("avg", (p, c) -> {
			return ParsedAvg.fromXContent(p, (String)c);
		});
		map.put("weighted_avg", (p, c) -> {
			return ParsedWeightedAvg.fromXContent(p, (String)c);
		});
		map.put("value_count", (p, c) -> {
			return ParsedValueCount.fromXContent(p, (String)c);
		});
		map.put("simple_value", (p, c) -> {
			return ParsedSimpleValue.fromXContent(p, (String)c);
		});
		map.put("derivative", (p, c) -> {
			return ParsedDerivative.fromXContent(p, (String)c);
		});
		map.put("bucket_metric_value", (p, c) -> {
			return ParsedBucketMetricValue.fromXContent(p, (String)c);
		});
		map.put("stats", (p, c) -> {
			return ParsedStats.fromXContent(p, (String)c);
		});
		map.put("stats_bucket", (p, c) -> {
			return ParsedStatsBucket.fromXContent(p, (String)c);
		});
		map.put("extended_stats", (p, c) -> {
			return ParsedExtendedStats.fromXContent(p, (String)c);
		});
		map.put("extended_stats_bucket", (p, c) -> {
			return ParsedExtendedStatsBucket.fromXContent(p, (String)c);
		});
		map.put("geo_bounds", (p, c) -> {
			return ParsedGeoBounds.fromXContent(p, (String)c);
		});
		map.put("geo_centroid", (p, c) -> {
			return ParsedGeoCentroid.fromXContent(p, (String)c);
		});
		map.put("histogram", (p, c) -> {
			return ParsedHistogram.fromXContent(p, (String)c);
		});
		map.put("date_histogram", (p, c) -> {
			return ParsedDateHistogram.fromXContent(p, (String)c);
		});
		map.put("auto_date_histogram", (p, c) -> {
			return ParsedAutoDateHistogram.fromXContent(p, (String)c);
		});
		map.put("sterms", (p, c) -> {
			return ParsedStringTerms.fromXContent(p, (String)c);
		});
		map.put("lterms", (p, c) -> {
			return ParsedLongTerms.fromXContent(p, (String)c);
		});
		map.put("dterms", (p, c) -> {
			return ParsedDoubleTerms.fromXContent(p, (String)c);
		});
		map.put("missing", (p, c) -> {
			return ParsedMissing.fromXContent(p, (String)c);
		});
		map.put("nested", (p, c) -> {
			return ParsedNested.fromXContent(p, (String)c);
		});
		map.put("reverse_nested", (p, c) -> {
			return ParsedReverseNested.fromXContent(p, (String)c);
		});
		map.put("global", (p, c) -> {
			return ParsedGlobal.fromXContent(p, (String)c);
		});
		map.put("filter", (p, c) -> {
			return ParsedFilter.fromXContent(p, (String)c);
		});
		map.put("sampler", (p, c) -> {
			return ParsedSampler.fromXContent(p, (String)c);
		});
		map.put("geohash_grid", (p, c) -> {
			return ParsedGeoHashGrid.fromXContent(p, (String)c);
		});
		map.put("range", (p, c) -> {
			return ParsedRange.fromXContent(p, (String)c);
		});
		map.put("date_range", (p, c) -> {
			return ParsedDateRange.fromXContent(p, (String)c);
		});
		map.put("geo_distance", (p, c) -> {
			return ParsedGeoDistance.fromXContent(p, (String)c);
		});
		map.put("filters", (p, c) -> {
			return ParsedFilters.fromXContent(p, (String)c);
		});
		map.put("adjacency_matrix", (p, c) -> {
			return ParsedAdjacencyMatrix.fromXContent(p, (String)c);
		});
		map.put("siglterms", (p, c) -> {
			return ParsedSignificantLongTerms.fromXContent(p, (String)c);
		});
		map.put("sigsterms", (p, c) -> {
			return ParsedSignificantStringTerms.fromXContent(p, (String)c);
		});
		map.put("scripted_metric", (p, c) -> {
			return ParsedScriptedMetric.fromXContent(p, (String)c);
		});
		map.put("ip_range", (p, c) -> {
			return ParsedBinaryRange.fromXContent(p, (String)c);
		});
		map.put("top_hits", (p, c) -> {
			return ParsedTopHits.fromXContent(p, (String)c);
		});
		map.put("composite", (p, c) -> {
			return ParsedComposite.fromXContent(p, (String)c);
		});
		List<NamedXContentRegistry.Entry> entries = (List)map.entrySet().stream().map((entry) -> {
			return new NamedXContentRegistry.Entry(Aggregation.class, new ParseField((String)entry.getKey(), new String[0]), (ContextParser)entry.getValue());
		}).collect(Collectors.toList());
		entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField("term", new String[0]), (parser, context) -> {
			return TermSuggestion.fromXContent(parser, (String)context);
		}));
		entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField("phrase", new String[0]), (parser, context) -> {
			return PhraseSuggestion.fromXContent(parser, (String)context);
		}));
		entries.add(new NamedXContentRegistry.Entry(Suggest.Suggestion.class, new ParseField("completion", new String[0]), (parser, context) -> {
			return CompletionSuggestion.fromXContent(parser, (String)context);
		}));
		return entries;
	}
}
