package ashlynnsAngels.modules;

import java.time.Instant;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.javacord.api.event.message.MessageCreateEvent;

import net.aksingh.owmjapis.api.APIException;

public class Greeter extends CustomMessageCreateListener {
	Random gen = new Random();
	Bank bank;
	HashMap<Long, Instant> beggars = new HashMap<Long, Instant>();
	String wakeWord = "!ash";
	String[] commands = { "help", "balance", "bet", "beg", "shares", "buy", "lotto", "give", "sell", "trade" };
	String[] cheats = { "print" };
	String[] greetings = { "hi", "hello", "hey", "hai" };

	public Greeter(String channelName) {
		super(channelName);
		bank = new Bank();
	}

	@Override
	public void handle(MessageCreateEvent event) throws APIException {
		String[] message = event.getMessageContent().split(" ");
		if (message.length > 0) {
			if (message[0].equals(wakeWord)) {
				if (message.length > 1) {
					obeyCommand(event, message);
				} else {
					event.getChannel().sendMessage("You need to include a command, you bum!");
				}
			}
		}
	}

	void obeyCommand(MessageCreateEvent event, String[] arguments) {
		String command = arguments[1];
		long id = event.getMessageAuthor().getId();

		System.out.println("Message from:" + id + ", " + event.getMessageAuthor().getDisplayName());
		if (command.equals("resetAll") && id == 296103852699287554L) {
			bank.reset();
		} else if (command.equalsIgnoreCase(commands[0])) {
			event.getChannel()
					.sendMessage("I can greet you! Just say '**!ash hi!**' I'm trying to learn how to do more...\n"
							+ "I can also give your hearts and shares balance if you say '**!ash balance**'! It's your own bank of love! ;)\n"
							+ "You can bet some of your hearts by saying '**!ash bet #**', but I can't guarantee you'll win...\n"
							+ "Don't have hearts? Just say '**!ash beg**' and I'll give you some! Only once every minute or two though...\n"
							+ "If you wanna know how many shares have been bought, and their price, just say '**!ash shares**' to find out how many!\n"
							+ "You can give things to others by saying '**!ash give # <thing> <person>**'! You can give hearts or shares...sharing is caring!\n"
							+ "Just say '**!ash buy #**' to buy a number of shares to get some ownership of me...\n"
							+ "Selling shares is easy! Just say '**!ash sell #**' to part ways with that sweet sweet ownership stock...\n"
							+ "A lotto is coming soon!\n");
		} else if (command.equalsIgnoreCase(commands[1])) {
			displayBalance(event, id);
		} else if (command.equalsIgnoreCase(commands[2])) {
			makeBet(event, id, arguments);
			bank.saveRecordsJSON();
		} else if (command.equalsIgnoreCase(commands[3])) {
			beg(event, id, arguments);
			bank.saveRecordsJSON();
		} else if (command.equalsIgnoreCase(commands[4])) {
			showSharesInfo(event, id);
		} else if (command.equalsIgnoreCase(commands[5])) {
			buyShares(event, id, arguments);
			bank.saveRecordsJSON();
		} else if (command.equalsIgnoreCase(commands[6])) {
			// lotto
		} else if (command.equalsIgnoreCase(commands[7])) {
			giveHearts(event, id, arguments);
			bank.saveRecordsJSON();
		} else if (command.equalsIgnoreCase(commands[8])) {
			sellShares(event, id, arguments);
			bank.saveRecordsJSON();
		} else if (command.equalsIgnoreCase(commands[9])) {
			tradeShares(event, id, arguments);
			bank.saveRecordsJSON();
		} else if (command.equalsIgnoreCase(cheats[0]) && id == 296103852699287554L) {
			growHearts(event, id, arguments);
			bank.saveRecordsJSON();
		} else {
			greet(event, id, command);
		}
	}

