import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {
    private static final int CURRENCY_LENGTH = 3;
    private static final int DATE_LENGTH = 10;


    private static String readAll(BufferedReader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        String in;
        while ((in = rd.readLine()) != null) {
            sb.append(in);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


    public static void main(String[] args) {
        String url;
        String currency, startDate, endDate;
        JSONObject json;
        double averageBuy = 0, averageSell = 0, averageSellPow = 0;
        int count = 0;

        Scanner input = new Scanner(System.in);
        System.out.println("Write currency, starting date and end date:");
        String line = input.nextLine();
        currency = line.substring(0, CURRENCY_LENGTH);
        startDate = line.substring(CURRENCY_LENGTH + 1, CURRENCY_LENGTH + 1 + DATE_LENGTH);
        endDate = line.substring(CURRENCY_LENGTH + 1 + DATE_LENGTH + 1);

        try {
            url = "http://api.nbp.pl/api/exchangerates/rates/c/" + currency.toLowerCase() +
                    "/" + startDate + "/" + endDate + "/?format=json";
            json = readJsonFromUrl(url);
            JSONArray array = json.getJSONArray("rates");
            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                averageBuy += Double.parseDouble(jsonObject.get("bid").toString());
                averageSell += Double.parseDouble(jsonObject.get("ask").toString());
                averageSellPow += Math.pow(Double.parseDouble(jsonObject.get("ask").toString()), 2);
                count++;
            }
            averageBuy /= count;
            averageSell /= count;
            averageSellPow /= count;

            System.out.println(new DecimalFormat("#.####").format(averageBuy));
            System.out.println(new DecimalFormat("#.####").format(Math.sqrt((averageSellPow - averageSell * averageSell))));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
