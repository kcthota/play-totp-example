package models;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

@Entity
@Table(name="BackupCodes")
public class BackupCodes extends Model{
	
	private static final long serialVersionUID = 1113590937346331431L;

	@Id
	private UUID userId;
	
	private Integer code1;
	
	private Integer code2;
	
	private Integer code3;
	
	private Integer code4;
	
	private Integer code5;

	
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public Integer getCode1() {
		return code1;
	}

	public void setCode1(Integer code1) {
		this.code1 = code1;
	}

	public Integer getCode2() {
		return code2;
	}

	public void setCode2(Integer code2) {
		this.code2 = code2;
	}

	public Integer getCode3() {
		return code3;
	}

	public void setCode3(Integer code3) {
		this.code3 = code3;
	}

	public Integer getCode4() {
		return code4;
	}

	public void setCode4(Integer code4) {
		this.code4 = code4;
	}

	public Integer getCode5() {
		return code5;
	}

	public void setCode5(Integer code5) {
		this.code5 = code5;
	}

	
	
	
}
