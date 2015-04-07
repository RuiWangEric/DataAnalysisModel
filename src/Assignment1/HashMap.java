package Assignment1;


/*
 * Database Homework 1
 * 
 * In this assignment, I choose HashMap as data structure to solve problem. Specifically I use LinkedHashMap,
 * which has predictable order.
 * 
 * If one record whose customer's name and product's name is not contained in the map, I create a Person instance
 * and put it into the map
 * 
 */


import java.sql.*;
import java.util.LinkedHashMap;

/*
 * create a class called Person which is used to every details of each record.
 * There's variable like name, product, maximum quantity, minimum quantity, and every state's product situation
 */
class Person{
	String name, product;
	
	//two array for calculate every state's average sales
	int[] stateSum = new int[4];		
	int[] stateNum = new int[4];
	
	int max_Q, min_Q;					//max_Q stores the maximum quantity and min_Q stores minimum quantity 
	String max_Date, min_Date;			//store the date when maximum/minimum quantity sales happened
	String max_State, min_State;		//store the state when maximum/minimum quantity sales happened
	int sum, num;						//two variable to help calculate average value
	
	Person(String name, String product){
		this.name = name;
		this.product = product;
		max_Q = Integer.MIN_VALUE;
		min_Q = Integer.MAX_VALUE;
		sum = num = 0;
	}
}

public class HashMap {

	public static void main(String[] args) 
	{
		String usr = "postgres";
		String pwd = "abc";
		String url = "jdbc:postgresql://localhost:5432/postgres";
		
		//Using a Hash map to store data which has been viewed
		//If one person with one product is not in the map, just put this information into the map
		LinkedHashMap<String, Person> map = new LinkedHashMap<String, Person>();
		
		try 
		{
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		} 

		catch(Exception e) 
		{
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try 
		{
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");

			while (rs.next()) 
			{
				//fetch one row of record and pick up its customer's name and product's name as key
				String key = rs.getString("cust") + rs.getString("prod");
				
		
				/*  If this key is not in the hash map, which means we have never meet that kind of record,
				 *  and then just put it into map 
				 */
				if(!map.containsKey(key)){
					Person person = new Person(rs.getString("cust"), rs.getString("prod"));
					map.put(key, person);
				}
				
				Person temp = map.get(key);
		
				//for assignment one, judge the product in which state. 0 for NY, 1 for NJ, 2 for CT, and 3 for others
				int select = 3;
				if(rs.getString("state").equals("NY")){
					select = 0;
				}else if(rs.getString("state").equals("NJ")) {
					select = 1;
				}else if(rs.getString("state").equals("CT")) {
					select = 2;
				}else{
					select = 3;
				}
				
				//In order to calculate each state of average amount, we need know the whole sum and item numbers
				temp.stateSum[select] += Integer.parseInt(rs.getString("quant"));
				temp.stateNum[select]++;
				
				//for assignment two, we should judge the maximum quantity and the minimum quantity.
				if(Integer.parseInt(rs.getString("quant")) > temp.max_Q){
					temp.max_Q = Integer.parseInt(rs.getString("quant"));
					String dateOfdd = rs.getString("day").length() <= 1 ? ("0"+rs.getString("day")) : rs.getString("day");
					String dateOfmm = rs.getString("month").length() <= 1 ? ("0"+rs.getString("month")) : rs.getString("month");
					temp.max_Date = dateOfmm+"/"+dateOfdd+"/"+rs.getString("year");
					temp.max_State = rs.getString("state");
				}
		
				if(Integer.parseInt(rs.getString("quant")) < temp.min_Q){
					temp.min_Q = Integer.parseInt(rs.getString("quant"));
					String dateOfdd = rs.getString("day").length() <= 1 ? ("0"+rs.getString("day")) : rs.getString("day");
					String dateOfmm = rs.getString("month").length() <= 1 ? ("0"+rs.getString("month")) : rs.getString("month");
					temp.min_Date = dateOfmm+"/"+dateOfdd+"/"+rs.getString("year");
					temp.min_State = rs.getString("state");
				}
				
			
				//calculate the sum and number of items in order to calculate average
				temp.sum += Integer.parseInt(rs.getString("quant"));
				temp.num++;
				
			}
			
			// print out assignment one outcomes:
			System.out.println("CUSTOMER   PRODUCT        NY_AVG    NJ_AVG    CT_AVG");
			System.out.println("========   =======        ======    ======    ======");
			for(Person temp : map.values()){
				System.out.format("%-10s %-10s",temp.name, temp.product);
				for(int i = 0; i < temp.stateSum.length-1; i++){
					if(temp.stateNum[i] == 0){
						System.out.format("%10d", 0);
						continue;
					}
					int ST_AVG = temp.stateSum[i]/temp.stateNum[i];
					System.out.format("%10d",ST_AVG);
				}
				System.out.println();
				
			}
			System.out.println("\n\n");
			
			
			//print out the assignment two outcomes:
			System.out.println("CUSTOMER PRODUCT      MAX_Q     DATE     ST     MIN_Q     DATE     ST     AVG_Q");
			System.out.println("======== =======      ====   ==========  ==     ====    ========   ==     =====");
			for(Person temp: map.values()){
				System.out.format("%-8s %-8s %8d %12s %3s %8d %12s %3s %8d\n", 
						temp.name, temp.product, temp.max_Q, temp.max_Date, temp.max_State, temp.min_Q, temp.min_Date, temp.min_State, temp.sum/temp.num);
			}
			
			
		} 

		catch(SQLException e) 
		{
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}

}
