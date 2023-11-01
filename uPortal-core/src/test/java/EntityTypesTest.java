import org.apereo.portal.EntityTypes;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EntityTypesTest {

    @InjectMocks
    private EntityTypes entityTypes;

    @Mock
    private JdbcOperations jdbcOperations;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Ignore
    @Test
    public void testGetAllEntityTypes() {

        // Mock data for the database query
        List<Class<?>> classes = Arrays.asList(
            getClassFromName("com.example.EntityA"),
            getClassFromName("com.example.EntityB"),
            null,
            getClassFromName("com.example.EntityC")
        );

        // Mock data for the database query

        when(jdbcOperations.query("SELECT ENTITY_TYPE_NAME FROM UP_ENTITY_TYPE", EntityTypes.CLASS_ROW_MAPPER))
            .thenReturn(classes);

        // Call the method under test
        Iterator result = entityTypes.getAllEntityTypes();

        // Validate the result
        int count = 0;
        while (result.hasNext()){
            Class type = (Class) result.next();
            if (type != null) {
                count++;
            }
        }
        System.out.println(count);
        // Verify that the expected number of non-null classes are returned
        assertEquals(2, count);
    }

    // Helper method to create Class objects from class names
    private Class<?> getClassFromName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
