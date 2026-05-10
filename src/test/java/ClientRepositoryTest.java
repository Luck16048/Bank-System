import org.example.entity.ClientEntity;
import org.example.repository.ClientRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryTest {
    ClientRepository rep = new ClientRepository();

    @Test
    void testGetByAccountNumber() throws Exception {
        ClientEntity client = rep.getByAccountNumber("PL00000000000000000000000001");
        assertNotNull(client);
        System.out.println("Founded: " + client.getFirstName() + " " + client.getLastName());
    }

    @Test
    void testGetByWrongAccount() throws Exception {
        ClientEntity client = rep.getByAccountNumber("PL99999999999999999999999999");
        assertNull(client);
    }

    @Test
    void testExistsByAccountNumber() throws Exception {
        boolean exists = rep.existsByAccountNumber("PL00000000000000000000000001");
        assertTrue(exists);
    }
}
