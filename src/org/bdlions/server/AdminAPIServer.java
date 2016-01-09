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
import org.bdlions.bean.UserInfo;
import org.bdlions.db.PaymentManager;
import org.bdlions.db.SubscriberManager;

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
            ResultEvent resultEvent = new ResultEvent();
            String subScriberInfo = routingContext.request().getParam("subscriber_info");
            UserInfo userInfo = UserInfo.getServiceInfo(subScriberInfo);
            SubscriberManager subscriberManger = new SubscriberManager();
            try {
                subscriberManger.createSubscriber(userInfo);
                int responseCode = subscriberManger.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            } catch (Exception ex) {
                resultEvent.setResponseCode(subscriberManger.getResponseCode());
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
        router.get("/getallsubscribers").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    SubscriberManager subscriberManger = new SubscriberManager();
                    List<UserInfo> subscriberList = subscriberManger.getAllSubscribers();
                    int responseCode = subscriberManger.getResponseCode();
                    subscriberManger.setResponseCode(responseCode);
                    if (responseCode == ResponseCodes.SUCCESS) {
                        resultEvent.setResult(subscriberList);
                    }
                }
            } catch (DBSetupException | SQLException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
        router.route("/getsubscriberinfo*").handler(BodyHandler.create());
        router.post("/getsubscriberinfo").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String userId = routingContext.request().getParam("subscriber_id");
            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    SubscriberManager subscriberManger = new SubscriberManager();
                    UserInfo subscriberInfo = subscriberManger.getSubscriberInfo(userId);
                    int responseCode = subscriberManger.getResponseCode();
                    subscriberManger.setResponseCode(responseCode);
                    if (responseCode == ResponseCodes.SUCCESS) {
                        resultEvent.setResult(subscriberInfo);
                    }
                }
            } catch (DBSetupException | SQLException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
        });

        router.route("/updatesubscriberinfo*").handler(BodyHandler.create());
        router.post("/updatesubscriberinfo").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String subScriberInfo = routingContext.request().getParam("subscriber_info");
            UserInfo userInfo = UserInfo.getServiceInfo(subScriberInfo);
            SubscriberManager subscriberManger = new SubscriberManager();
            try {
                subscriberManger.updateSubscriber(userInfo);
                int responseCode = subscriberManger.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            } catch (Exception ex) {
                resultEvent.setResponseCode(subscriberManger.getResponseCode());
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

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
            ResultEvent resultEvent = new ResultEvent();
            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    PaymentManager paymentManger = new PaymentManager();
                    paymentManger.getSubscriberPaymentList();
                    int responseCode = paymentManger.getResponseCode();
                    paymentManger.setResponseCode(responseCode);
                    if (responseCode == ResponseCodes.SUCCESS) {
//                        resultEvent.setResult();
                    }
                }
            } catch (DBSetupException | SQLException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
        // ------------------------------- Subscriber Payment Module ----------------------------//
        router.route("/getalltransactions*").handler(BodyHandler.create());
        router.post("/getalltransactions").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String userId = routingContext.request().getParam("user_id");
            String offset = routingContext.request().getParam("offset");
            String limit = routingContext.request().getParam("limit");

            try {
                TransactionManager transactionManager = new TransactionManager();
                List<TransactionInfo> transactionList = transactionManager.getAllTransactions();
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                if (responseCode == ResponseCodes.SUCCESS) {
                    resultEvent.setResult(transactionList);
                }
            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
        router.route("/gettransctionInfo*").handler(BodyHandler.create());
        router.post("/gettransctionInfo").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String transctionId = routingContext.request().getParam("transction_id");
            try {
                TransactionManager transactionManager = new TransactionManager();
                TransactionInfo transactionInfo = transactionManager.getTransactionInfo(transctionId);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                if (responseCode == ResponseCodes.SUCCESS) {
                    resultEvent.setResult(transactionInfo);
                }
            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
        router.route("/createtransction*").handler(BodyHandler.create());
        router.post("/createtransction").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String transctionInfo = routingContext.request().getParam("transction_info");
            TransactionInfo transactionInfo = TransactionInfo.getTransctionInfo(transctionInfo);
            try {
                TransactionManager transactionManager = new TransactionManager();
                transactionManager.addTransaction(transactionInfo);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });
        router.route("/updatetransctionInfo*").handler(BodyHandler.create());
        router.post("/updatetransctionInfo").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String transctionInfo = routingContext.request().getParam("transction_info");
            TransactionInfo transactionInfo = TransactionInfo.getTransctionInfo(transctionInfo);
            try {
                TransactionManager transactionManager = new TransactionManager();
                transactionManager.updateTransaction(transactionInfo);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });

        router.route("/deletetransctionInfo*").handler(BodyHandler.create());
        router.post("/deletetransctionInfo").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String transctionId = routingContext.request().getParam("transction_id");
            try {
                TransactionManager transactionManager = new TransactionManager();
                transactionManager.deleteTransaction(transctionId);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());

        });

        server.requestHandler(router::accept).listen(2020);
    }
}
