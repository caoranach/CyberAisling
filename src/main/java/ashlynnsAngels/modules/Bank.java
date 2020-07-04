package ashlynnsAngels.modules;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ashlynnsAngels.CyberAisling.Utilities;

public class Bank {
	private BankRecords records;

	Bank() {
		records = readRecordsJSON();
	}

	public void reset() {
		records = new BankRecords();
		saveRecordsJSON();
	}

	public BankRecords getRecords() {
		return records;
	}

	private BankRecords readRecordsJSON() {
		try {
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("bank.json");
			Reader reader = new InputStreamReader(is);
			Gson gson = new GsonBuilder().create();
			System.out.println("Loaded Users as JAR");
			return gson.fromJson(reader, BankRecords.class);
		} catch (Exception e) {
			System.out.println("Failed loading bank as JAR, trying from IDE...");
			// e.printStackTrace();
		}
		try (Reader reader = new InputStreamReader(Utilities.class.getClassLoader().getResourceAsStream("bank.json"))) {
			System.out.println("Loading users as IDE");
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(reader, BankRecords.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("Failed to load Users");
		return new BankRecords();
	}

	public void setRecords(BankRecords records) {
		this.records = records;
	}

	public void saveRecordsJSON() {
		Gson gson = new GsonBuilder().create();
		String str = gson.toJson(records);
		try {
			PrintWriter out = new PrintWriter(new FileWriter("bank.json", false));
			out.print(str);
			out.close();
			System.out.println("Users saved!");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error saving users as JAR, trying from IDE...");
		}
		try {
			PrintWriter out = new PrintWriter(new FileWriter("src/main/resources/bank.json", false));
			out.print(str);
			out.close();
			System.out.println("Users saved!");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Error saving users.");
		}

	}

	public long getUserBalance(long id) {
		List<User> userList = records.getUsers();
		for (User u : userList) {
			if (u.getId() == id) {
				return u.getHearts();
			}
		}
		userList.add(new User(id));
		records.setUsers(userList);
		setRecords(records);
		return 0;
	}

	public void setUserBalance(long id, long hearts) {
		List<User> userList = records.getUsers();
		boolean userExists = false;
		for (User u : userList) {
			if (u.getId() == id) {
				u.setHearts(hearts);
				userExists = true;
			}
		}
		if (!userExists) {
			User newUser = new User(id);
			newUser.setHearts(hearts);
			userList.add(newUser);
		}
		records.setUsers(userList);
		setRecords(records);
	}

	public long getUserShares(long id) {
		List<User> userList = records.getUsers();
		for (User u : userList) {
			if (u.getId() == id) {
				return u.getShares();
			}
		}
		userList.add(new User(id));
		records.setUsers(userList);
		setRecords(records);
		return 0;
	}

	public void setUserShares(MessageCreateEvent event, long id, long shares) {
		List<User> userList = records.getUsers();
		for (User u : userList) {
			if (u.getId() == id) {
				u.setShares(shares);
			}
		}
		checkForNewOwner(event);
		records.setUsers(userList);
		setRecords(records);
	}
	public org.javacord.api.entity.user.User getUserById(MessageCreateEvent event, long id){
		org.javacord.api.entity.user.User user;
		try {
			user = event.getApi().getUserById(id).get();
			return user;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public void checkForNewOwner(MessageCreateEvent event) {
		long totalShares = getTotalSharesInCirculation();
		List<User> userList = records.getUsers();
		Server server = event.getServer().get();
		Role owner = server.getRoleById(690813028811276359L).get();
		for (User u : userList) {
			long shares = u.getShares();
			org.javacord.api.entity.user.User user;
			try {
				user = event.getApi().getUserById(u.getId()).get();
				boolean userIsNotOwner = true;
				for (Role r : user.getRoles(server)) {
					if (r == owner) {
						userIsNotOwner = false;
					}
				}

				if (shares > totalShares / 2) {
					if (userIsNotOwner) {
						user.addRole(owner);
						event.getChannel().sendMessage("**BOOM!!** " + user.getName() + " is Ash's new owner!");
					}
				} else {
					user.removeRole(owner);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}
	}

	public long getTotalHeartsInCirculation() {
		long total = 0;
		List<User> userList = records.getUsers();
		for (User u : userList) {
			total += u.getHearts();
		}
		return total;
	}

	public long getTotalSharesInCirculation() {
		long total = 0;
		List<User> userList = records.getUsers();
		total += records.getShares();
		for (User u : userList) {
			total += u.getShares();
		}
		return total;
	}

	public String getShareOwnersListAsString(MessageCreateEvent event) {
		Server server = event.getServer().get();
		List<User> users = getUserListSortedByShares();
		
		int longest = -1;
		for(User u : users) {
			String name = getUserById(event, u.getId()).getDisplayName(server);
			if(name.length() > longest) {
				longest = name.length();
			}
		}
		String tabs = "";
		for(int x = 0; x < longest-1; x++) {
			tabs += " ";
		}
		String underline = "";
		for(int x = 0; x < longest+6+tabs.length(); x++) {
			underline += "-";
		}
		String list = "`   Shareholders:\n"+"   NAME" + tabs + "  SHARES\n"+underline + "\n";


		int i = 1;
		for(User u : users) {
			String name = getUserById(event, u.getId()).getDisplayName(server);
			int tabLength = longest - name.length()+3;
			tabs = "";
			for(int x = 0; x < tabLength; x++) {
				tabs += " ";
			}
			list += i+". "+ name + ": " + tabs + u.getShares() + "\n";
			i++;
		}
		list+= "`";
		return list;
	}

	public List<User> getUserListSortedByShares() {
		List<User> newList = new ArrayList<User>();
		for(User u : records.getUsers()) {
			newList.add(u);
		}
		List<User> users = new ArrayList<User>();
		while (newList.size() > 0) {
			long largest = -1;
			int largestOwner = 0;
			for (int i = 0; i < newList.size(); i++) {
				if (newList.get(i).getShares() > largest) {
					largest = newList.get(i).getShares();
					largestOwner = i;
				}
			}
			users.add(newList.get(largestOwner));
			newList.remove(largestOwner);
		}

		return users;
	}

	public long getSharesPrice() {
		long price = (getTotalSharesInCirculation() / 3) + (getTotalHeartsInCirculation() / 100) + 1;
		return price;
	}
}
