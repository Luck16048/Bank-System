package org.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.entity.ClientEntity;
import org.example.entity.OperationEntity;
import org.example.entity.RoleEnum;
import org.example.service.ClientService;
import org.jooq.SQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;


public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ClientService clientService = new ClientService();
    private final Gson gson = new Gson();
    private ClientEntity loggedClient = null;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                String response = handleRequest(line);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Connection error " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String handleRequest(String json) {
        try {
            JsonObject request = gson.fromJson(json, JsonObject.class);
            String action = request.get("action").getAsString();

            return switch (action) {
                case "login" -> handleRequest(String.valueOf(request));
                case "balance" -> handleBalance();
                case "deposit" -> handleDeposit(request);
                case "withdraw" -> handleWithdraw(request);
                case "transfer" -> handleTransfer(request);
                case "logs" -> handleLogs();
                case "register" -> handleRegister(request);
                case "update" -> handleUpdate(request);
                case "deactivate" -> handleDeactivate(request);
                default -> error("Unknown command");
            };
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    private String handleLogin(JsonObject req) throws SQLException {
        String accountNumber = req.get("accountNumber").getAsString();
        String password = req.get("password").getAsString();
        loggedClient = clientService.login(accountNumber, password);
        if (loggedClient == null) return error("Wrong login or password");
        return success("Login successful! Role: " + loggedClient.getRole());
    }

    private String handleBalance() throws SQLException {
        if (loggedClient == null) return error("Not authorized");
        BigDecimal balance = clientService.getBalance(loggedClient.getAccountNumber());
        return success("Balance: " + balance + " PLN");
    }

    private String handleDeposit(JsonObject req) throws SQLException {
        if (loggedClient == null) return error("Not authorized");
        BigDecimal amount = req.get("amount").getAsBigDecimal();
        boolean result = clientService.deposit(loggedClient.getAccountNumber(), amount);
        return result ? success("Deposited: " + amount + " PLN") : error("Deposit error");
    }

    private String handleWithdraw(JsonObject req) throws SQLException {
        if (loggedClient == null) return error("Not authorized");
        BigDecimal amount = req.get("amount").getAsBigDecimal();
        boolean result = clientService.withdraw(loggedClient.getAccountNumber(), amount);
        return result ? success("Withdraw: " + amount + " PLN") : error("Not enough funds");
    }

    private String handleTransfer(JsonObject req) throws SQLException {
        if (loggedClient == null) return error("Not authorized");
        String toAccount = req.get("toAccount").getAsString();
        BigDecimal amount = req.get("amount").getAsBigDecimal();
        boolean result = clientService.transfer(loggedClient.getAccountNumber(), toAccount, amount);
        return result ? success("Transferred: " + amount + " PLN") : error("Transfer error");
    }

    private String handleLogs() throws SQLException {
        if (loggedClient == null) return error("Not authorized");
        List<OperationEntity> logs = clientService.getLogs(loggedClient.getAccountNumber());
        return success(gson.toJson(logs));
    }

    private String handleRegister(JsonObject req) throws SQLException {
        if (isNotBanker()) return error("Access denied");
        String firstName = req.get("firstName").getAsString();
        String lastName = req.get("lastName").getAsString();
        String pesel = req.get("pesel").getAsString();
        String password = req.get("password").getAsString();
        boolean result = clientService.registerClient(firstName, lastName, pesel, password);
        return result ? success("Client registered") : error("PESEL already exists");
    }

    private String handleUpdate(JsonObject req) throws SQLException {
        if (isNotBanker()) return error("Access denied");
        String accountNumber = req.get("accountNumber").getAsString();
        String firstName = req.get("firstName").getAsString();
        String lastName = req.get("lastName").getAsString();
        String pesel = req.get("pesel").getAsString();
        boolean result = clientService.updateClient(accountNumber, firstName, lastName, pesel);
        return result ? success("Data updated") : error("Client not found");
    }

    private String handleDeactivate(JsonObject req) throws SQLException {
        if (isNotBanker()) return error("Access denied");
        String accountNumber = req.get("ccountNumber").getAsString();
        boolean result = clientService.deactivateClient(accountNumber);
        return result ? success("Client deactivated") : error("Client not found");
    }

    private boolean isNotBanker() {
        return loggedClient == null || loggedClient.getRole() != RoleEnum.BANKER;
    }

    private String success(String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", "ok");
        res.addProperty("message", message);
        return gson.toJson(res);
    }

    private String error(String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", "error");
        res.addProperty("message", message);
        return gson.toJson(res);
    }
}
