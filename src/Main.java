import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class Main{
    static final int portAddress = 3307;
    static Connection connection = createConnection();
    static Statement stmnt = createStatement();
    static final String[] jsonKeys = {
            "id", "parent_id", "link_id", "name",
            "author", "body", "subreddit_id",
            "subreddit", "created_utc", "score"
    };

    String id = "";
    String parent_id = "";
    String link_id = "";
    String name = "";
    String author = "";
    String body = "";
    String subreddit_id = "";
    String subreddit = "";
    int created_utc = 0;
    int score = 0;

    public static void main(String[] args) throws IOException {

        File jsonFile = new File("RC_2007-10");
        FileReader fr = new FileReader(jsonFile);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null){
            printValues(line);
        }
    }

    public static Connection createConnection(){
        Connection con = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:" + portAddress + "/reddit";
            con = DriverManager.getConnection(url, "root", "root");

        } catch(Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public static Statement createStatement(){
        try{
            stmnt = connection.createStatement();
        } catch(Exception ex) {
            System.out.println("Exception");
        }
        return stmnt;
    }

    static void insert() throws SQLException {



//            String query = "INSERT INTO client (personal_number, firstname, lastname, address) VALUES (?, ?, ?, ?)";
//
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1, personalNumber);
//            preparedStatement.setString(2, firstName);
//            preparedStatement.setString(3, lastName);
//            preparedStatement.setString(4, address);
//
//            preparedStatement.execute();


    }

    static void printValues(String line){

        JSONObject obj = new JSONObject(line);

        for (int i = 0; i < 9; i++){
            System.out.println(jsonKeys[i] + ": " + obj.get(jsonKeys[i]));
        }
        System.out.println(jsonKeys[9] + ": " + obj.get(jsonKeys[9]));


        printDashes(100);
        System.out.println();

    }

    static void printDashes(int n){
        for (int i = 0; i < n; i++)
            System.out.print("-");
    }
}

