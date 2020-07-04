package ashlynnsAngels.CyberAisling;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import ashlynnsAngels.modules.Greeter;

public class Bot {

	private String token;
	private String channelName;
	DiscordApi api;

	public Bot(String token, String channelName) {
		this.token = token;
		this.channelName = channelName;
	}

	public void connect() {
		api = new DiscordApiBuilder().setToken(token).login().join();
		System.out.println("Invite link: " + api.createBotInvite());
		api.getServerTextChannelsByName(channelName).forEach(e -> e.sendMessage("Bot Connected"));

		// add Listeners
		api.addMessageCreateListener(new Greeter(channelName));
	}

}
