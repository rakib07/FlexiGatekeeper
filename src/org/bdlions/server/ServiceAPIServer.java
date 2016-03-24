/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.server;

import com.fasterxml.jackson.databind.JsonSerializer;
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
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.db.TransactionManager;
import org.bdlions.response.ResultEvent;
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
            
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setAPIKey(APIKey);
            transactionInfo.setCellNumber(cellNumber);
            transactionInfo.setDescription(description);
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
              
            TransactionManager transactionManager = new TransactionManager();
            transactionManager.addTransaction(transactionInfo);
            int responseCode = transactionManager.getResponseCode();
            resultEvent.setResponseCode(responseCode);
            if(responseCode == ResponseCodes.SUCCESS)
            {
                transactionInfo.setTransactionId(transactionManager.getTransactionId());
                resultEvent.setResult(transactionInfo);
            }
            response.end(resultEvent.toString());
        });
        
        router.route("/addmultipletransactions*").handler(BodyHandler.create());
        router.post("/addmultipletransactions").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            
            List<TransactionInfo> transactionList = new ArrayList<>();
            TransactionInfo transactonInfo1 = new TransactionInfo();
            transactonInfo1.setId(1);
            transactonInfo1.setTransactionId("trnx1");
            
            TransactionInfo transactonInfo2 = new TransactionInfo();
            transactonInfo2.setId(2);
            transactonInfo2.setTransactionId("trnx2");
            
            TransactionInfo transactonInfo3 = new TransactionInfo();
            transactonInfo3.setId(3);
            transactonInfo3.setTransactionId("trnx3");
            
            transactionList.add(transactonInfo1);
            transactionList.add(transactonInfo2);
            transactionList.add(transactonInfo3);
            
            resultEvent.setResult(transactionList);
            resultEvent.setResponseCode(ResponseCodes.SUCCESS);
            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
        });
        
        router.route("/updatetransactionstatus*").handler(BodyHandler.create());
        router.post("/updatetransactionstatus").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String transactionId = routingContext.request().getParam("transactionid");
            String statusId = routingContext.request().getParam("statusid");
            String senderCellNumber = routingContext.request().getParam("sendercellnumber");
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
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
            
        });
        router.route("/sendsms*").handler(BodyHandler.create());
        router.post("/sendsms").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String cellList = routingContext.request().getParam("cell_list");
            String message = routingContext.request().getParam("message");
            System.out.println(cellList);
            JsonArray jsonArray = new JsonArray(cellList);
            JsonArray ja = new JsonArray();
            for(int counter = 0 ; counter < jsonArray.size(); counter++)
            {
                System.out.println(jsonArray.getValue(counter));
                
                JsonObject jsonObject = new JsonObject(jsonArray.getValue(counter).toString());
                String id = jsonObject.getString("id");
                String cellNo = jsonObject.getString("cell_no");
                System.out.println(id);
                System.out.println(cellNo);
                
                JsonObject jO = new JsonObject();
                jO.put("id", id);
                jO.put("cell_no", cellNo);
                
                ja.add(jO);
            }
            System.out.println(ja.toString());
            HttpServerResponse response = routingContext.response();
            resultEvent.setResult(ja.toString());
            response.end(resultEvent.toString());
            
        });
        
        server.requestHandler(router::accept).listen(3030);
    }
}
