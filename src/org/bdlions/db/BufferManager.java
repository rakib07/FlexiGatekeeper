package org.bdlions.db;

import java.util.List;
import org.bdlions.activemq.Producer;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.callback.CallbackTransactionManager;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Transactions;
import org.bdlions.utility.ServerPropertyProvider;
import org.bdlions.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul
 */
public class BufferManager {
    private static final Logger logger = LoggerFactory.getLogger(BufferManager.class.getName());
    static TransactionManager transactionManager = new TransactionManager();
    static int bufferTime;
    public BufferManager()
    {
        try
        {
            bufferTime = Integer.parseInt(ServerPropertyProvider.get("BUFFER_TRANSACTION_WAIT_TIME"));
        }
        catch(Exception ex)
        {
            logger.debug("Invalid transaction buffer time:"+ex.toString());
        }
    }
    
    public TransactionManager getTransactionManager()
    {
        return transactionManager;
    }
    
    synchronized public static void processBuffer(TransactionInfo transactionInfo, int processType)
    {
        if(processType == Transactions.BUFFER_PROCESS_TYPE_ADD_TRANSACTION)
        {
            System.out.println("ProcessType:"+processType);
            //add transaction
            transactionManager.addTransaction(transactionInfo);
        }
        else if(processType == Transactions.BUFFER_PROCESS_TYPE_UPDATE_TRANSACTION)
        {
            System.out.println("ProcessType:"+processType);
            //update transaction
            TransactionInfo updatedTransactionInfo = transactionManager.getTransactionInfo(transactionInfo.getTransactionId());
            if(updatedTransactionInfo.isEditable())
            {
                updatedTransactionInfo.setBalanceOut(transactionInfo.getBalanceOut());
                updatedTransactionInfo.setCellNumber(transactionInfo.getCellNumber());
                transactionManager.updateTransactionInfo(updatedTransactionInfo);
            }
            else
            {
                transactionManager.setResponseCode(ResponseCodes.ERROR_CODE_UPDATE_TRANSACTION_NOT_ALLOWED);
            }            
        }
        else if(processType == Transactions.BUFFER_PROCESS_TYPE_ACTIVEMQ)
        {
            System.out.println("ProcessType:"+processType);
            List<TransactionInfo> transactionList = transactionManager.getEditableTransactionList();
            int transactionListSize = transactionList.size();
            if(transactionListSize > 0)
            {
                AuthManager authManager = new AuthManager();
                String lsIdentifier = "";
                String baseURL = "";
                for(int counter = 0; counter < transactionList.size(); counter++)
                {
                    TransactionInfo editableTransactionInfo = new TransactionInfo();
                    editableTransactionInfo = transactionList.get(counter);
                    System.out.println(editableTransactionInfo.getTransactionId());
                    int currentTime = Utils.getCurrentUnixTime();
                    int createdOn = editableTransactionInfo.getCreatedOn();
                    if(currentTime >= createdOn + bufferTime)
                    {
                        System.out.println("We need to update editable status of this transaction.");
                        editableTransactionInfo.setEditable(Boolean.FALSE);
                        transactionManager.updateTransactionInfo(editableTransactionInfo);
                        //forward the transaction to activemq
                        if(lsIdentifier == null || lsIdentifier.equals(""))
                        {
                            lsIdentifier = authManager.getLSIdentifier(editableTransactionInfo.getAPIKey());
                        }
                        if(baseURL == null || baseURL.equals(""))
                        {
                            baseURL = authManager.getBaseURLTransactionId(editableTransactionInfo.getTransactionId());
                        }
                        if(lsIdentifier != null && !lsIdentifier.equals(""))
                        {
                            try
                            {
                                //if(editableTransactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LOCALSERVER_TEST) || editableTransactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LIVE))
                                {
                                    //activemq to enqueue a new transaction
                                    Producer producer = new Producer();
                                    System.out.println(editableTransactionInfo.toString());
                                    producer.setMessage(editableTransactionInfo.toString());
                                    producer.setServiceQueueName(editableTransactionInfo.getServiceId(), lsIdentifier);
                                    System.out.println("Queue name:"+producer.getServiceQueueName());
                                    producer.produce();
                                }
                            }
                            catch(Exception ex)
                            {

                            }
                            //execute callback function to update editable at webserver
                            try
                            {
                                CallbackTransactionManager callbackTransactionManager = new CallbackTransactionManager();
                                callbackTransactionManager.setBaseURL(baseURL);
                                callbackTransactionManager.updateTransactionEditableStatus(editableTransactionInfo.getTransactionId(), editableTransactionInfo.isEditable());
                            }
                            catch(Exception ex)
                            {

                            }
                        }                        
                    }

                }
            }            
        }        
    }
}
