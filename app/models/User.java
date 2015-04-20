package models;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
@Table(name="User")
public class User extends Model{
	
	private static final long serialVersionUID = 1910550446523952990L;
	
	@Id
	private UUID id;
	
	@Column(unique = true)
	private String email;
	
	@JsonIgnore
	private String password;
	
	@JsonIgnore
	private String salt;
	
	@JsonIgnore
	private Boolean totpEnabled = false;
	
	@JsonIgnore
	private String totpKey;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Boolean getTotpEnabled() {
		return totpEnabled;
	}

	public void setTotpEnabled(Boolean totpEnabled) {
		this.totpEnabled = totpEnabled;
	}

	public String getTotpKey() {
		return totpKey;
	}

	public void setTotpKey(String totpKey) {
		this.totpKey = totpKey;
	}	
	
}
