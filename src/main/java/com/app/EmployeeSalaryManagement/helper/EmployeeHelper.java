package com.app.EmployeeSalaryManagement.helper;

import com.app.EmployeeSalaryManagement.model.Employee;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeHelper {
    public static String TYPE = "text/csv";
    public static SimpleDateFormat diffDateFormat = new SimpleDateFormat("dd-MMM-yy");
    public static SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean hasCSVFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static HashMap<String, Object> csvToRecords(InputStream is) {
        HashMap<String, Object> map = new HashMap<>();
        int rowCount = 0;
        //LocalDate startDate = null;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
            List<Employee> users = new ArrayList<Employee>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                rowCount++;
                if(csvRecord.get("id").startsWith("#")){
                    continue;
                } else if (csvRecord.get("id").equalsIgnoreCase("")
                        || csvRecord.get("login").equalsIgnoreCase("")
                        || csvRecord.get("name").equalsIgnoreCase("")
                        || csvRecord.get("salary") == null
                        || csvRecord.get("startDate") == null) {
                    map.put("message", "Bad input - Empty mandatory data [Record no. : "+rowCount+" ]");
                    return map;
                } else if (csvRecord.get("salary") != null){
                    if(Double.parseDouble(csvRecord.get("salary")) < 0.0){
                        map.put("message", "Bad input - Invalid Salary [Record no. : "+rowCount+" ]");
                        return map;
                    }
                }
                /*
                try {
                    startDate = LocalDate.parse(localDateFormat.format(diffDateFormat.parse(csvRecord.get("startDate"))));
                } catch (ParseException e) {
                    startDate = LocalDate.parse(csvRecord.get("startDate"));
                }*/
                Employee user = new Employee(
                        csvRecord.get("id"),
                        csvRecord.get("login"),
                        csvRecord.get("name"),
                        Double.parseDouble(csvRecord.get("salary")),
                        csvRecord.get("startDate")
                );
                for (Employee existingUser : users
                ) {
                    if (existingUser.getId().equalsIgnoreCase(user.getId())) {
                        map.put("message", "Bad input - Duplicate ID [Record no. : "+rowCount+" ]");
                        return map;
                    } else if (existingUser.getLogin().equalsIgnoreCase(user.getLogin())) {
                        map.put("message", "Bad input - Duplicate Login [Record no. : "+rowCount+" ]");
                        return map;
                    }
                }
                users.add(user);
            }
            map.put("message", "");
            map.put("result", users);
            return map;
        } catch (IOException e) {
            map.put("Exception", e.getMessage());
            return map;
        }
    }

    /**
     * Convert "asc"/"desc" into Sort.Direction.ASC/Sort.Direction.DES.
     *
     * @param direction
     * @return sort direction
     */
    public static Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
          return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
          return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
      }
}
