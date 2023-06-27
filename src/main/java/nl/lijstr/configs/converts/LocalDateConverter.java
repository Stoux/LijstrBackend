package nl.lijstr.configs.converts;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * A converter to support the Java 8 Date format: LocalDate.
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDate locDateTime) {
        return locDateTime == null ? null : Timestamp.valueOf(locDateTime.atStartOfDay());
    }

    @Override
    public LocalDate convertToEntityAttribute(Timestamp sqlTimestamp) {
        return sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime().toLocalDate();
    }

}
