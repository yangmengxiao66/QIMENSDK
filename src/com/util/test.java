package com.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;

public class test {
    public static void main(String[] args) {
        // This is the path where the file's name you want to take.
//        String path = "D://QIMENProject//Framework_QIMEN//trunk//java//src//nds//control//ejb//command";
//        getFile(path);
//        try {
//            File file = new File("D:\\123.pdf");
//            System.out.println(file.length());
//            File file1 = new File("D:\\234.pdf");
//            System.out.println(file1.length());
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        String date = "@_@20190809@_@";
        date = date.substring(date.indexOf("@_@")+3,date.lastIndexOf("@_@"));
        System.out.println(date);
    }

    private static void getFile(String path) {
        // get file list where the path has
        File file = new File(path);
        // get the folder list
        File[] array = file.listFiles();

        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile()) {
                // only take file name
                System.out.println(array[i].getName().substring(0,array[i].getName().lastIndexOf(".")));
            } else if (array[i].isDirectory()) {
                getFile(array[i].getPath());
            }
        }
    }


    public static void AA() throws IOException {
        Document document = DocumentHelper.createDocument();
        Element itemElement = document.addElement("response");
        Element idElement = itemElement.addElement("flag");
        idElement.setText("xxxxx");
        Element nameElement = itemElement.addElement("code");
        nameElement.setText(String.valueOf("RRRR"));
        Element messageElement = itemElement.addElement("message");
        messageElement.setText("HAHAHAHAH");
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("utf-8");
        outputFormat.setNewLineAfterDeclaration(false);
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
        xmlWriter.write(document);
        System.out.println("-----返回信息-----" + xmlWriter.toString());
        xmlWriter.close();
    }
}
