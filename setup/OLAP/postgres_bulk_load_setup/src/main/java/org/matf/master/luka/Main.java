package org.matf.master.luka;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        List<String> productLines = new ArrayList<>();

        for(int i=0;i<2000000;i++) {
            List<String> row = new ArrayList<>();
            row.add(""+i);
            String name = "name_" + i;
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

        File productCsvOutputFile = new File("productPG.csv");
        int bufferSize = 16384*10;

        PrintWriter productOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(productCsvOutputFile.toPath())), bufferSize), true);
        productOut.println(productContent);
        //SUPPLIER

        List<String> supplierLines = new ArrayList<>();
        for(int i=0;i<10000;i++) {
            List<String> row = new ArrayList<>();
            row.add(""+i);
            String name = "name_"+i;
            row.add(name);
            String address = "address_"+i;
            row.add(address);
            String phone = "phone_"+i;
            row.add(phone);
            supplierLines.add(convertToCSVRow(row));
        }

        String supplierContent = convertToCSV(supplierLines);

        File supplierCsvOutputFile = new File("supplierPG.csv");

        PrintWriter supplierOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(supplierCsvOutputFile.toPath())), bufferSize), true);
        supplierOut.println(supplierContent);

        //PRODUCTSUPPLIER

        List<String> productSupplier = new ArrayList<>();
        for(int i=0;i<800000;i++) {
            List<String> row = new ArrayList<>();
            row.add(""+i);
            String product = ""+(i%732000);
            String supplier = ""+i%9837;
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

        File productSupplierCsvOutputFile = new File("productsupplierPG.csv");

        PrintWriter productSupplierOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(productSupplierCsvOutputFile.toPath())), bufferSize), true);
        productSupplierOut.println(productSupplierContent);

        List<String> customerLines = new ArrayList<>();
        for(int i=0;i<150000;i++) {
            List<String> row = new ArrayList<>();
            row.add(""+i);
            String name = "name_"+i;
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

        File customerCsvOutputFile = new File("customerPG.csv");

        PrintWriter customerOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(customerCsvOutputFile.toPath())), bufferSize), true);
        customerOut.println(customerContent);

        //ORDER
        List<String> orderLines = new ArrayList<>();
        for(int i=0;i<1500000;i++) {
            List<String> row = new ArrayList<>();
            String id = ""+i;
            row.add(id);
            String customer = ""+(i%150000);
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

        File orderCsvOutputFile = new File("orderPG.csv");

        PrintWriter orderOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(orderCsvOutputFile.toPath())), bufferSize), true);
        orderOut.println(orderContent);

        //ORDER_ITEM
        List<String> orderItemLines = new ArrayList<>();
        for(int i=0;i<6000000;i++) {
            List<String> row = new ArrayList<>();
            row.add(""+i);
            String order = ""+i%1450000;
            String product = ""+i%1970000;
            String supplier = ""+i%9500;
            String orderNo = ""+(i%7);
            row.add(order);
            row.add(product);
            row.add(supplier);
            row.add(orderNo);
            String quantity = ""+i%5;
            row.add(quantity);
            String basePrice = "3500";
            row.add(basePrice);
            String discount = "0.1";
            row.add(discount);
            String tax = "0.2";
            row.add(tax);
            String status = "status_"+i%10;
            row.add(status);
            String shipDate = ""+System.currentTimeMillis();
            row.add(shipDate);
            String commitDate = ""+System.currentTimeMillis();
            row.add(commitDate);
            String comment = "comment_"+i;
            row.add(comment);
            orderItemLines.add(convertToCSVRow(row));
        }

        String orderItemContent = convertToCSV(orderItemLines);

        File orderItemCsvOutputFile = new File("orderitemPG.csv");

        PrintWriter orderItemOut =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(orderItemCsvOutputFile.toPath())), bufferSize), true);
        orderItemOut.println(orderItemContent);

    }

    private static  String convertToCSVRow(List<String> data) {
        return String.join(",",data);
    }

    private static  String convertToCSV(List<String> data) {
        String result =  String.join("\\n",data);
        return result.substring(0,result.lastIndexOf("\\n"));
    }
}