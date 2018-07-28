package po;

import java.sql.*;
import java.util.*;

public class Emp {

	private String empname;
	private java.sql.Date birthday;
	private Integer id;
	private Integer dept;
	private Double salary;
	private Integer age;


	public String getEmpname(){
		return empname;
	}
	public java.sql.Date getBirthday(){
		return birthday;
	}
	public Integer getId(){
		return id;
	}
	public Integer getDept(){
		return dept;
	}
	public Double getSalary(){
		return salary;
	}
	public Integer getAge(){
		return age;
	}


	public void setEmpname(String empname){
		this.empname=empname;
	}
	public void setBirthday(java.sql.Date birthday){
		this.birthday=birthday;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public void setDept(Integer dept){
		this.dept=dept;
	}
	public void setSalary(Double salary){
		this.salary=salary;
	}
	public void setAge(Integer age){
		this.age=age;
	}


}
