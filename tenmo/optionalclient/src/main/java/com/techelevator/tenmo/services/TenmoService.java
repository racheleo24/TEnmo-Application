package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TenmoService {

    public static String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser user;

    public TenmoService(AuthenticatedUser user) {
        this.user = user;
    }

    public BigDecimal viewCurrentBalance(){
        ResponseEntity<BigDecimal> balance = null;
        try{
            balance = restTemplate.exchange(API_BASE_URL+"balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
        }  catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance.getBody();
    }

    public Transfer[] listTransferHistory(){
        ResponseEntity<Transfer[]> transferHistory = null;
        try {
            transferHistory = restTemplate.exchange(API_BASE_URL + "myTransfers", HttpMethod.GET,
                    makeAuthEntity(), Transfer[].class);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferHistory.getBody();
    }

    public Transfer[] listPendingTransfers() {
        ResponseEntity<Transfer[]> pendingTransfers = null;
        try {
            pendingTransfers = restTemplate.exchange(API_BASE_URL + "myTransfers/approve", HttpMethod.GET,
                    makeAuthEntity(), Transfer[].class);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return pendingTransfers.getBody();
    }

    public Transfer sendMoney(int id, BigDecimal amount) {
        Transfer newTransfer = new Transfer();
        newTransfer.setAmount(amount);
        newTransfer.setMoneyRecipientId(id);

        ResponseEntity<Transfer> transfer = null;
        try {
            transfer = restTemplate.exchange(API_BASE_URL + "transfer", HttpMethod.POST,
                    makeTransferEntity(newTransfer), Transfer.class);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer.getBody();
    }

    public Transfer requestMoney(int id, BigDecimal amount) {
        Transfer newTransfer = new Transfer();
        newTransfer.setMoneySenderId(id);
        newTransfer.setAmount(amount);

        ResponseEntity<Transfer> transfer = null;
        try {
            transfer = restTemplate.exchange(API_BASE_URL + "request", HttpMethod.POST,
                makeTransferEntity(newTransfer), Transfer.class);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer.getBody();
    }

    public Transfer resolvePendingTransfer(int id, boolean approve) {
        ResponseEntity<Transfer> transfer = null;
        ApprovalDTO approvalDTO = new ApprovalDTO(id, approve);
        try {
            transfer = restTemplate.exchange(API_BASE_URL + "myTransfers/approve", HttpMethod.POST,
                    makeApprovalDTOEntity(approvalDTO), Transfer.class);
        }catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer.getBody();
    }

    public User[] listOtherUsers() {
        ResponseEntity<User[]> users = null;
        try {
            users = restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET,
                    makeAuthEntity(), User[].class);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users.getBody();
    }

    public User[] listAllUsers() {
        ResponseEntity<User[]> users = null;
        try {
            users = restTemplate.exchange(API_BASE_URL + "users/all", HttpMethod.GET,
                    makeAuthEntity(), User[].class);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users.getBody();
    }


    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<Account>(account, headers);
    }

    private HttpEntity<Void> makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<Transfer>(transfer, headers);
    }

    private HttpEntity<ApprovalDTO> makeApprovalDTOEntity(ApprovalDTO approvalDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<ApprovalDTO>(approvalDTO, headers);
    }
}
