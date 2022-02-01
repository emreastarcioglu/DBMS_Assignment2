import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main{
    public static void main(String[] args) throws IOException {

        File jsonFile = new File("RC_2007-10");
        FileReader fr = new FileReader(jsonFile);
        BufferedReader br = new BufferedReader(fr);

        String[] jsonKeys = {
                "id", "parent_id", "link_id", "name",
                "author", "body", "subreddit_id",
                "subreddit", "created_utc",
        };

        String line;
        for (int i = 0; i < 20; i++){
            line = br.readLine();
            printValues(line, jsonKeys);
        }
    }

    static void printValues(String line, String[] jsonKeys){

        JSONObject obj = new JSONObject(line);

        for (String key : jsonKeys){
            System.out.println(key + ": " + obj.getString(key));
        }
        System.out.println("score: " + obj.getBigInteger( "score"));

        printDashes(100);
        System.out.println();
    }

    static void printDashes(int n){
        for (int i = 0; i < n; i++)
            System.out.print("-");
    }
}

