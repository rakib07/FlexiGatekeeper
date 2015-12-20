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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.bdlions.bean.ServiceInfo;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.db.Database;
import org.bdlions.db.ServiceManager;
import org.bdlions.db.TransactionManager;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.response.ResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;

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
            ResultEvent resultEvent = new ResultEvent();
            String title = routingContext.request().getParam("title");
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setTitle(title);
            ServiceManager serviceManger = new ServiceManager();
            try {
                serviceManger.createService(serviceInfo);
                int responseCode = serviceManger.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                if (responseCode == ResponseCodes.SUCCESS) {
                    serviceInfo.setId(serviceManger.getServiceId());
                    resultEvent.setResult(serviceInfo);
                }
            } catch (Exception ex) {
                resultEvent.setResponseCode(serviceManger.getResponseCode());
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
//        router.route("/getallservices*").handler(BodyHandler.create());
        router.get("/getallservices").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    ServiceManager serviceManger = new ServiceManager();
                    List<ServiceInfo> serviceList = serviceManger.getAllServices();
                    int responseCode = serviceManger.getResponseCode();
                    serviceManger.setResponseCode(responseCode);
                    if (responseCode == ResponseCodes.SUCCESS) {
                        resultEvent.setResult(serviceList);
                    }
                }
            } catch (DBSetupException | SQLException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
        });

        router.route("/getserviceinfo*").handler(BodyHandler.create());
        router.post("/getserviceinfo").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
//            String service_id = routingContext.request().getParam("service_id");
            int service_id;
            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    if (!Strings.isNullOrEmpty(routingContext.request().getParam("service_id"))) {
                        try {
                            service_id = Integer.parseInt(routingContext.request().getParam("service_id"));
                            ServiceManager serviceManger = new ServiceManager();
                            ServiceInfo serviceInfo = serviceManger.getServiceInfo(service_id);
                            int responseCode = serviceManger.getResponseCode();
                            serviceManger.setResponseCode(responseCode);
                            if (responseCode == ResponseCodes.SUCCESS) {
                                resultEvent.setResult(serviceInfo);
                            }

                        } catch (NumberFormatException nfe) {
                            logger.debug(nfe.getMessage());
                        }
                    }

                }
            } catch (DBSetupException | SQLException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
        router.route("/updateserviceinfo*").handler(BodyHandler.create());
        router.post("/updateserviceinfo").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();

            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    ServiceInfo serviceInfo = new ServiceInfo();
                    if (!Strings.isNullOrEmpty(routingContext.request().getParam("service_id"))) {
                        try {
                            serviceInfo.setId(Integer.parseInt(routingContext.request().getParam("service_id")));

                        } catch (NumberFormatException nfe) {
                            logger.debug(nfe.getMessage());
                        }
                    }
                    serviceInfo.setTitle(routingContext.request().getParam("title"));
                    ServiceManager serviceManger = new ServiceManager();
                    serviceManger.updateServiceInfo(serviceInfo);
                    int responseCode = serviceManger.getResponseCode();
                    if (responseCode == ResponseCodes.SUCCESS) {
                        resultEvent.setResponseCode(ResponseCodes.SUCCESS);
                    }
                }
            } catch (DBSetupException | SQLException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

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
         *
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
            try {
                transactionInfo.setBalanceIn(Long.parseLong(amount));

                TransactionManager transactionManager = new TransactionManager();
                transactionManager.addUserPayment(transactionInfo);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                if (responseCode == ResponseCodes.SUCCESS) {
                    transactionInfo.setTransactionId(transactionManager.getTransactionId());
                    resultEvent.setResult(transactionInfo);
                }
            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
        });
        /**
         * returning subscriber payment list
         *
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