	private boolean isValidCommand(MessageCreateEvent event, String[] arguments, String[] requirements) {
		if (arguments.length < requirements.length + 2) {
			// Message missing arguments!
			return false;
		}
		for (int i = 0; i < requirements.length; i++) {
			if (requirements[i].equals("long")) {
				if (!arguments[i + 2].matches("[0-9]+$")) {
					return false;
				} else {
				}
			} else if (requirements[i].equals("String")) {
				// eh
			} else if (requirements[i].equals("User")) {
				String sId = arguments[i + 2];
				sId = sId.replace('<', ' ').replace('>', ' ').replace('@', ' ').replace('!', ' ').trim();

				long id = Long.parseLong(sId);
				try {
					org.javacord.api.entity.user.User u = event.getApi().getUserById(id).get();
					if (u == null) {
						return false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				} catch (ExecutionException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		return true;
	}

	private void giveHearts(MessageCreateEvent event, long id, String[] arguments) {
		String[] reqs = { "long", "String", "User" };
		System.out.println(isValidCommand(event, arguments, reqs));
		if (isValidCommand(event, arguments, reqs)) {
			long count = Long.parseLong(arguments[2]);
			if (arguments[3].equalsIgnoreCase("Hearts")) {
				if (bank.getUserBalance(id) >= count) {
					bank.setUserBalance(id, bank.getUserBalance(id) - count);
					long recipientId = getIdFromPing(arguments[4]);
					bank.setUserBalance(recipientId, bank.getUserBalance(recipientId) + count);
					try {
						org.javacord.api.entity.user.User recipient = event.getApi().getUserById(recipientId).get();
						event.getChannel()
								.sendMessage("You gave " + count + " hearts to " + recipient.getName() + " aka "
										+ recipient.getDisplayName(event.getServer().get()) + "!\n"
										+ getDisplayBalanceString(id));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (arguments[3].equalsIgnoreCase("shares")) {
				if (bank.getUserShares(id) >= count) {
					bank.setUserShares(event, id, bank.getUserShares(id) - count);
					long recipientId = getIdFromPing(arguments[4]);
					bank.setUserShares(event, recipientId, bank.getUserShares(recipientId) + count);
					try {
						org.javacord.api.entity.user.User recipient = event.getApi().getUserById(recipientId).get();
						event.getChannel()
								.sendMessage("You gave " + count + " shares to " + recipient.getName() + " aka "
										+ recipient.getDisplayName(event.getServer().get()) + "!\n"
										+ getDisplayBalanceString(id));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				event.getChannel().sendMessage("What's a(n)" + arguments[3] + "?");
			}

		} else {
			event.getChannel().sendMessage(
					"Oops! The valid syntax is '!ash give <amount> <type> <recipient>\n<amount> is how many, <type> is hearts or shares, and <recipient> is a user ID! A ping will work~");
		}
	}

	private long getIdFromPing(String ping) {
		String sId = ping.replace('<', ' ').replace('>', ' ').replace('@', ' ').replace('!', ' ').trim();
		long id = Long.parseLong(sId);
		return id;
	}

	private void growHearts(MessageCreateEvent event, long id, String[] arguments) {
		// TODO Auto-generated method stub
		if (arguments.length > 2) {
			if (arguments[2].matches("[0-9]+$")) {
				long hearts = Long.parseLong(arguments[2]);
				bank.setUserBalance(id, bank.getUserBalance(id) + hearts);
				event.getChannel().sendMessage("You grew " + hearts + " more hearts!\n" + getDisplayBalanceString(id));
			}
		}
	}

	private void showSharesInfo(MessageCreateEvent event, long id) {
		long totalShares = bank.getTotalSharesInCirculation();
		long sharePrice = bank.getSharesPrice();
		String sharesInfo = bank.getShareOwnersListAsString(event);
		event.getChannel().sendMessage(
				"There are **__" + totalShares + "__** shares in circulation, and buying new shares costs **__" + sharePrice + "__** hearts!\n"
				+sharesInfo);
	}

	void buyShares(MessageCreateEvent event, long id, String[] arguments) {

		String[] reqs = { "long" };
		System.out.println(isValidCommand(event, arguments, reqs));

		if (arguments.length > 2) {
			if (arguments[2].matches("[0-9]+$")) {
				long desiredShares = Long.parseLong(arguments[2]);
				long sharePrice = bank.getSharesPrice();
				long cost = desiredShares * sharePrice;
				if (bank.getUserBalance(id) > 0 && bank.getUserBalance(id) >= cost) {
					bank.setUserBalance(id, bank.getUserBalance(id) - cost);
					bank.setUserShares(event, id, bank.getUserShares(id) + desiredShares);
					event.getChannel().sendMessage("You bought " + desiredShares + " shares at " + sharePrice
							+ " hearts each.\n" + "Your total cost was " + cost + ".\n" + getDisplayBalanceString(id));
				} else {
					event.getChannel().sendMessage("You don't have enough hearts...you need at least " + cost
							+ " hearts to buy " + desiredShares + "shares.");
				}
			} else {
				event.getChannel().sendMessage("That's not a valid number, you bum!");
			}
		} else {
			event.getChannel().sendMessage("You need to include a number, you bum!");
		}
	}

	void sellShares(MessageCreateEvent event, long id, String[] arguments) {
		String[] reqs = { "long" };
		System.out.println(isValidCommand(event, arguments, reqs));
		if (isValidCommand(event, arguments, reqs)) {
			long desiredShares = Long.parseLong(arguments[2]);
			if (bank.getUserShares(id) >= desiredShares) {
				long sharePrice = bank.getSharesPrice();
				long cost = desiredShares * sharePrice;
				bank.setUserBalance(id, bank.getUserBalance(id) + cost);
				bank.setUserShares(event, id, bank.getUserShares(id) - desiredShares);
				event.getChannel().sendMessage("You sold " + desiredShares + " shares at " + sharePrice
						+ " hearts each.\n" + "Your total profit was " + cost + ".\n" + getDisplayBalanceString(id));
			}
		}
	}

	void tradeShares(MessageCreateEvent event, long id, String[] arguments) {
		event.getChannel().sendMessage("I'm still working on that feature!");
	}

	String getDisplayBalanceString(long id) {
		long bal = bank.getUserBalance(id);
		long shares = bank.getUserShares(id);
		return "You have " + bal + " hearts, and " + shares + " shares!";
	}

	void displayBalance(MessageCreateEvent event, long id) {
		event.getChannel().sendMessage(getDisplayBalanceString(id));
	}

	void makeBet(MessageCreateEvent event, long id, String[] arguments) {

		String[] reqs = { "long" };
		String[] altReqs = { "String" };
		System.out.println(isValidCommand(event, arguments, reqs));

		if (arguments.length > 2) {
			if (arguments[2].matches("[0-9]+$")) {
				long bet = Long.parseLong(arguments[2]);
				if (bank.getUserBalance(id) > 0 && bank.getUserBalance(id) >= bet) {
					bet(event, id, bet);
				} else {
					event.getChannel().sendMessage("You don't have enough hearts for that... :c");
				}
			} else {
				if (arguments[2].equalsIgnoreCase("all")) {
					long bet = bank.getUserBalance(id);
					bet(event, id, bet);
				} else {
					event.getChannel().sendMessage("That's not a valid number, you bum!");
				}
			}
		} else {
			event.getChannel().sendMessage("You need to include a number, you bum!");
		}
	}

	private void bet(MessageCreateEvent event, long id, long bet) {
		int random = gen.nextInt(100);
		if (random > 50) {
			bank.setUserBalance(id, bank.getUserBalance(id) + bet);
			event.getChannel().sendMessage("You won! You earn " + bet + " hearts!\n" + getDisplayBalanceString(id));
		} else {
			bank.setUserBalance(id, bank.getUserBalance(id) - bet);
			event.getChannel()
					.sendMessage("You lost! Your " + bet + " hearts are mine now!\n" + getDisplayBalanceString(id));
		}
	}

	void beg(MessageCreateEvent event, long id, String[] arguments) {
		int gain = gen.nextInt(10) + 1;
		Instant time = event.getMessage().getCreationTimestamp();
		boolean canBeg = true;
		for (long beggar : beggars.keySet()) {
			if (id == beggar) {
				if (time.compareTo(beggars.get(beggar).plusSeconds(60)) > 0) {
					canBeg = true;
				} else {
					canBeg = false;
				}
			}
		}
		if (canBeg) {
			beggars.put(id, time);
			bank.setUserBalance(id, bank.getUserBalance(id) + gain);
			event.getChannel()
					.sendMessage("Okaaayyy, fiiine. Here's " + gain + " hearts.\n" + getDisplayBalanceString(id));
		} else {
			event.getChannel().sendMessage("You need to wait a little bit before doing that again...");
		}
	}

	void greet(MessageCreateEvent event, long id, String command) {
		boolean isGreeting = false;
		for (String greeting : greetings) {
			if (command.contains(greeting)) {
				int i = gen.nextInt(greetings.length);
				event.getChannel().sendMessage(greetings[i] + "! ;)");
				isGreeting = true;
				break;
			}
		}
		if (!isGreeting) {
			event.getChannel().sendMessage("I don't know what you're asking me to do...ask me to help!");
		}
	}
}
