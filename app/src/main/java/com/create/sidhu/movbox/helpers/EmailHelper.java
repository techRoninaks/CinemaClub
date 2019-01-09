package com.create.sidhu.movbox.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import com.create.sidhu.movbox.R;

public class EmailHelper {
    public static final String TECH_SUPPORT = "tech";
    public static final String ADMIN = "admin";
    public static final String CUSTOM = "custom";
    public static final String ALL = "all";
    private String sender;
    private String recepient = "";
    private String cc = "";
    private String body = "No body message";
    private String subject;
    private String bcc = "";
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

        SharedPreferences sharedPreferences = context.getSharedPreferences("CinemaClub", 0);
        body = sharedPreferences.getString("current_usermodel","") + "\n\n" + body;
        SqlHelper sqlHelper = new SqlHelper(context);
        sqlHelper.setMethod("POST");
        sqlHelper.setExecutePath("send-mail.php");
        ContentValues params = new ContentValues();
        params.put("sender", sender);
        params.put("recepient", recepient);
        params.put("body", body);
        params.put("subject", subject);
        params.put("bcc", bcc);
        params.put("cc", cc);
        sqlHelper.setParams(params);
        sqlHelper.setService(true);
        sqlHelper.executeUrl(false);
    }

}
