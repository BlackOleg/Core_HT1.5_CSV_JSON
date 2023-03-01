package ru.netology.olegivanov;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        System.out.println("Let's start");
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileXML = "data.xml";
        String fileOut = "data.json";
        List<Employee> list=parseCSV(columnMapping, fileName);
        //System.out.println(list);
        // Преобразуем в Json
        String json = listToJson(list);
        // Пишем в файл
        writeString(json, fileOut);
        // Читаем XML
        List<Employee> listXML= parseXML(fileXML);
        //System.out.println(listXML);
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, fileOut);
        System.out.println("The tasks completed!");
    }

    private static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // Load the input XML document, parse it and return an instance of the
        Document doc = builder.parse(new File(fileName));
        NodeList nodeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                // Get the value of all sub-elements.
                Long id = Long.parseLong(elem.getElementsByTagName("id")
                        .item(0).getChildNodes().item(0).getNodeValue());
                String firstName = elem.getElementsByTagName("firstName")
                        .item(0).getChildNodes().item(0).getNodeValue();
                String lastName = elem.getElementsByTagName("lastName").item(0)
                        .getChildNodes().item(0).getNodeValue();
                String country = elem.getElementsByTagName("country").item(0)
                        .getChildNodes().item(0).getNodeValue();
                Integer age = Integer.parseInt(elem.getElementsByTagName("age")
                        .item(0).getChildNodes().item(0).getNodeValue());
                employees.add(new Employee(id, firstName, lastName, country, age));
            }
        }
    return employees;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String listToJson(List list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
    return json;
    }
    public static void writeString(String json, String fileOut){
        try (FileWriter file = new FileWriter(fileOut)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}