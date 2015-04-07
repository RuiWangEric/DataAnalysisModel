package Assignment2;

/*
 * Database Homework2.1:
 * Author: Rui Wang 
 * ID:10382363
 * Q1: For each customer and product, compute the customer's average sale of this product and the average sale of the product for the other customers.
 * Answer:
 * [Instruct]
 * For the Question1, I used HashMap data structure to solve the problem. 
 * I build an instance: Person1 who contains the two construct function Person1(String, String) for the combination of customer and product
 * and the Person1(String) for each product to calculate the average sale of this product.   
 * If there no person1 in the map, then I create a person based on the customer and product combination. 
 * Please change the usr, pwd and url to yourself!!! Thank you!
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

/*
 * Create a class for Person1 to store the information of the Person. 
 * Such as the customer name, product name, sum of the quant for this product, the number of the product, the sum and number of the quant for this customer and product.
 * There are two construct: Person1(String, String) to calculate the average sale of this customer and product.
 * Person1(String) to calculate the sum sale of the product and then to calculate the average sale of this product for the other customers.
 */
class Person1 {

	String customer, product;

	int sum, num;  					//sum and number for this customer and product
	int sumProd, numProd;			//sum and number for this product

	Person1(String customer, String product) {
		this.customer = customer;
		this.product = product;
		sum = num = 0;
	}

	Person1(String product) {
		this.product = product;
		sumProd = numProd = 0;
	}
	
}

public class Assignment2_1 {

	public static void main(String[] args) {

		String usr = "postgres";
		String pwd = "abc";
		String url = "jdbc:postgresql://localhost:5432/postgres";

		//Using the LinkedHashMap of map to store the each combination of customer and product.
		LinkedHashMap<String, Person1> map = new LinkedHashMap<String, Person1>();
		//Using the LinkedHashMap of mapProd to store each product sum and number.
		LinkedHashMap<String, Person1> mapProd = new LinkedHashMap<String, Person1>();
		
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

				//Choose the customer name and product name as the key of Person1
				String key = rs.getString("cust") + rs.getString("prod");
				//Choose the product name as the key for each product. 
				String keyProd = rs.getString("prod");

				//If the key is not in the LinkedHashMap map, then put the key to the map and the value is this Person1.
				if (!map.containsKey(key)) {
					Person1 person = new Person1(rs.getString("cust"), rs.getString("prod"));
					map.put(key, person);
				}
				//If the keyProd is not in the mapProd, then put the key to the mapProd  and the value is this Person1.
				if (!mapProd.containsKey(keyProd)) {
					Person1 person = new Person1(rs.getString("prod"));
					mapProd.put(keyProd, person);
				}

				//Get the value Person1 from the key.
 				Person1 temp = map.get(key);
 				//Get the value Person1 from the keyProd.
				Person1 tempProd = mapProd.get(keyProd);

				//For this customer and product the sum and number of the quant.
				temp.sum += Integer.parseInt(rs.getString("quant"));
				temp.num++;

				//For this product the sum and number of the quant.
				tempProd.sumProd += Integer.parseInt(rs.getString("quant"));
				tempProd.numProd++;

			}

			//Print out outcomes for the Qustion1. For the others_avg use the sum of this product- sum of this customer and product.
			System.out.println("CUSTOMER   PRODUCT    CUST_AVG    OTHERS_AVG");
			System.out.println("========   =======    ========    ==========");
			for (Person1 temp : map.values()) {
				for (Person1 tempProd : mapProd.values()) {
					if (temp.product.equals(tempProd.product)) {
						System.out.format("%-10s %-10s %8s %13s\n",temp.customer, temp.product, temp.sum/ temp.num,(tempProd.sumProd - temp.sum)/ (tempProd.numProd - temp.num));
					}
				}
			}

		} catch (SQLException e) {
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}

}
