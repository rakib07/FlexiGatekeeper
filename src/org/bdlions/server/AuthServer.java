/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Services;
import org.bdlions.db.AuthManager;
import org.bdlions.db.Database;
import org.bdlions.db.TransactionManager;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.exceptions.MaxMemberRegException;
import org.bdlions.exceptions.ServiceExpireException;
import org.bdlions.exceptions.SubscriptionExpireException;
import org.bdlions.exceptions.UnRegisterIPException;
import org.bdlions.response.ResultEvent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class AuthServer extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(AuthServer.class);

    @Override
    public void start() {

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route("/").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            response.end("Authentication server");
        });

        router.route("/registersubscriber*").handler(BodyHandler.create());
        router.post("/registersubscriber").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    String userName = routingContext.request().getParam("username");
                    String maxMembers = routingContext.request().getParam("maxmembers");
                    String APIKey = routingContext.request().getParam("apikey");

                    try {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setReferenceUserName(userName);
                        userInfo.setMaxMembers(Integer.parseInt(maxMembers));
                        userInfo.setRegistrationDate(1449141369);
                        //right now we are setting a default expired time
                        userInfo.setExpiredDate(2140000000);
                        //right now are not restricting/validating ip address
                        userInfo.setIpAddress("192.168.1.30");

                        UserServiceInfo userServiceInfo = new UserServiceInfo();
                        userServiceInfo.setServiceId(Services.SERVICE_TYPE_ID_BKASH_CASHIN);
                        userServiceInfo.setAPIKey(APIKey);
                        userServiceInfo.setRegistrationDate(1449141369);
                        userServiceInfo.setExpiredDate(2140000000);
                        //right now we have not implement call back function
                        userServiceInfo.setCallbackFunction("callback30");
                        List<UserServiceInfo> userServiceInfoList = new ArrayList<>();
                        userServiceInfoList.add(userServiceInfo);

                        AuthManager authManager = new AuthManager();
                        authManager.createSubscriber(userInfo, userServiceInfoList);
                        int responseCode = authManager.getResponseCode();
                        resultEvent.setResponseCode(responseCode);
                    } catch (Exception ex) {
                        resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_MEMBER_COUNTER);
                        logger.error(ex.getMessage());
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
            try {
                Database db = Database.getInstance();
                Connection connection = db.getConnection();
                if (connection == null) {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                } else {
                    String userName = routingContext.request().getParam("username");
                    try {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setSubscriberReferenceUserName(userName);

                        AuthManager authManager = new AuthManager();
                        userInfo = authManager.getSubscriberInfo(userInfo);
                        int responseCode = authManager.getResponseCode();
                        resultEvent.setResponseCode(responseCode);
                        if (responseCode == ResponseCodes.SUCCESS) {
                            resultEvent.setResult(userInfo);
                        }
                    } catch (Exception ex) {
                        resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_MEMBER_COUNTER);
                        logger.error(ex.getMessage());
                    }
                }
            } catch (DBSetupException | SQLException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
        });

        router.route("/registermember*").handler(BodyHandler.create());
        router.post("/registermember").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String userName = routingContext.request().getParam("username");
            String subscriberName = routingContext.request().getParam("subscribername");

            UserInfo userInfo = new UserInfo();
            userInfo.setReferenceUserName(userName);
            userInfo.setSubscriberReferenceUserName(subscriberName);
            //right now are not restricting/validating ip address
            userInfo.setIpAddress("192.168.1.30");

            try {
                AuthManager authManager = new AuthManager();
                authManager.createUser(userInfo);
                int responseCode = authManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            } catch (UnRegisterIPException ex) {
                //Right now we are skipping ipaddress validation
            } catch (SubscriptionExpireException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_SUBSCRIPTION_PERIOD_EXPIRED);
                logger.error(ex.getMessage());
            } catch (MaxMemberRegException ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_MAXINUM_MEMBERS_CREATION_REACHED);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
        });

        router.route("/getsessioninfo").handler((RoutingContext routingContext) -> {
            String result = "";
            UserInfo userInfo = new UserInfo();
            userInfo.setReferenceUserName("ru31");
            userInfo.setIpAddress("192.168.1.30");

            try {
                AuthManager authManager = new AuthManager();
                result = authManager.getSessionInfo(userInfo, "64hedl981o0suvld9r79kklta2");
            } catch (SubscriptionExpireException | ServiceExpireException ex) {

            }
            HttpServerResponse response = routingContext.response();
            response.end(result);
        });

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

        router.route("/getdeshboardinfolist*").handler(BodyHandler.create());
        router.post("/getdeshboardinfolist").handler((RoutingContext routingContext) -> {
            JSONObject resultInfos = new JSONObject();
            ResultEvent resultEvent = new ResultEvent();
            String startUnixTime = routingContext.request().getParam("start_time");
            String endUnixTime = routingContext.request().getParam("end_time");
            try {
                //generate summary info
                JSONObject summaryInfo = new JSONObject();
                summaryInfo.put("total_amount", 600000);
                summaryInfo.put("total_profit", 50000);
                summaryInfo.put("total_subscriber", 50);

                //generate survice rank list by volumn
                // return service rank list by acending order by volumn
                JSONObject serviceRankInfoByVolumn1 = new JSONObject();
                serviceRankInfoByVolumn1.put("service_id", "101");
                serviceRankInfoByVolumn1.put("total_amount", 30000);
                serviceRankInfoByVolumn1.put("total_profit", 5000);
                JSONObject serviceRankInfoByVolumn2 = new JSONObject();
                serviceRankInfoByVolumn2.put("service_id", "1");
                serviceRankInfoByVolumn2.put("total_amount", 20000);
                serviceRankInfoByVolumn2.put("total_profit", 1000);
                JSONObject serviceRankInfoByVolumn3 = new JSONObject();
                serviceRankInfoByVolumn3.put("service_id", "3");
                serviceRankInfoByVolumn3.put("total_amount", 10000);
                serviceRankInfoByVolumn3.put("total_profit", 500);

                List<JSONObject> serviceRankByVolumnList = new ArrayList<>();
                serviceRankByVolumnList.add(serviceRankInfoByVolumn1);
                serviceRankByVolumnList.add(serviceRankInfoByVolumn2);
                serviceRankByVolumnList.add(serviceRankInfoByVolumn3);

                //generate survice profit rank list by volumn
                JSONObject serviceProfitRank1 = new JSONObject();
                serviceProfitRank1.put("service_id", "101");
                serviceProfitRank1.put("service_percentage", "30");
                JSONObject serviceProfitRank2 = new JSONObject();
                serviceProfitRank2.put("service_id", "1");
                serviceProfitRank2.put("service_percentage", "10");
                JSONObject serviceProfitRank3 = new JSONObject();
                serviceProfitRank3.put("service_id", "2");
                serviceProfitRank3.put("service_percentage", "10");
                JSONObject serviceProfitRank4 = new JSONObject();
                serviceProfitRank4.put("service_id", "3");
                serviceProfitRank4.put("service_percentage", "15");
                JSONObject serviceProfitRank5 = new JSONObject();
                serviceProfitRank5.put("service_id", "4");
                serviceProfitRank5.put("service_percentage", "10");
                JSONObject serviceProfitRank6 = new JSONObject();
                serviceProfitRank6.put("service_id", "102");
                serviceProfitRank6.put("service_percentage", "15");
                JSONObject serviceProfitRank7 = new JSONObject();
                serviceProfitRank7.put("service_id", "103");
                serviceProfitRank7.put("service_percentage", "1");

                List<JSONObject> serviceProfitRankList = new ArrayList<>();
                serviceProfitRankList.add(serviceProfitRank1);
                serviceProfitRankList.add(serviceProfitRank2);
                serviceProfitRankList.add(serviceProfitRank3);
                serviceProfitRankList.add(serviceProfitRank4);
                serviceProfitRankList.add(serviceProfitRank5);
                serviceProfitRankList.add(serviceProfitRank6);
                serviceProfitRankList.add(serviceProfitRank7);

                //generate Top customer rank list by volumn
                JSONObject customerInfo1 = new JSONObject();
                customerInfo1.put("user_name", "Nazmul Hasan");
                customerInfo1.put("amount_percentage", "50");
                JSONObject customerInfo2 = new JSONObject();
                customerInfo2.put("user_name", "Alamgir Kabir");
                customerInfo2.put("amount_percentage", "30");
                JSONObject customerInfo3 = new JSONObject();
                customerInfo3.put("user_name", "Rashida Sultana");
                customerInfo3.put("amount_percentage", "20");

                List<JSONObject> topCustomerList = new ArrayList<>();
                topCustomerList.add(customerInfo1);
                topCustomerList.add(customerInfo2);
                topCustomerList.add(customerInfo3);

                resultInfos.put("summaryInfo", summaryInfo);
                resultInfos.put("serviceRankByVolumnList", serviceRankByVolumnList);
                resultInfos.put("topCustomerList", topCustomerList);
                resultInfos.put("serviceProfitRankList", serviceProfitRankList);
                resultEvent.setResponseCode(ResponseCodes.SUCCESS);
                resultEvent.setResult(resultInfos);

            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(resultInfos.toString());
        });

        router.route("/getServiceRanklistByVolumn*").handler(BodyHandler.create());
        router.post("/getServiceRanklistByVolumn").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            List<JSONObject> serviceRankByVolumnList = new ArrayList<>();
            String startDate = routingContext.request().getParam("start_date");
            String endDate = routingContext.request().getParam("end_date");
            try {

                //generate survice rank list by volumn
                // return service rank list by acending order by volumn
                JSONObject serviceRankInfoByVolumn1 = new JSONObject();
                serviceRankInfoByVolumn1.put("service_id", "102");
                serviceRankInfoByVolumn1.put("total_amount", 7000);
                serviceRankInfoByVolumn1.put("total_profit", 100);
                JSONObject serviceRankInfoByVolumn2 = new JSONObject();
                serviceRankInfoByVolumn2.put("service_id", "1");
                serviceRankInfoByVolumn2.put("total_amount", 2000);
                serviceRankInfoByVolumn2.put("total_profit", 2000);
                JSONObject serviceRankInfoByVolumn3 = new JSONObject();
                serviceRankInfoByVolumn3.put("service_id", "2");
                serviceRankInfoByVolumn3.put("total_amount", 30000);
                serviceRankInfoByVolumn3.put("total_profit", 500);

//                List<JSONObject> serviceRankByVolumnList = new ArrayList<>();
                serviceRankByVolumnList.add(serviceRankInfoByVolumn1);
                serviceRankByVolumnList.add(serviceRankInfoByVolumn2);
                serviceRankByVolumnList.add(serviceRankInfoByVolumn3);
                resultEvent.setResponseCode(ResponseCodes.SUCCESS);
                resultEvent.setResult(serviceRankByVolumnList);

            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(serviceRankByVolumnList.toString());
        });
        router.route("/getCustomerList*").handler(BodyHandler.create());
        router.post("/getCustomerList").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            List<JSONObject> topCustomerList = new ArrayList<>();
            String startDate = routingContext.request().getParam("start_date");
            String endDate = routingContext.request().getParam("end_date");
            String serviceId = routingContext.request().getParam("service_id");
            try {

                //generate Top customer rank list by volumn
                JSONObject customerInfo1 = new JSONObject();
                customerInfo1.put("user_name", "Rashida sultana");
                customerInfo1.put("amount_percentage", "60");
                JSONObject customerInfo2 = new JSONObject();
                customerInfo2.put("user_name", "Alamgir Kabir");
                customerInfo2.put("amount_percentage", "30");
                JSONObject customerInfo3 = new JSONObject();
                customerInfo3.put("user_name", "Nazmul Hasan");
                customerInfo3.put("amount_percentage", "20");

//                List<JSONObject> topCustomerList = new ArrayList<>();
                topCustomerList.add(customerInfo1);
                topCustomerList.add(customerInfo2);
                topCustomerList.add(customerInfo3);
                resultEvent.setResponseCode(ResponseCodes.SUCCESS);
                resultEvent.setResult(topCustomerList);

            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(topCustomerList.toString());
        });

        router.route("/getServiceProfitList*").handler(BodyHandler.create());
        router.post("/getServiceProfitList").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            List<JSONObject> serviceProfitRankList = new ArrayList<>();
            String startDate = routingContext.request().getParam("start_date");
            String endDate = routingContext.request().getParam("end_date");
            try {

                JSONObject serviceProfitRank1 = new JSONObject();
                serviceProfitRank1.put("service_id", "101");
                serviceProfitRank1.put("service_percentage", "20");
                JSONObject serviceProfitRank2 = new JSONObject();
                serviceProfitRank2.put("service_id", "1");
                serviceProfitRank2.put("service_percentage", "20");
                JSONObject serviceProfitRank3 = new JSONObject();
                serviceProfitRank3.put("service_id", "2");
                serviceProfitRank3.put("service_percentage", "10");
                JSONObject serviceProfitRank4 = new JSONObject();
                serviceProfitRank4.put("service_id", "3");
                serviceProfitRank4.put("service_percentage", "15");
                JSONObject serviceProfitRank5 = new JSONObject();
                serviceProfitRank5.put("service_id", "4");
                serviceProfitRank5.put("service_percentage", "5");
                JSONObject serviceProfitRank6 = new JSONObject();
                serviceProfitRank6.put("service_id", "102");
                serviceProfitRank6.put("service_percentage", "15");
                JSONObject serviceProfitRank7 = new JSONObject();
                serviceProfitRank7.put("service_id", "103");
                serviceProfitRank7.put("service_percentage", "10");

                serviceProfitRankList.add(serviceProfitRank1);
                serviceProfitRankList.add(serviceProfitRank2);
                serviceProfitRankList.add(serviceProfitRank3);
                serviceProfitRankList.add(serviceProfitRank4);
                serviceProfitRankList.add(serviceProfitRank5);
                serviceProfitRankList.add(serviceProfitRank6);
                serviceProfitRankList.add(serviceProfitRank7);
                resultEvent.setResponseCode(ResponseCodes.SUCCESS);
                resultEvent.setResult(serviceProfitRankList);

            } catch (Exception ex) {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
            }
            HttpServerResponse response = routingContext.response();
            response.end(serviceProfitRankList.toString());
        });
        server.requestHandler(router::accept).listen(4040);
    }

}
