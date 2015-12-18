/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.db.TransactionManager;
import org.bdlions.response.ResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class AdminAPIServer extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(AdminAPIServer.class);
    @Override
    public void start() {

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route("/").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            response.end("Admin API server");
        });

        router.route("/api*").handler(BodyHandler.create());
        router.post("/api").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            HttpServerRequest request = routingContext.request();
            
            
            response.end("Admin API server : param value is " + request.getParam("habijabi"));
        });
        // ------------------------------- Service Module ---------------------------------------//
        router.route("/createservice*").handler(BodyHandler.create());
        router.post("/createservice").handler((RoutingContext routingContext) -> {
            
        });
        router.route("/getallservices*").handler(BodyHandler.create());
        router.post("/getallservices").handler((RoutingContext routingContext) -> {
            
        });
        router.route("/getserviceinfo*").handler(BodyHandler.create());
        router.post("/getserviceinfo").handler((RoutingContext routingContext) -> {
            
        });
        router.route("/updateserviceinfo*").handler(BodyHandler.create());
        router.post("/updateserviceinfo").handler((RoutingContext routingContext) -> {
            
        });
        // ------------------------------- Subscriber Module -----------------------------------//
        router.route("/createsubscriber*").handler(BodyHandler.create());
        router.post("/createsubscriber").handler((RoutingContext routingContext) -> {
            
        });
        router.route("/getallsubscribers*").handler(BodyHandler.create());
        router.post("/getsubscribers").handler((RoutingContext routingContext) -> {
            
        });
        router.route("/getsubscriberinfo*").handler(BodyHandler.create());
        router.post("/getsubscriberinfo").handler((RoutingContext routingContext) -> {
            
        });
        router.route("/updatesubscriberinfo*").handler(BodyHandler.create());
        router.post("/updatesubscriberinfo").handler((RoutingContext routingContext) -> {
            
        });
        // ------------------------------- Subscriber Payment Module ----------------------------//
        /**
        * adding subscriber payment info
        * @param APIKey, API key of a service
        * @param amount, payment amount
        */
        router.route("/addsubscriberpayment*").handler(BodyHandler.create());
        router.post("/addsubscriberpayment").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();            
            String APIKey = routingContext.request().getParam("APIKey");
            String amount = routingContext.request().getParam("amount");
            
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setAPIKey(APIKey);
            try
            {
                transactionInfo.setBalanceIn(Long.parseLong(amount));
                
                TransactionManager transactionManager = new TransactionManager();
                transactionManager.addUserPayment(transactionInfo);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                if(responseCode == ResponseCodes.SUCCESS)
                {
                    transactionInfo.setTransactionId(transactionManager.getTransactionId());
                    resultEvent.setResult(transactionInfo);
                }
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
        });
        /**
        * returning subscriber payment list
        * @param subscriber user id
        * @param offset
        * @param limit
        */
        router.route("/getsubscriberpaymentlist*").handler(BodyHandler.create());
        router.post("/getsubscriberpaymentlist").handler((RoutingContext routingContext) -> {
            
        });
        // ------------------------------- Subscriber Payment Module ----------------------------//
        router.route("/getalltransactions*").handler(BodyHandler.create());
        router.post("/getalltransactions").handler((RoutingContext routingContext) -> {
            
        });
        server.requestHandler(router::accept).listen(2020);
    }
}
