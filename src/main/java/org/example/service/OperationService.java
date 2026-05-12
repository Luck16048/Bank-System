package org.example.service;

import org.example.entity.OperationEntity;
import org.example.entity.OperationTypeEnum;
import org.example.repository.OperationRepository;
import org.jooq.impl.QOM;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class OperationService {
    private final OperationRepository operationRep = new OperationRepository();

    public void save(String accountNumber, OperationTypeEnum type, BigDecimal amount, String targetAccount) throws SQLException {
        OperationEntity op = new OperationEntity();
        op.setAccountNumber(accountNumber);
        op.setType(type);
        op.setAmount(amount);
        op.setTargetAccount(targetAccount);
        operationRep.save(op);
    }

    public List<OperationEntity> getLogs(String accountNumber) throws SQLException{
        return  operationRep.getByAccountNumber(accountNumber);
    }

}
