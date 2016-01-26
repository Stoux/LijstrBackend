package nl.lijstr.configs.converts;

import java.sql.Timestamp;
import java.time.LocalDate;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
