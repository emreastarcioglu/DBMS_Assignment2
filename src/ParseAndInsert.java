import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class ParseAndInsert{
    static final int portAddress = 3306;
    static Connection connection = createConnection();
    static Statement stmnt = createStatement();
    static final String[] jsonKeys = {
            "id", "parent_id", "link_id", "name",
            "author", "body", "subreddit_id",
            "subreddit", "created_utc", "score"
    };

    static String subredditInsertQuery = "INSERT IGNORE INTO subreddit (id, name) VALUES (?, ?)";
    static String postInsertQuery = "INSERT IGNORE INTO post (id, subreddit_id) VALUES (?, ?)";
    static String commentInsertQuery = "INSERT IGNORE INTO comment (id, parent_id, link_id, name, author, body, score, created_utc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    static String id = "";
    static String parent_id = "";
    static String link_id = "";
    static String name = "";
    static String author = "";
    static String body = "";
    static String subreddit_id = "";
    static String subreddit = "";
    static int created_utc = 0;
    static int score = 0;

    public static void main(String[] args) throws IOException, SQLException {

        File jsonFile = new File("RC_2007-10");
        FileReader fr = new FileReader(jsonFile);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null){
            parseJSONString(line);
            insert();
        }

        System.out.println("______End of the Program______");
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
        PreparedStatement preparedStatement = connection.prepareStatement(subredditInsertQuery);
        preparedStatement.setString(1, subreddit_id);
        preparedStatement.setString(2, subreddit);

        preparedStatement.execute();


        preparedStatement = connection.prepareStatement(postInsertQuery);
        preparedStatement.setString(1, link_id);
        preparedStatement.setString(2, subreddit_id);

        preparedStatement.execute();


        preparedStatement = connection.prepareStatement(commentInsertQuery);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, parent_id);
        preparedStatement.setString(3, link_id);
        preparedStatement.setString(4, name);
        preparedStatement.setString(5, author);
        preparedStatement.setString(6, body);
        preparedStatement.setInt(7, score);
        preparedStatement.setInt(8, created_utc);

        preparedStatement.execute();
    }

    static void parseJSONString(String line){
        JSONObject obj = new JSONObject(line);

        id = obj.getString(jsonKeys[0]);
        parent_id = obj.getString(jsonKeys[1]);
        link_id = obj.getString(jsonKeys[2]);
        name = obj.getString(jsonKeys[3]);
        author = obj.getString(jsonKeys[4]);
        body = obj.getString(jsonKeys[5]);
        subreddit_id = obj.getString(jsonKeys[6]);
        subreddit = obj.getString(jsonKeys[7]);
        created_utc = obj.getInt(jsonKeys[8]);
        score = obj.getInt(jsonKeys[9]);
    }
}

