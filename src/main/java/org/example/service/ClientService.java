package org.example.service;

import org.example.entity.ClientEntity;
import org.example.entity.OperationEntity;
import org.example.entity.OperationTypeEnum;
import org.example.entity.RoleEnum;
import org.example.repository.ClientRepository;
import org.example.repository.OperationRepository;
import org.example.util.AccountGeneratorUtil;
import org.example.util.PasswordUtil;
import org.jooq.SQL;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ClientService {
    private final ClientRepository clientRep = new ClientRepository();
    private final OperationService operationSer = new OperationService();

    public ClientEntity login(String accountNumber, String password) throws SQLException {
        ClientEntity client = clientRep.getByAccountNumber(accountNumber);
        if (client == null || !client.getActive()) return null;
        if (!PasswordUtil.verify(password, client.getPasswordHash())) return null;
        return client;
    }

    public BigDecimal getBalance(String accountNumber) throws SQLException {
        ClientEntity client = clientRep.getByAccountNumber(accountNumber);
        return client != null ? client.getBalance() : null;
    }

    public boolean deposit(String accountNumber, BigDecimal amount) throws SQLException {
        ClientEntity client = clientRep.getByAccountNumber(accountNumber);
        if (client == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;

        clientRep.updateBalance(accountNumber, client.getBalance().add(amount));
        operationSer.save(accountNumber, OperationTypeEnum.DEPOSIT, amount, null);
        return true;
    }

    public boolean withdraw(String accountNumber, BigDecimal amount) throws SQLException {
        ClientEntity client = clientRep.getByAccountNumber(accountNumber);
        if (client == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        if (client.getBalance().compareTo(amount) < 0) return false;

        clientRep.updateBalance(accountNumber, client.getBalance().subtract(amount));
        operationSer.save(accountNumber, OperationTypeEnum.WITHDRAW, amount, null);
        return true;
    }

    public boolean transfer(String fromAccount, String toAccount, BigDecimal amount) throws SQLException {
        if (fromAccount.equals(toAccount) || amount.compareTo(BigDecimal.ZERO) <= 0) return false;

        ClientEntity sender = clientRep.getByAccountNumber(fromAccount);
        ClientEntity receiver = clientRep.getByAccountNumber(toAccount);
        if (sender == null || receiver == null || !sender.getActive()) return false;
        if (sender.getBalance().compareTo(amount) < 0) return false;

        clientRep.updateBalance(fromAccount, sender.getBalance().subtract(amount));
        clientRep.updateBalance(toAccount, receiver.getBalance().add(amount));
        operationSer.save(fromAccount, OperationTypeEnum.TRANSFER_OUT, amount, toAccount);
        operationSer.save(toAccount, OperationTypeEnum.TRANSFER_IN, amount, fromAccount);
        return true;
    }

    public List<OperationEntity> getLogs(String accountNumber) throws SQLException {
        return operationSer.getLogs(accountNumber);
    }

    public boolean registerClient(String firstName, String lastName, String pesel, String password) throws SQLException {
        if (clientRep.getByPesel(pesel) != null) return false;

        ClientEntity client = new ClientEntity();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setPesel(pesel);
        client.setAccountNumber(AccountGeneratorUtil.generate());
        client.setPasswordHash(PasswordUtil.hash(password));
        client.setBalance(BigDecimal.ZERO);
        client.setRole(RoleEnum.CLIENT);
        client.setActive(true);

        clientRep.save(client);
        return true;
    }

    public boolean updateClient(String accountNumber, String firstName, String lastName, String pesel) throws SQLException {
        ClientEntity client = clientRep.getByAccountNumber(accountNumber);
        if (client == null) return false;

        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setPesel(pesel);
        clientRep.updateById(client);
        return true;
    }

    public boolean deactivateClient(String accountNumber) throws SQLException {
        ClientEntity client = clientRep.getByAccountNumber(accountNumber);
        if (client == null) return false;

        clientRep.deleteById(accountNumber);
        return true;
    }
}
