package org.matf.master.luka;

import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static void main(String[] args) throws IOException, ParseException {

        List<String> productLines = new ArrayList<>();

        for(int i=0;i<3000000;i++) {
            List<String> row = new ArrayList<>();
            String name = "name_" + i;
            row.add(name);
            row.add(name);
            String brand = "brand_" + i;
            row.add(brand);
            String type = "type_" + i;
            row.add(type);
            String size = ""+(i % 200);
            row.add(size);
            String container = "container_" + i;
            row.add(container);
            String price = "1000";
            row.add(price);
            String comment = "comment_" + i;
            row.add(comment);
            productLines.add(convertToCSVRow(row));
        }

        String productContent = convertToCSV(productLines);

        File productCsvOutputFile = new File("productHB.csv");
        int bufferSize = 16384*100;

        PrintWriter productOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(productCsvOutputFile.toPath())), bufferSize), true);
        productOut.println(productContent);
        //SUPPLIER

        List<String> supplierLines = new ArrayList<>();
        for(int i=0;i<1000000;i++) {
            List<String> row = new ArrayList<>();
            String name = "name_"+i;
            row.add(name);
            row.add(name);
            String address = "address_"+i;
            row.add(address);
            String phone = "phone_"+i;
            row.add(phone);
            supplierLines.add(convertToCSVRow(row));
        }

        String supplierContent = convertToCSV(supplierLines);

        File supplierCsvOutputFile = new File("supplierHB.csv");

        PrintWriter supplierOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(supplierCsvOutputFile.toPath())), bufferSize), true);
        supplierOut.println(supplierContent);

        //PRODUCTSUPPLIER

        List<String> productSupplier = new ArrayList<>();
        for(int i=0;i<5000000;i++) {
            List<String> row = new ArrayList<>();
            String product = "name_"+(i%732000);
            String supplier = "name_"+i%9837;
            row.add(product+supplier);
            row.add(product);
            row.add(supplier);
            String available = ""+i%100;
            row.add(available);
            String supplyCost = ""+200;
            row.add(supplyCost);
            String comment = "comment_"+i;
            row.add(comment);
            productSupplier.add(convertToCSVRow(row));
        }

        String productSupplierContent = convertToCSV(productSupplier);

        File productSupplierCsvOutputFile = new File("productsupplierHB.csv");

        PrintWriter productSupplierOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(productSupplierCsvOutputFile.toPath())), bufferSize), true);
        productSupplierOut.println(productSupplierContent);

        List<String> customerLines = new ArrayList<>();
        for(int i=0;i<1500000;i++) {
            List<String> row = new ArrayList<>();
            String name = "name_"+i;
            row.add(name);
            row.add(name);
            String address = "address_"+i;
            row.add(address);
            String phone = "phone_"+i;
            row.add(phone);
            String comment = "comment_"+i;
            row.add(comment);
            customerLines.add(convertToCSVRow(row));
        }

        String customerContent = convertToCSV(customerLines);

        File customerCsvOutputFile = new File("customerHB.csv");

        PrintWriter customerOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(customerCsvOutputFile.toPath())), bufferSize), true);
        customerOut.println(customerContent);

        //ORDER
        List<String> orderLines = new ArrayList<>();
        for(int i=0;i<1500000;i++) {
            List<String> row = new ArrayList<>();
            String id = ""+i;
            row.add(id);
            String customer = "name_"+(i%150000);
            row.add(customer);
            String status = "status"+(i%20);
            row.add(status);
            String totalPrice = ""+20000;
            row.add(totalPrice);
            String entryDate = ""+System.currentTimeMillis();
            row.add(entryDate);
            String priority = "priority_"+(i%10);
            row.add(priority);
            String comment = "comment_"+i;
            row.add(comment);
            orderLines.add(convertToCSVRow(row));
        }

        String orderContent = convertToCSV(orderLines);

        File orderCsvOutputFile = new File("orderHB.csv");

        PrintWriter orderOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(orderCsvOutputFile.toPath())), bufferSize), true);
        orderOut.println(orderContent);

        //ORDER_ITEM
        List<String> orderItemLines = new ArrayList<>();
        for(int i=0;i<6000000;i++) {
            List<String> row = new ArrayList<>();
            String order = ""+i%1450000;
            String product = "name_"+i%1970000;
            String supplier = "name_"+i%9500;
            String orderNo = ""+(i%7);
            String status = "status_"+i%3;
            row.add(status+"-"+order+"-"+orderNo);
            row.add(order);
            row.add(product);
            row.add(supplier);
            row.add(orderNo);
            String quantity = retrieveQuantity(i);
            row.add(quantity);
            String basePrice = "3500";
            row.add(basePrice);
            String discount = "0.1";
            row.add(discount);
            String tax = "0.2";
            row.add(tax);
            row.add(status);
            String shipDate = retrieveShipDate(i);
            row.add(shipDate);
            String commitDate = ""+System.currentTimeMillis();
            row.add(commitDate);
            String comment = "comment_"+i;
            row.add(comment);
            orderItemLines.add(convertToCSVRow(row));
        }

        String orderItemContent = convertToCSV(orderItemLines);

        File orderItemCsvOutputFile = new File("orderitemHB.csv");
        PrintWriter orderItemOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(orderItemCsvOutputFile.toPath())), bufferSize), true);
        orderItemOut.println(orderItemContent);



        List<String> orderItemStatsLines = new ArrayList<>();
        for(int i=0;i<20;i++){
            List<String> row = new ArrayList<>();
            row.add("status_"+i);
            row.add("status_"+i);
            String quantitySumOn01072024 = ""+256;
            row.add(quantitySumOn01072024);
            String basePrice01072024 = ""+2521;
            row.add(basePrice01072024);
            String sumDiscountPrice01072024 = ""+1456;
            row.add(sumDiscountPrice01072024);
            String sumChargePrice01072024 = ""+1521;
            row.add(sumChargePrice01072024);
            String avgQuantity01072024 = ""+251.5;
            row.add(avgQuantity01072024);
            String avgPrice01072024 = ""+154.5;
            row.add(avgPrice01072024);
            String avgDiscount01072024 = ""+0.5;
            row.add(avgDiscount01072024);

            String quantitySumOn02072024 = ""+221;
            row.add(quantitySumOn02072024);
            String basePrice02072024 = ""+2521;
            row.add(basePrice02072024);
            String sumDiscountPrice02072024 = ""+1456;
            row.add(sumDiscountPrice02072024);
            String sumChargePrice02072024 = ""+1521;
            row.add(sumChargePrice02072024);
            String avgQuantity02072024 = ""+251.5;
            row.add(avgQuantity02072024);
            String avgPrice02072024 = ""+154.5;
            row.add(avgPrice02072024);
            String avgDiscount02072024 = ""+0.5;
            row.add(avgDiscount02072024);



            String quantitySumOn03072024 = ""+237;
            row.add(quantitySumOn03072024);
            String basePrice03072024 = ""+2521;
            row.add(basePrice03072024);
            String sumDiscountPrice03072024 = ""+1456;
            row.add(sumDiscountPrice03072024);
            String sumChargePrice03072024 = ""+1521;
            row.add(sumChargePrice03072024);
            String avgQuantity03072024 = ""+251.5;
            row.add(avgQuantity03072024);
            String avgPrice03072024 = ""+154.5;
            row.add(avgPrice03072024);
            String avgDiscount03072024 = ""+0.5;
            row.add(avgDiscount03072024);



            String quantitySumOn04072024 = ""+321;
            row.add(quantitySumOn04072024);
            String basePrice04072024 = ""+2521;
            row.add(basePrice04072024);
            String sumDiscountPrice04072024 = ""+1456;
            row.add(sumDiscountPrice04072024);
            String sumChargePrice04072024 = ""+1521;
            row.add(sumChargePrice04072024);
            String avgQuantity04072024 = ""+251.5;
            row.add(avgQuantity04072024);
            String avgPrice04072024 = ""+154.5;
            row.add(avgPrice04072024);
            String avgDiscount04072024 = ""+0.5;
            row.add(avgDiscount04072024);


            String quantitySumOn05072024 = ""+275;
            row.add(quantitySumOn05072024);
            String basePrice05072024 = ""+2521;
            row.add(basePrice05072024);
            String sumDiscountPrice05072024 = ""+1456;
            row.add(sumDiscountPrice05072024);
            String sumChargePrice05072024 = ""+1521;
            row.add(sumChargePrice05072024);
            String avgQuantity05072024 = ""+251.5;
            row.add(avgQuantity05072024);
            String avgPrice05072024 = ""+154.5;
            row.add(avgPrice05072024);
            String avgDiscount05072024 = ""+0.5;
            row.add(avgDiscount05072024);


            String quantitySumOn06072024 = ""+124;
            row.add(quantitySumOn06072024);
            String basePrice06072024 = ""+2521;
            row.add(basePrice06072024);
            String sumDiscountPrice06072024 = ""+1456;
            row.add(sumDiscountPrice06072024);
            String sumChargePrice06072024 = ""+1521;
            row.add(sumChargePrice06072024);
            String avgQuantity06072024 = ""+251.5;
            row.add(avgQuantity06072024);
            String avgPrice06072024 = ""+154.5;
            row.add(avgPrice06072024);
            String avgDiscount06072024 = ""+0.5;
            row.add(avgDiscount06072024);

            String quantitySumOn07072024 = ""+222;
            row.add(quantitySumOn07072024);
            String basePrice07072024 = ""+2521;
            row.add(basePrice07072024);
            String sumDiscountPrice07072024 = ""+1456;
            row.add(sumDiscountPrice07072024);
            String sumChargePrice07072024 = ""+1521;
            row.add(sumChargePrice07072024);
            String avgQuantity07072024 = ""+251.5;
            row.add(avgQuantity07072024);
            String avgPrice07072024 = ""+154.5;
            row.add(avgPrice07072024);
            String avgDiscount07072024 = ""+0.5;
            row.add(avgDiscount07072024);

            orderItemStatsLines.add(convertToCSVRow(row));
        }

        String orderItemStatsContent = convertToCSV(orderItemStatsLines);

        File orderItemStatsCsvOutputFile = new File("orderitemStatsHB.csv");
        PrintWriter orderItemStatsOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(orderItemStatsCsvOutputFile.toPath())), bufferSize), true);
        orderItemStatsOut.println(orderItemStatsContent);
    }

    private static String retrieveQuantityForQ01072024(int seed){
        if((seed%7)+1==1){
            return (seed%7) + "";
        }
        else
            return "0";
    }

    private static String retrieveQuantityForQ02072024(int seed){
        if((seed%7)+1==2){
            return (seed%7) + "";
        }
        else
            return "0";
    }

    private static String retrieveQuantityForQ03072024(int seed){
        if((seed%7)+1==3){
            return (seed%7) + "";
        }
        else
            return "0";
    }

    private static String retrieveQuantityForQ04072024(int seed){
        if((seed%7)+1==4){
            return (seed%7) + "";
        }
        else
            return "0";
    }

    private static String retrieveQuantityForQ05072024(int seed){
        if((seed%7)+1==5){
            return (seed%7) + "";
        }
        else
            return "0";
    }
    private static String retrieveQuantityForQ06072024(int seed){
        if((seed%7)+1==6){
            return (seed%7) + "";
        }
        else
            return "0";
    }

    private static String retrieveQuantityForQ07072024(int seed){
        if((seed%7)+1==7){
            return (seed % 7) + "";
        }
        else
            return 0 + "";
    }

    private static String retrieveShipDate(int seed) throws ParseException {
        return dateFormat.parse("0"+((seed%7)+1)+".07.2024.").getTime()+"";
    }

    private static String retrieveQuantity(int seed){
        return (seed%7) + "";
    }


    private static  String convertToCSVRow(List<String> data) {
        return String.join(",",data);
    }

    private static  String convertToCSV(List<String> data) {
        return String.join("\n",data);
    }
}