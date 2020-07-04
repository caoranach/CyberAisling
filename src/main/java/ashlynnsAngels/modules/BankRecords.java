package ashlynnsAngels.modules;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankRecords {

	@SerializedName("users")
	@Expose
	private List<User> users = new ArrayList<User>();

	@SerializedName("shares")
	@Expose
	private long shares = 0;
	
	@SerializedName("lottoPool")
	@Expose
	private long lottoPool = 0;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public long getShares() {
		return this.shares;
	}
	
	public long getLottoPool() {
		return this.lottoPool;
	}
}