package com.fqueue.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import sun.security.krb5.internal.PAEncTSEnc;

public class PrintPage implements Printable {  
      
	PageFormat pageFormat;
    String text = "kosong";

    public static final int PAPER_A4 = 1;
    public static final int PAPER_LETTER = 2;

    int normalFontSize = 25;
    int largeFontSize = 100;
    double widthInch = 8.27;
    double heightInch = 5;
    double dpiX = 180;
    double dpiY = 180;

    int width = (int) (widthInch * (double)dpiX) - 5;//612;
    int height = (int) (heightInch * (double)dpiY) - 5;//936;
    int leftMargin = 12;
    int rightMargin = 2;
    int topMargin = 10; 
    Vector v = null;
    
    public PrintPage(String queueNo, String service, String time) {  
    	v = new Vector();
    	v.add(new String[]{"30","PLAIN","No Antrian: "});
    	v.add(new String[]{"170","PLAIN",setLeadingZero(Integer.parseInt(queueNo))});
    	v.add(new String[]{"40","PLAIN",service});
    	v.add(new String[]{"20","PLAIN", "Jika Nomer Anda Terlewat"});
    	v.add(new String[]{"20","PLAIN", "Mohon Mengambil Nomer Antrian Kembali"});
    	v.add(new String[]{"20","PLAIN", "Terima Kasih"});
    	v.add(new String[]{"25","PLAIN",time});
//    	v.add(new String[]{"26","PLAIN","Perkiraan Menunggu: "+estimate});
    }
    
    private static String setLeadingZero(int qNo){
		NumberFormat formatter = new DecimalFormat("000");
		String res = formatter.format(qNo);
		return res;
	}
    
    public int print(Graphics g, PageFormat pageFormat, int index) throws PrinterException {
        System.out.println("PermataPrint");
        Graphics2D g2 = (Graphics2D) g;
        g2.translate((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY());
        g2.scale(72.0d / dpiX, 72.0d / dpiY);

        System.out.println("x:"+72.0d / dpiX +"  y:"+ 72.0d / dpiY);
        /**
         * Calculate max length
         */
        int maxLength = 0;

        StringTokenizer token = new StringTokenizer(text, "\n");
        
        while(token.hasMoreTokens())
        {
            String next = token.nextToken();
            int length = next.length();
            if(length > maxLength)
            {
                maxLength = length;
            }
        }

        Font font;
        FontMetrics metrics = null;
        int fontSize = largeFontSize;
        int maxWidth;
        double paperWidth = pageFormat.getImageableWidth();
        System.out.println("PaperWidth:"+paperWidth);
        int yDelta = 0;
        int y = 0;

        for (int i=0; i<v.size(); i++){
        	
        	String[] s = (String[]) v.get(i); 
        	font = Font.decode("Monospaced-"+s[1]+ "-" +s[0]);
         	metrics = g2.getFontMetrics(font);
        	if (i==1){
        		leftMargin = 70;
        		yDelta = 160;
        	}else if(i==2){
        		leftMargin = (445-metrics.stringWidth(s[2]))/2; 
        		yDelta = 70;
        	}else if(i==3){
        		leftMargin = 78;
        		yDelta = 30;
        	}else if(i==5){
        		leftMargin = 140;
        		yDelta = 30;
        	}else{
        		leftMargin = 10;
        		yDelta = 30;
        	}
        	
    		 //metrics.getHeight();
    		y += yDelta;
    	
        	int width = metrics.charWidth('w');
            maxWidth = width * maxLength;
           
            g2.setFont(font);
            g2.setColor(Color.black);    
            
            
//            g2.drawString(s[2].substring(0, s[2].length()), leftMargin, y);
            g2.drawString(s[2], leftMargin, y);
            
        }
        
      
        return Printable.PAGE_EXISTS;
    }

  }  