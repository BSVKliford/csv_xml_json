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
            Node root = document.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                long id = 0L;
                String firstName = null;
                String lastName = null;
                String country = null;
                int age = 0;
                if (node.getNodeType() != Node.TEXT_NODE) {
                    NodeList nodeProps = node.getChildNodes();
                    for (int j = 0; j < nodeProps.getLength(); j++) {
                        Node nodeProp = nodeProps.item(j);
                        if (nodeProp.getNodeType() != Node.TEXT_NODE) {
                            if (nodeProp.getNodeName().equals("id")) {
                                id = Long.parseLong(nodeProp.getChildNodes().item(0).getTextContent());
                            } else if (nodeProp.getNodeName().equals("firstName")) {
                                firstName = nodeProp.getChildNodes().item(0).getTextContent();
                            } else if (nodeProp.getNodeName().equals("lastName")) {
                                lastName = nodeProp.getChildNodes().item(0).getTextContent();
                            } else if (nodeProp.getNodeName().equals("country")) {
                                country = nodeProp.getChildNodes().item(0).getTextContent();
                            } else {
                                age = Integer.parseInt(nodeProp.getChildNodes().item(0).getTextContent());
                            }
                        }
                    }
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