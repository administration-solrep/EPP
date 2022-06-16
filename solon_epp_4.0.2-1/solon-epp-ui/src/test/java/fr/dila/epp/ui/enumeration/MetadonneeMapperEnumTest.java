package fr.dila.epp.ui.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum;
import java.util.stream.Stream;
import org.junit.Test;

public class MetadonneeMapperEnumTest {

    @Test
    public void enumShouldHaveFieldWithSameName() {
        assertThat(Stream.of(MetadonneeMapperEnum.values()))
            .allSatisfy(enumMapper -> assertThat(enumMapper.name()).isEqualTo(enumMapper.getField().name()));
    }

    @Test
    public void allCommunicationFieldShouldHaveMapper() {
        assertThat(Stream.of(CommunicationMetadonneeEnum.values()))
            .allSatisfy(field -> assertThat(MetadonneeMapperEnum.getMapperFromCommunicationField(field)).isNotNull());
    }

    @Test
    public void getFromFieldShouldReturnNull() {
        assertThat(MetadonneeMapperEnum.getMapperFromCommunicationField(null)).isNull();
    }
}
