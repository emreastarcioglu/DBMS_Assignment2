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

        String query0 = "SELECT COUNT(subreddit.id) FROM subreddit WHERE subreddit.id = ?";

        PreparedStatement preparedStatement0 = connection.prepareStatement(query0);
        preparedStatement0.setString(1, subreddit_id);
        ResultSet resultSet = preparedStatement0.executeQuery();

//      Checks if subreddit exists or not in our database
        if (resultSet.next()){
            if(resultSet.getInt(1) == 0){
                String query3 = "INSERT INTO subreddit (id, name) VALUES (?, ?)";

                PreparedStatement preparedStatement3 = connection.prepareStatement(query3);
                preparedStatement3.setString(1, subreddit_id);
                preparedStatement3.setString(2, subreddit);

                preparedStatement3.execute();
            }
        }

        String query1 = "SELECT COUNT(post.id) FROM post WHERE post.id = ?";
        preparedStatement0 = connection.prepareStatement(query1);
        preparedStatement0.setString(1, link_id);
        resultSet = preparedStatement0.executeQuery();

        if (resultSet.next()) {
            if (resultSet.getInt(1) == 0) {
                String query2 = "INSERT INTO post (id, subreddit_id) VALUES (?, ?)";

                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
                preparedStatement2.setString(1, link_id);
                preparedStatement2.setString(2, subreddit_id);

                preparedStatement2.execute();
            }
        }

        String query = "INSERT INTO comment (id, parent_id, link_id, name, author, body, score, created_utc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
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

    static void printDashes(int n){
        for (int i = 0; i < n; i++)
            System.out.print("-");
    }
}

