package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.util.BasicLogger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private TenmoService accountService;
    public static Map<Long, String> allUsers;
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        accountService =new TenmoService(currentUser);
        allUsers = arrayToMap(accountService.listAllUsers());
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        String balance = "Current balance is: $" + accountService.viewCurrentBalance();
        consoleService.printMessage(balance);
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = accountService.listTransferHistory();
        if(transfers.length == 0){
            consoleService.printMessage("\nNo transfers to display.\n");
        }else {
            consoleService.listTransfers(transfers);
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        Transfer[] transfers = accountService.listPendingTransfers();
        if(transfers.length == 0){
            consoleService.printMessage("\nNo pending transfers to display.\n");
        } else {
            consoleService.listTransfers(transfers);
            int id = consoleService.promptForInt("Enter the transaction ID you'd like to resolve: ");
            boolean approve = 1 == consoleService.promptForInt("Enter 1 to approve, 0 to decline: ");
            try{
                Transfer transfer = accountService.resolvePendingTransfer(id, approve);
                consoleService.printTransfer(transfer);
            } catch (Exception e) {
                BasicLogger.log(e.getMessage());
                consoleService.printErrorMessage();
            }
        }
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		// First display users
        consoleService.listUsers(accountService.listOtherUsers());
        // Prompt for info
        int id = consoleService.promptForInt("Enter the user ID you'd like to send money to: ");
        BigDecimal amount = consoleService.promptForBigDecimal("Enter the amount you'd like to send: ");
        // Process transfer
        try {
            Transfer transfer = accountService.sendMoney(id, amount);
            consoleService.printMessage("\nTransfer completed: ");
            consoleService.printTransfer(transfer);
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
            consoleService.printErrorMessage();
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        consoleService.listUsers(accountService.listOtherUsers());
        int id = consoleService.promptForInt("Enter user ID you'd like to request money from: ");
        BigDecimal amount = consoleService.promptForBigDecimal("Enter the amount you'd like to request: ");
        try {
            Transfer transfer = accountService.requestMoney(id, amount);
            consoleService.printMessage("\nNew request sent: ");
            consoleService.printTransfer(transfer);
        } catch (Exception e) {
            BasicLogger.log(e.getMessage());
            consoleService.printErrorMessage();
        }
	}

    private Map <Long, String> arrayToMap (User[] users) {
        Map<Long, String> mapUsers = new HashMap<>();
        for (User user: users){
            mapUsers.put(user.getId(), user.getUsername());
        }
        return mapUsers;
    }

}
