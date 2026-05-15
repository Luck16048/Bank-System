import org.example.entity.ClientEntity;
import org.example.service.ClientService;
import org.example.util.PasswordUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientServiceTest {

    ClientService service = new ClientService();

    @Test
    @Order(1)
    void testLoginSuccess() throws Exception {
        ClientEntity client = service.login("PL00000000000000000000000001", "password123");
        assertNotNull(client);
        assertEquals("Jan", client.getFirstName());
    }

    @Test
    @Order(2)
    void testLoginWrongPassword() throws Exception {
        ClientEntity client = service.login("PL00000000000000000000000001", "wrongpassword");
        assertNull(client);
    }

    @Test
    @Order(3)
    void testDeposit() throws Exception {
        boolean result = service.deposit("PL00000000000000000000000001", new BigDecimal("100"));
        assertTrue(result);
    }

    @Test
    @Order(4)
    void testWithdrawNotEnoughMoney() throws Exception {
        boolean result = service.withdraw("PL00000000000000000000000001", new BigDecimal("999999"));
        assertFalse(result);
    }

    @Test
    @Order(5)
    void testTransfer() throws Exception {
        boolean result = service.transfer("PL00000000000000000000000001",
                "PL00000000000000000000000002",
                new BigDecimal("100"));
        assertTrue(result);
    }

    @Test
    @Order(6)
    void testTranserToSameAccount() throws Exception {
        boolean result = service.transfer("PL00000000000000000000000001",
                "PL00000000000000000000000001",
                new BigDecimal("100"));
        assertFalse(result);
    }
}
