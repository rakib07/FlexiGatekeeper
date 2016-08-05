/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.bean.SMSTransactionInfo;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Services;
import org.bdlions.constants.Transactions;
import org.bdlions.db.BufferManager;
import org.bdlions.db.SIMManager;
import org.bdlions.db.TransactionManager;
import org.bdlions.response.ResultEvent;
import org.bdlions.utility.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class ServiceAPIServer extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(ServiceAPIServer.class);
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            response.end("ServiceAPI server");
        });
        
        //router.route("/addtransaction").handler((RoutingContext routingContext) -> {
        router.route("/addtransaction*").handler(BodyHandler.create());
        router.post("/addtransaction").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            String userId = "";
            String sessionId = "";
            //validate userId and sessionId from the hashmap
            
            String APIKey = routingContext.request().getParam("APIKey");
            String amount = routingContext.request().getParam("amount");
            String cellNumber = routingContext.request().getParam("cell_no");
            String packageId = routingContext.request().getParam("package_id");
            String description = routingContext.request().getParam("description");
            String liveTestFlag = routingContext.request().getParam("livetestflag");
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setAPIKey(APIKey);
            transactionInfo.setCellNumber(cellNumber);
            transactionInfo.setDescription(description);
            transactionInfo.setLiveTestFlag(liveTestFlag);
            transactionInfo.setEditable(Boolean.TRUE);
            try
            {
                transactionInfo.setPackageId(Integer.parseInt(packageId));
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_OPERATOR_PACKAGE_ID);
                logger.error(ex.getMessage());
                response.end(resultEvent.toString());
                return;
            }
            try
            {
                transactionInfo.setBalanceOut(Double.parseDouble(amount));
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
                response.end(resultEvent.toString());
                return;
            }
              
            //TransactionManager transactionManager = new TransactionManager();
            //transactionManager.addTransaction(transactionInfo);
            
            BufferManager bufferManager = new BufferManager();
            bufferManager.processBuffer(transactionInfo, Transactions.BUFFER_PROCESS_TYPE_ADD_TRANSACTION);
            int responseCode = bufferManager.getTransactionManager().getResponseCode();
            
            resultEvent.setResponseCode(responseCode);
            if(responseCode == ResponseCodes.SUCCESS)
            {
                transactionInfo.setTransactionId(bufferManager.getTransactionManager().getTransactionId());
                resultEvent.setResult(transactionInfo);
            }
            response.end(resultEvent.toString());
        });
        
        router.route("/updatetransactioninfo*").handler(BodyHandler.create());
        router.post("/updatetransactioninfo").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            String transactionId = routingContext.request().getParam("transaction_id");
            String amount = routingContext.request().getParam("amount");
            String cellNumber = routingContext.request().getParam("cell_no");
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setTransactionId(transactionId);
            transactionInfo.setCellNumber(cellNumber);
            transactionInfo.setEditable(Boolean.TRUE);
            try
            {
                transactionInfo.setBalanceOut(Double.parseDouble(amount));
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
                response.end(resultEvent.toString());
                return;
            }
              
            
            BufferManager bufferManager = new BufferManager();
            bufferManager.processBuffer(transactionInfo, Transactions.BUFFER_PROCESS_TYPE_UPDATE_TRANSACTION);
            int responseCode = bufferManager.getTransactionManager().getResponseCode();
            resultEvent.setResponseCode(responseCode);
            if(responseCode == ResponseCodes.SUCCESS)
            {
                resultEvent.setResult(transactionInfo);
            }
            response.end(resultEvent.toString());
        });
        
        router.route("/addmultipletransactions*").handler(BodyHandler.create());
        router.post("/addmultipletransactions").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            HttpServerResponse response = routingContext.response();
            List<TransactionInfo> transactionInfoList = new ArrayList<>();
            String transactionList = routingContext.request().getParam("transction_list");
            String liveTestFlag = routingContext.request().getParam("livetestflag");
            JsonArray transactionArray = new JsonArray(transactionList);
            BufferManager bufferManager = new BufferManager();
            for(int counter = 0 ; counter < transactionArray.size(); counter++)
            {
                JsonObject jsonObject = new JsonObject(transactionArray.getValue(counter).toString());
                String id = jsonObject.getString("id"); 
                String cellNo = jsonObject.getString("cell_no"); 
                String APIKey = jsonObject.getString("APIKey"); 
                String packageId = jsonObject.getString("operator_type_id");
                String amount = jsonObject.getString("amount");
                
                TransactionInfo transactionInfo = new TransactionInfo();
                transactionInfo.setEditable(Boolean.TRUE);
                transactionInfo.setAPIKey(APIKey);
                try
                {
                    transactionInfo.setPackageId(Integer.parseInt(packageId));
                }
                catch(Exception ex)
                {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_OPERATOR_PACKAGE_ID);
                    logger.error(ex.getMessage());
                    response.end(resultEvent.toString());
                    return;
                }
                try
                {
                    transactionInfo.setBalanceOut(Double.parseDouble(amount));
                }
                catch(Exception ex)
                {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                    logger.error(ex.getMessage());
                    response.end(resultEvent.toString());
                    return;
                }
                transactionInfo.setLiveTestFlag(liveTestFlag);
                transactionInfo.setCellNumber(cellNo);
                transactionInfo.setReferenceId(id);
                //transactionInfo.setTransactionId(Utils.getTransactionId());
                
                //UserServiceInfo userServiceInfo = transactionManager.getUserServiceInfo(APIKey);
                //transactionInfo.setServiceId(userServiceInfo.getServiceId());
                
                //transactionManager.addTransaction(transactionInfo);
                bufferManager.processBuffer(transactionInfo, Transactions.BUFFER_PROCESS_TYPE_ADD_TRANSACTION);
                int responseCode = bufferManager.getTransactionManager().getResponseCode();
                if(responseCode == ResponseCodes.SUCCESS)
                {
                    transactionInfo.setTransactionId(bufferManager.getTransactionManager().getTransactionId());
                } 
                //what will you do if response code is not success?
                transactionInfoList.add(transactionInfo);
            }            
            resultEvent.setResult(transactionInfoList);
            resultEvent.setResponseCode(ResponseCodes.SUCCESS);            
            
            response.end(resultEvent.toString());
        });
        
        router.route("/updatetransactionstatus*").handler(BodyHandler.create());
        router.post("/updatetransactionstatus").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String transactionId = routingContext.request().getParam("transactionid");
            String statusId = routingContext.request().getParam("statusid");
            String senderCellNumber = routingContext.request().getParam("sendercellnumber");
            String balanceStr = routingContext.request().getParam("balance");
            double balance = 0;
            try
            {
                balance = Double.parseDouble(balanceStr);
            }
            catch(Exception ex)
            {
                logger.error(ex.getMessage());
            }
            
            if(!transactionId.equals(""))
            {
                try
                {
                    TransactionInfo transactionInfo = new TransactionInfo();
                    transactionInfo.setTransactionId(transactionId);
                    transactionInfo.setTransactionStatusId(Integer.parseInt(statusId));
                    transactionInfo.setSenderCellNumber(senderCellNumber);

                    TransactionManager transactionManager = new TransactionManager();
                    transactionManager.updateTransactionStatus(transactionInfo);

                    int responseCode = transactionManager.getResponseCode();
                    resultEvent.setResponseCode(responseCode);  
                }
                catch(Exception ex)
                {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_UPDATE_TRANSACTION_STATUS_FAILED);
                    logger.error(ex.getMessage());
                }
            }
            else
            {
                //before updating sim balance check different parameters.
                //right now it is assumed that we are upting bkash sim balance only
                try
                {
                    SIMManager simManager = new SIMManager();
                    SIMInfo simInfo = new SIMInfo();
                    simInfo.setSimNo(senderCellNumber);
                    SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                    simServiceInfo.setCurrentBalance(balance);
                    simServiceInfo.setId(Services.SIM_SERVICE_TYPE_ID_BKASH);
                    simInfo.getSimServiceList().add(simServiceInfo);
                    simManager.updateSIMServiceBalanceInfo(simInfo);

                    int responseCode = simManager.getResponseCode();
                    resultEvent.setResponseCode(responseCode); 
                }
                catch(Exception ex)
                {
                    logger.error(ex.getMessage());
                }
            }
            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
            
        });
        /**
         * post method to send bulk sms
         * @param cellnumberlist, cell number list
         * @param sms, sms body
         * @param APIKey, APIKey
         * @param livetestflag, flag of transaction
        */
        router.route("/sendsms*").handler(BodyHandler.create());
        router.post("/sendsms").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            SMSTransactionInfo smsTransactionInfo = new SMSTransactionInfo();
            String cellNumberList = routingContext.request().getParam("cellnumberlist");
            String sms = routingContext.request().getParam("sms");
            String APIKey = routingContext.request().getParam("APIKey");
            String liveTestFlag = routingContext.request().getParam("livetestflag");
            smsTransactionInfo.setLiveTestFlag(liveTestFlag);
            smsTransactionInfo.setSms(sms);
            smsTransactionInfo.setAPIKey(APIKey);
            JsonArray cellNumberArray = new JsonArray(cellNumberList);
            //JsonArray ja = new JsonArray();
            for(int counter = 0 ; counter < cellNumberArray.size(); counter++)
            {
                JsonObject jsonObject = new JsonObject(cellNumberArray.getValue(counter).toString());
                String id = jsonObject.getString("id");
                String cellNo = jsonObject.getString("cell_no");                
                smsTransactionInfo.getCellNumberList().add(cellNo);
                
                
                //System.out.println(id);
                //System.out.println(cellNo);
                
                //JsonObject jO = new JsonObject();
                //jO.put("id", id);
                //jO.put("cell_no", cellNo);
                
                //ja.add(jO);
            }
            try
            {
                TransactionManager transactionManager = new TransactionManager();
                transactionManager.addSMSTransaction(smsTransactionInfo);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                if(responseCode == ResponseCodes.SUCCESS)
                {
                    smsTransactionInfo.setTransactionId(transactionManager.getTransactionId());
                    resultEvent.setResult(smsTransactionInfo);
                }
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_WEBSERVICE_PROCESS_EXCEPTION);
                logger.error(ex.toString());
            }
            response.end(resultEvent.toString());            
        });
        
        router.route("/sendemail*").handler(BodyHandler.create());
        router.post("/sendemail").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            String receiverEmail = routingContext.request().getParam("email");
            String message = routingContext.request().getParam("message");
            Email email = new Email();
            email.sendEmail(receiverEmail, message);
            response.end(resultEvent.toString());            
        });
        
        server.requestHandler(router::accept).listen(3030);
    }
}
