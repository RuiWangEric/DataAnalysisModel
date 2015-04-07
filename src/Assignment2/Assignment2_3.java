package Assignment2;

/*
 * Database Homework2.3:
 * Author: Rui Wang 
 * ID:10382363
 * Q3: For customer and product, count for each quarter, how many sales of the previous and how many sales of the following quarter had quantities between that quarter's average sale and maximum sale.
 * Answer:
 * [Instruct]
 * For the Question3, I used HashMap data structure to solve the problem. 
 * I build an instance: Person3 who contains the construct function Person1(String, String) for the combination of customer and product
 * I used the index 'select' for each quarter to separate the quant for each customer and product.
 * Please change the usr, pwd and url to yourself!!! Thank you!
 */

import java.sql.*;
import java.util.LinkedHashMap;

/*
 * Create a class for Person3 to store the information of the Person. 
 * Such as the customer name, product name, monthSum and monthNum for each quarter, max_Quant, avg_Quant and count.
 * There are two construct: Person2(String, String) to calculate the average sale of this customer and product for each quarter.
 */
class Person3 {
	
	String customer, product;
	
	int[] monthSum = new int[4];			//Sum of quant for each quarter
	int[] monthNum = new int[4];			//Number of quant for each quarter
	
	int[] max_Quant = new int[4];			//The Max quant for each quarter
	int[] avg_Quant = new int[4];			//The average quant for each quarter
	int[][] count = new int[4][2];			//Count for the each quarter of previous and following quarter between that quarter's average sale and maximum sale.
	
	Person3 (String customer, String product) {
		this.customer = customer;
		this.product = product;
		for(int i = 0;i<4;i++){
		max_Quant[i] = Integer.MIN_VALUE;
		}
	}
}

public class Assignment2_3 {

	public static void main(String[] args) {

		String usr = "postgres";
		String pwd = "abc";
		String url = "jdbc:postgresql://localhost:5432/postgres";

		//Using the LinkedHashMap of map to store the each combination of customer and product.
		LinkedHashMap<String, Person3> map = new LinkedHashMap<String, Person3>();

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

				//Choose the customer name and product name as the key of Person3.
				String key = rs.getString("cust") + rs.getString("prod");

				//If the key is not in the LinkedHashMap map, then put the key to the map and the value is this Person3.
				if (!map.containsKey(key)) {
					Person3 person = new Person3 (rs.getString("cust"),rs.getString("prod"));
					map.put(key, person);
				}

				//Get the value Person3 from the key.
				Person3 temp = map.get(key);

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
				
				//For the quant for each quarter, I have to judge that whether the quant is larger than the max_quant, if not, change the max_quant for this quarter of this customer and product.
				if (Integer.parseInt(rs.getString("quant")) > temp.max_Quant[select]) {
					temp.max_Quant[select] = Integer.parseInt(rs.getString("quant"));
				}

			}
			
			//Judge that for each quarter whether there are quant for each customer and product. If not, set avg_Quant equals to zero, otherwise use the monthSum/monthNum to calculate the avg_quant. 
			for (Person3 temp : map.values()) {
				for(int i=0; i<4; i++) {
					if(temp.monthNum[i] == 0) {
						temp.avg_Quant[i] = 0;
					}else if (temp.monthNum[i]!=0){
						temp.avg_Quant[i] = temp.monthSum[i]/temp.monthNum[i];
					}
				}
			}
			
