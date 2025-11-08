package fastcampus.ecommerce.batch.util;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import fastcampus.ecommerce.batch.util.ReflectionUtils;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReflectionUtilsTest {

    private static class TestClass {

        private String stringField;
        private int intField;
        private static final String CONSTANT = "constant";
    }


    @Test
    void testGetFieldNames() {
        List<String> fieldNames = ReflectionUtils.getFieldNames(TestClass.class);
        assertAll(
            () -> assertThat(fieldNames).hasSize(2).containsExactly("stringField", "intField")
                .doesNotContain("CONSTANT"));
    }
}
