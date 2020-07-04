package ashlynnsAngels.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

	@SerializedName("id")
	@Expose
	private long id;
	
	@SerializedName("hearts")
	@Expose
	private long hearts;
	
	@SerializedName("shares")
	@Expose
	private long shares;

	User(long id){
		this.id = id;
		this.hearts = 0;
		this.shares = 0;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHearts() {
		return hearts;
	}

	public void setHearts(long hearts) {
		this.hearts = hearts;
	}
	
	public long getShares() {
		return shares;
	}

	public void setShares(long shares) {
		this.shares = shares;
	}

}