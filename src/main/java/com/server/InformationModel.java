package com.server;

import java.sql.Date;

public class InformationModel {
	private String text = "";
    private Date startDate = null;
    private Date endDate = null;

    public InformationModel(String text, Date startDate, Date endDate){
        this.text = text;
        //this.startDate = startDate;
        //this.endDate = endDate;
    }

    public InformationModel(String text, Date endDate){
        this.text = text;
        //this.endDate = endDate;
    }

    public InformationModel(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }
    
    public byte[] getBytes(Integer currentOrder) {
    	String message = currentOrder+"#"+this.text+"#"+this.startDate+"#"+this.endDate;
    	System.out.print(message);
    	return message.getBytes();
    }
    
    public void setStartDate(Date startDate) {
    	//this.startDate = startDate;
    }
    
    
    @Override 
    public String toString() {
    	return this.text+" "+this.startDate+" "+this.endDate;
    }
    
}
