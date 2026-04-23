package com.trade.sales.config;

import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.trade.sales.features")
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
                .host("sales-mysql") // Nombre del servicio en docker-compose
                .port(3306)
                .user("root")
                .password("root")
                .database("sales_db")
                .build());
    }

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new ByteBufferToUUIDConverter());
        converters.add(new UUIDToByteBufferConverter());

        // MySqlDialect.INSTANCE es específico para el driver que estás usando (asyncer)
        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE, converters);
    }

    // 1. Convertidor de BASE DE DATOS (Bytes) -> JAVA (UUID)
    @ReadingConverter
    public static class ByteBufferToUUIDConverter implements Converter<ByteBuffer, UUID> {
        @Override
        public UUID convert(ByteBuffer source) {
            return new UUID(source.getLong(), source.getLong());
        }
    }

    // 2. Convertidor de JAVA (UUID) -> BASE DE DATOS (Bytes)
    @WritingConverter
    public static class UUIDToByteBufferConverter implements Converter<UUID, ByteBuffer> {
        @Override
        public ByteBuffer convert(UUID source) {
            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(source.getMostSignificantBits());
            bb.putLong(source.getLeastSignificantBits());
            bb.flip();
            return bb;
        }
    }
}
