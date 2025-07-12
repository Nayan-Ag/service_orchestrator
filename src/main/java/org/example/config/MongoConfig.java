package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * - MongoDB configuration class for customizing Mongo behavior.
 *
 * <p>This configuration includes:
 * <ul>
 *     <li>Enabling Mongo auditing (e.g., @CreatedDate, @LastModifiedDate)</li>
 *     <li>Removing the default {@code _class} field from MongoDB documents</li>
 * </ul>
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    /**
     * Provides a custom {@link MappingMongoConverter} that:
     * <ul>
     *     <li>Applies custom conversions if defined</li>
     *     <li>Removes the {@code _class} field from MongoDB documents for cleaner storage</li>
     * </ul>
     *
     * @param factory      MongoDB database factory
     * @param context      Mongo mapping context
     * @param conversions  Custom MongoDB conversions
     * @return a customized {@link MappingMongoConverter}
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory factory,
            MongoMappingContext context,
            MongoCustomConversions conversions
    ) {
        MappingMongoConverter converter = new MappingMongoConverter(factory, context);
        converter.setCustomConversions(conversions);
        MongoTypeMapper typeMapper = new DefaultMongoTypeMapper(null);
        converter.setTypeMapper(typeMapper);

        return converter;
    }
}
