package Assignment2;

/*
 * Database Homework2.2:
 * Author: Rui Wang 
 * ID:10382363
 * Q2: For customer and product, show the average sales before and after each quarter.The "YEAR" attribute is not considered for this query.
 * Answer:
 * [Instruct]
 * For the Question2, I used HashMap data structure to solve the problem. 
 * I build an instance: Person2 who contains the construct function Person1(String, String) for the combination of customer and product
 * I used the index 'select' for each quarter to separate the quant for each customer and product.
 * Please change the usr, pwd and url to yourself!!! Thank you!
 */

import java.sql.*;
import java.util.LinkedHashMap;

/*
 * Create a class for Person2 to store the information of the Person. 
 * Such as the customer name, product name, monthSum and monthNum for each quarter. 
 * There are two construct: Person2(String, String) to calculate the average sale of this customer and product for each quarter.
 */
class Person2 {
	
	String customer, product;
	
	int[] monthSum = new int[4];			//Sum of quant for each quarter
	int[] monthNum = new int[4];			//Number of quant for each quarter

	Person2 (String customer, String product) {
		this.customer = customer;
		this.product = product;
	}

}

public class Assignment2_2 {

	public static void main(String[] args) {

		String usr = "postgres";
		String pwd = "abc";
		String url = "jdbc:postgresql://localhost:5432/postgres";

		//Using the LinkedHashMap of map to store the each combination of customer and product.
		LinkedHashMap<String, Person2> map = new LinkedHashMap<String, Person2>();

		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		} catch (Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");

			
			while (rs.next()) {

				//Choose the customer name and product name as the key of Person2
				String key = rs.getString("cust") + rs.getString("prod");

				//If the key is not in the LinkedHashMap map, then put the key to the map and the value is this Person2.
				if (!map.containsKey(key)) {
					Person2 person = new Person2 (rs.getString("cust"),rs.getString("prod"));
					map.put(key, person);
				}

				//Get the value Person2 from the key.
				Person2 temp = map.get(key);

				//Giving a index 'select' to separate the quant to different quarters. 0 for Q1, 1 for Q2, 2 for Q3 and 3 for Q4.
				int select=4;
				if (Integer.parseInt(rs.getString("month")) > 0 && (Integer.parseInt(rs.getString("month")) < 4)) {
					select = 0;
				} else if (Integer.parseInt(rs.getString("month")) > 3 && (Integer.parseInt(rs.getString("month")) < 7)) {
					select = 1;
				} else if (Integer.parseInt(rs.getString("month")) > 6 && (Integer.parseInt(rs.getString("month")) < 10)) {
					select = 2;
				} else if (Integer.parseInt(rs.getString("month")) > 9 && (Integer.parseInt(rs.getString("month")) < 13)) {
					select = 3;
				}

				//To calculate the each quarter of the quant, I need to know the sum and number of the quant for each customer and product.
				temp.monthSum[select] += Integer.parseInt(rs.getString("quant"));
				temp.monthNum[select]++;
				
			}

			//Print out outcomes for the Question2. I need to consider the situation that the customer didn't sale this product in some quarter. 
			System.out.println("CUSTOMER   PRODUCT   QUARTER    BEFORE_AVG    AFTER_AVG");
			System.out.println("========   =======   =======    ==========    =========");
			for (Person2 temp : map.values()) {
				for (int select = 0; select < 4; select++) {
					System.out.format("%-10s %-10s", temp.customer,temp.product);
					if (select == 0) {
						if (temp.monthNum[select + 1] == 0) {
							System.out.format("%-10s %10s %12d\n", "Q1","<NULL>", 0);
						} else {
							System.out.format("%-10s %10s %12d\n", "Q1", "<NULL>", temp.monthSum[select + 1]/ temp.monthNum[select + 1]);
						}
					} else if (select == 1) {
						if (temp.monthNum[select - 1] == 0 || temp.monthNum[select + 1] == 0) {
							if (temp.monthNum[select - 1] == 0 && temp.monthNum[select + 1] == 0) {
								System.out.format("%-10s %10d %12d\n", "Q2", 0, 0);
							} else if(temp.monthNum[select - 1] == 0) {
								System.out.format("%-10s %10d %12d\n", "Q2", 0, temp.monthSum[select + 1]/ temp.monthNum[select + 1]);
							} else if (temp.monthNum[select + 1] == 0) {
								System.out.format("%-10s %10d %12d\n", "Q2",temp.monthSum[select - 1]/ temp.monthNum[select - 1], 0);
							}
						} else {
							System.out.format("%-10s %10d %12d\n", "Q2", temp.monthSum[select - 1]/ temp.monthNum[select - 1],temp.monthSum[select + 1]/ temp.monthNum[select + 1]);
						}
					} else if (select == 2) {
						if (temp.monthNum[select - 1] == 0 || temp.monthNum[select + 1] == 0) {
							if (temp.monthNum[select - 1] == 0 && temp.monthNum[select + 1] == 0) {
								System.out.format("%-10s %10d %12d\n", "Q3", 0, 0);
							} else if(temp.monthNum[select - 1] == 0) {
								System.out.format("%-10s %10d %12d\n", "Q3", 0, temp.monthSum[select + 1]/ temp.monthNum[select + 1]);
							} else if (temp.monthNum[select + 1] == 0) {
								System.out.format("%-10s %10d %12d\n", "Q3",temp.monthSum[select - 1]/ temp.monthNum[select - 1], 0);
							}
						} else {
							System.out.format("%-10s %10d %12d\n", "Q3",temp.monthSum[select - 1]/ temp.monthNum[select - 1],temp.monthSum[select + 1]/ temp.monthNum[select + 1]);
						}
					} else if (select == 3) {
						if (temp.monthNum[select - 1] == 0) {
							System.out.format("%-10s %10d %12s\n", "Q4", 0, "<NULL>");
						} else {
							System.out.format("%-10s %10d %12s\n", "Q4", temp.monthSum[select - 1]/ temp.monthNum[select - 1], "<NULL>");
						}
					}
				}

			}

		} catch (SQLException e) {
			System.out
					.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}

}
