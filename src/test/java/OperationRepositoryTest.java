import org.example.entity.OperationEntity;
import org.example.entity.OperationTypeEnum;
import org.example.repository.OperationRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OperationRepositoryTest {
    OperationRepository rep = new OperationRepository();

    @Test
    @Order(1)
    void testSave() throws Exception {
        OperationEntity op = new OperationEntity();
        op.setAccountNumber("PL00000000000000000000000001");
        op.setType(OperationTypeEnum.DEPOSIT);
        op.setAmount(new BigDecimal("100.00"));
        op.setTargetAccount(null);

        assertDoesNotThrow(() -> rep.save(op));
        System.out.println("Operation saved!");
    }

    @Test
    @Order(2)
    void testGetByAccountNumber() throws Exception {
        List<OperationEntity> ops = rep.getByAccountNumber("PL00000000000000000000000001");

        assertNotNull(ops);
        assertFalse(ops.isEmpty());
        System.out.println("Operations found: " + ops.size());
        ops.forEach(op -> System.out.println(op.getType() + " : " + op.getAmount()));
    }

    @Test
    @Order(3)
    void testGetByWrongAccountNumber() throws Exception {
        List<OperationEntity> ops = rep.getByAccountNumber("PL99999999999999999999999999");

        assertNotNull(ops);
        assertTrue(ops.isEmpty());
        System.out.println("The list is empty - everything is correct!");
    }
}