			//This is the second time to scan the SQL.
			Statement stmt1 = conn.createStatement();
			ResultSet rs1 = stmt1.executeQuery("SELECT * FROM Sales");
			while (rs1.next()) {
				
				//Choose the customer name and product name as the key of Person3.
				String key = rs1.getString("cust") + rs1.getString("prod");

				//If the key is not in the LinkedHashMap map, then put the key to the map and the value is this Person3.
				if (!map.containsKey(key)) {
					Person3 person = new Person3(rs1.getString("cust"),rs1.getString("prod"));
					map.put(key, person);
				}

				//Get the value Person3 from the key.
				Person3 temp = map.get(key);

				//Giving a index 'select' to separate the quant to different quarters. 0 for Q1, 1 for Q2, 2 for Q3 and 3 for Q4.
				int select = 4;
				if (Integer.parseInt(rs1.getString("month")) > 0 && (Integer.parseInt(rs1.getString("month")) < 4)) {
					select = 0;
				} else if (Integer.parseInt(rs1.getString("month")) > 3 && (Integer.parseInt(rs1.getString("month")) < 7)) {
					select = 1;
				} else if (Integer.parseInt(rs1.getString("month")) > 6 && (Integer.parseInt(rs1.getString("month")) < 10)) {
					select = 2;
				} else if (Integer.parseInt(rs1.getString("month")) > 9 && (Integer.parseInt(rs1.getString("month")) < 13)) {
					select = 3;
				}

				//First, I should judge the avg_Quant equals to zero, if not, count for the number satisfied with condition between the average and maximum sales for that quarter.
				if (select == 0 && temp.avg_Quant[select + 1] != 0) {
					if (Integer.parseInt(rs1.getString("quant")) >= temp.avg_Quant[select + 1] && Integer.parseInt(rs1.getString("quant")) <= temp.max_Quant[select + 1]) {
						temp.count[select + 1][0]++;
					}
				} else if (select == 1) {
					if (temp.avg_Quant[select - 1] != 0) {
						if (Integer.parseInt(rs1.getString("quant")) >= temp.avg_Quant[select - 1] && Integer.parseInt(rs1.getString("quant")) <= temp.max_Quant[select - 1]) {
							temp.count[select - 1][1]++;
						}
					}
					if (temp.avg_Quant[select + 1] != 0) {
						if (Integer.parseInt(rs1.getString("quant")) >= temp.avg_Quant[select + 1] && Integer.parseInt(rs1.getString("quant")) <= temp.max_Quant[select + 1]) {
							temp.count[select + 1][0]++;
						}
					}
				}
				if (select == 2) {
					if (temp.avg_Quant[select - 1] != 0) {
						if (Integer.parseInt(rs1.getString("quant")) >= temp.avg_Quant[select - 1] && Integer.parseInt(rs1.getString("quant")) <= temp.max_Quant[select - 1]) {
							temp.count[select - 1][1]++;
						}
					}
					if (temp.avg_Quant[select + 1] != 0) {
						if (Integer.parseInt(rs1.getString("quant")) >= temp.avg_Quant[select + 1] && Integer.parseInt(rs1.getString("quant")) <= temp.max_Quant[select + 1]) {
							temp.count[select + 1][0]++;
						}
					}
				} else if (select == 3 && temp.avg_Quant[select - 1] != 0) {
					if (Integer.parseInt(rs1.getString("quant")) >= temp.avg_Quant[select - 1] && Integer.parseInt(rs1.getString("quant")) <= temp.max_Quant[select - 1]) {
						temp.count[select - 1][1]++;
					}
				}

			}
			
			//Print out outcomes for the Question3.
			System.out.println("CUSTOMER   PRODUCT   QUARTER    BEFORE_TOT    AFTER_TOT");
			System.out.println("========   =======   =======    ==========    =========");
			for (Person3 temp : map.values()) {
				for (int select = 0; select < 4; select++) {
					System.out.format("%-10s %-10s", temp.customer, temp.product);
					if (select == 0) {
						System.out.format("%-10s %10s %12d\n", "Q1", "<NULL>", temp.count[select][1]);						
					} else if (select == 1) {
						System.out.format("%-10s %10d %12d\n", "Q2", temp.count[select][0],temp.count[select][1]);							
					} else if (select == 2) {
						System.out.format("%-10s %10d %12d\n", "Q3", temp.count[select][0], temp.count[select][1]);
					} else if (select == 3) {
						System.out.format("%-10s %10d %12s\n", "Q4", temp.count[select][0], "<NULL>");
					}
				}
			}

		} catch (SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}

}
