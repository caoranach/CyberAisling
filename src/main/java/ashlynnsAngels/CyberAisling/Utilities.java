package ashlynnsAngels.CyberAisling;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Utilities {
	private Utilities() {
	}

	/**
	 * 
	 * @return list of bots
	 */
	public static Map<String, BotInfo> loadBotsFromJson() {
		try {
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/config.json");
			Reader reader = new InputStreamReader(is);
			System.out.println("Loaded config as JAR");
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(reader, new TypeToken<Map<String, BotInfo>>(){}.getType());
		} catch (Exception e) {
			System.out.println("Failed loading config as JAR, trying as IDE...");
			//e.printStackTrace();
		}
		try (Reader reader = new InputStreamReader(
				Utilities.class.getClassLoader().getResourceAsStream("testing/config.json"))) {
			System.out.println("Loaded config as IDE.");
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(reader, new TypeToken<Map<String, BotInfo>>(){}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("Failed to load bots");
		return null;
	}
}
