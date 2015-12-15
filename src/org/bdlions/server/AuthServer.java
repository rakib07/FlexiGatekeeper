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
                if(connection == null){
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                }
                else
                {
                    String userName = routingContext.request().getParam("username");
                    String maxMembers = routingContext.request().getParam("maxmembers");
                    String APIKey = routingContext.request().getParam("apikey");

                    try
                    {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setReferenceUserName(userName);
                        userInfo.setMaxMembers(Integer.parseInt(maxMembers));
                        userInfo.setRegistrationDate(1449141369);
                        //right now we are setting a default expired time
                        userInfo.setExpiredDate(2140000000);
                        //right now are not restricting/validating ip address
                        userInfo.setIpAddress("192.168.1.30");

                        UserServiceInfo userServiceInfo = new UserServiceInfo();
                        userServiceInfo.setServiceId(Services.SERVICE_TYPE_ID_BKASH_SEND_MONEY);
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
                    }
                    catch(Exception ex)
                    {
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
                if(connection == null){
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION);
                    logger.info("Db connection not set.");
                }
                else
                {
                    String userName = routingContext.request().getParam("username");
                    try
                    {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setSubscriberReferenceUserName(userName);
                        

                        AuthManager authManager = new AuthManager();
                        userInfo = authManager.getSubscriberInfo(userInfo);
                        int responseCode = authManager.getResponseCode();
                        resultEvent.setResponseCode(responseCode);
                        if(responseCode == ResponseCodes.SUCCESS)
                        {
                            resultEvent.setResult(userInfo);
                        }
                    }
                    catch(Exception ex)
                    {
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
            
            try
            {
                AuthManager authManager = new AuthManager();
                authManager.createUser(userInfo);
                int responseCode = authManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            }
            catch(UnRegisterIPException ex)
            {
                //Right now we are skipping ipaddress validation
            }
            catch(SubscriptionExpireException ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_SUBSCRIPTION_PERIOD_EXPIRED);
                logger.error(ex.getMessage());
            }
            catch(MaxMemberRegException ex)
            {
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
            
            try
            {
                AuthManager authManager = new AuthManager();
                result = authManager.getSessionInfo(userInfo, "64hedl981o0suvld9r79kklta2");
            }
            catch(SubscriptionExpireException | ServiceExpireException ex)
            {
            
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
        
        server.requestHandler(router::accept).listen(4040);
    }

}
