import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ParseAndInsert{
    static final int portAddress = 3306;
    static Connection connection = createConnection();
    static Statement stmnt = createStatement();
    static final String[] jsonKeys = {
            "id", "parent_id", "link_id", "name",
            "author", "body", "subreddit_id",
            "subreddit", "created_utc", "score"
    };

    static final String subredditInsertQuery = "INSERT IGNORE INTO subreddit (id, name) VALUES (?, ?)";
    static final String postInsertQuery = "INSERT IGNORE INTO post (id, subreddit_id) VALUES (?, ?)";
    static final String commentInsertQuery = "INSERT IGNORE INTO comment (id, parent_id, link_id, name, author, body, score, created_utc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    String id;
    String parent_id;
    String link_id;
    String name;
    String author;
    String body;
    String subreddit_id;
    String subreddit;
    String created_utc;
    long epoch;
    int score;

    public static void main(String[] args) throws IOException, SQLException {
        File jsonFile = new File("RC_2007-10");
        FileReader fr = new FileReader(jsonFile);
        BufferedReader br = new BufferedReader(fr);
        String line;

        long start = System.nanoTime();

        ParseAndInsert[] objects = new ParseAndInsert[100];
        int loopIndex = 0;

        while ((line = br.readLine()) != null){
            objects[loopIndex] = parseJSONString(line);
            loopIndex++;

            if (loopIndex == 100){
                loopIndex = 0;
                insert(objects);
            }
        }

        long end = System.nanoTime();
        long elapsedTime = (end - start) / 1_000_000_000;

        System.out.println("Executed in " + elapsedTime + "second");
        System.out.println("______End of the Program______");
    }


    public static Connection createConnection(){
        Connection con = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url =
                "jdbc:mysql://localhost:" + portAddress + "/reddit?"
                + "rewriteBatchedStatements=true";
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


    static void insert(ParseAndInsert[] array) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(subredditInsertQuery);

        for (int i = 0; i < 100; i++){
            preparedStatement.setString(1, array[i].subreddit_id);
            preparedStatement.setString(2, array[i].subreddit);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();

        preparedStatement = connection.prepareStatement(postInsertQuery);

        for (int i = 0; i < 100; i++){
            preparedStatement.setString(1, array[i].link_id);
            preparedStatement.setString(2, array[i].subreddit_id);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();

        preparedStatement = connection.prepareStatement(commentInsertQuery);
        for (int i = 0; i < 100; i++) {
            preparedStatement.setString(1, array[i].id);
            preparedStatement.setString(2, array[i].parent_id);
            preparedStatement.setString(3, array[i].link_id);
            preparedStatement.setString(4, array[i].name);
            preparedStatement.setString(5, array[i].author);
            preparedStatement.setString(6, array[i].body);
            preparedStatement.setInt(7, array[i].score);
            preparedStatement.setString(8, array[i].created_utc);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }


    static ParseAndInsert parseJSONString(String line){
        JSONObject obj = new JSONObject(line);

        ParseAndInsert parseObj = new ParseAndInsert();

        parseObj.id = obj.getString(jsonKeys[0]);
        parseObj.parent_id = obj.getString(jsonKeys[1]);
        parseObj.link_id = obj.getString(jsonKeys[2]);
        parseObj.name = obj.getString(jsonKeys[3]);
        parseObj.author = obj.getString(jsonKeys[4]);
        parseObj.body = obj.getString(jsonKeys[5]);
        parseObj.subreddit_id = obj.getString(jsonKeys[6]);
        parseObj.subreddit = obj.getString(jsonKeys[7]);

        parseObj.epoch = obj.getInt(jsonKeys[8]);
        parseObj.created_utc = convertEpochToDateFormat(parseObj.epoch);

        parseObj.score = obj.getInt(jsonKeys[9]);

        return parseObj;
    }


    static String convertEpochToDateFormat(long epoch){
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        return dateTime.format(formatter);
    }
}

