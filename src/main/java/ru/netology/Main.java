package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //задание 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCsv = parseCSV(columnMapping, fileName);
        String jsonCsv = listToJson(listCsv);
        writeString(jsonCsv, "data.json");
        //задание 2
        List<Employee> listXml = parseXML("data.xml");
        String jsonXml = listToJson(listXml);
        writeString(jsonXml, "data2.json");
        //задание 3
        String json = readString("data.json");
        List<Employee> list = jsonToList(json);
        for (Employee e : list) {
            System.out.println(e);
        }
    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> jsonReturn = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray array = (JSONArray) parser.parse(json);
            for (int i = 0; i < array.size(); i++) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Employee employee = gson.fromJson(String.valueOf(array.get(i)), Employee.class);//json text
                jsonReturn.add(employee);
            }
            return jsonReturn;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonReturn;
    }

    private static String readString(String s) {
        try (BufferedReader br = new BufferedReader(new FileReader(s))) {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(br);
            return obj.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Employee> parseXML(String s) {
        List<Employee> xmlReturn = new ArrayList<>();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(s);
            document.getDocumentElement().normalize();//а нужно ли?
            NodeList root = document.getElementsByTagName("employee");
            for (int i = 0; i < root.getLength(); i++) {
                long id = 0L;
                String firstName = null;
                String lastName = null;
                String country = null;
                int age = 0;
                Node node = root.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList elementNode = element.getElementsByTagName("id").item(0).getChildNodes();
                    id = Long.parseLong((elementNode.item(0)).getNodeValue());
                    elementNode = element.getElementsByTagName("firstName").item(0).getChildNodes();
                    firstName = (elementNode.item(0)).getNodeValue();
                    elementNode = element.getElementsByTagName("lastName").item(0).getChildNodes();
                    lastName = (elementNode.item(0)).getNodeValue();
                    elementNode = element.getElementsByTagName("country").item(0).getChildNodes();
                    country = (elementNode.item(0)).getNodeValue();
                    elementNode = element.getElementsByTagName("age").item(0).getChildNodes();
                    age = Integer.parseInt((elementNode.item(0)).getNodeValue());
                    xmlReturn.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        return xmlReturn;
    }

    private static void writeString(String json, String nameFile) {
        try (FileWriter file = new FileWriter(nameFile)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> csvReturn = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            csvReturn = csv.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvReturn;
    }
}