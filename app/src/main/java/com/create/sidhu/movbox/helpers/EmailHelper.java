package com.create.sidhu.movbox.helpers;

import android.content.Context;
import android.provider.ContactsContract;

import com.create.sidhu.movbox.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class EmailHelper {
    public static final String TECH_SUPPORT = "tech";
    public static final String ADMIN = "admin";
    public static final String CUSTOM = "custom";
    public static final String ALL = "all";
    private String sender;
    private String recepient = "";
    private String cc;
    private String body = "No body message";
    private String subject;
    private String bcc;
    private Context context;
    private String type = "";

    public EmailHelper(Context context, String type, String subject){
        this.context = context;
        this.type = type;
        this.subject = subject;
        this.sender = context.getString(R.string.email_sender);
    }
    public EmailHelper(Context context, String type, String subject, String body){
        this.context = context;
        this.type = type;
        this.subject = subject;
        this.body = body;
        this.sender = context.getString(R.string.email_sender);
    }
    public EmailHelper(Context context, String type, String subject, String body, String recepient){
        this.context = context;
        this.type = type;
        this.subject = subject;
        this.body = body;
        this.recepient = recepient;
        this.sender = context.getString(R.string.email_sender);
    }
    public EmailHelper(Context context, String type, String subject, String body, String sender, String recepient){
        this.context = context;
        this.type = type;
        this.subject = subject;
        this.body = body;
        this.sender = sender;
        this.recepient = recepient;
    }
    public EmailHelper(Context context, String type, String subject, String body, String sender, String recepient, String cc){
        this.context = context;
        this.type = type;
        this.subject = subject;
        this.body = body;
        this.sender = sender;
        this.recepient = recepient;
        this.cc = cc;
    }
    public EmailHelper(Context context, String type, String subject, String body, String sender, String recepient, String cc, String bcc){
        this.context = context;
        this.type = type;
        this.subject = subject;
        this.body = body;
        this.sender = sender;
        this.recepient = recepient;
        this.cc = cc;
        this.bcc = bcc;
    }

    public void sendEmail(){
        if(type.isEmpty())
            type = ADMIN;
        switch (type){
            case TECH_SUPPORT:{
                recepient = context.getString(R.string.email_tech_support);
                break;
            }
            case ADMIN:{
                recepient = context.getString(R.string.email_admin);
                break;
            }
            case CUSTOM:{
                if(recepient.isEmpty())
                    recepient = context.getString(R.string.email_admin);
                else
                    bcc = context.getString(R.string.email_admin);
                break;
            }
            case ALL:{
                recepient = context.getString(R.string.email_all);
                break;
            }
        }
        if(sender.isEmpty())
            sender = context.getString(R.string.email_sender);
        SqlHelper sqlHelper = new SqlHelper(context);
        sqlHelper.setMethod("POST");
        sqlHelper.setExecutePath("send-email.php");
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("sender", sender));
        params.add(new BasicNameValuePair("recepient", recepient));
        params.add(new BasicNameValuePair("body", body));
        params.add(new BasicNameValuePair("subject", subject));
        params.add(new BasicNameValuePair("bcc", bcc));
        params.add(new BasicNameValuePair("cc", cc));
        sqlHelper.setParams(params);
        sqlHelper.setService(true);
        sqlHelper.executeUrl(false);
    }

}
