package Assignment1;

/*
 * Rui Wang 10382363
 * Q2. For each combination of customer and product, compute the maximum and minimum sales quantities along with the corresponding dates 
 * (i.e., dates of those maximum and minimum sales quantities) and the state in which the sale transaction took place.
 * Q3. For the same combination of product and customer, compute the average sales quantity.
 * Answer:
 * [Instruct] 
 * For question2, I used arrays as the data structures of my program. 
 * For question3, I used arrays as the data structures of my program.  
 * I only read the database one time.
 * [The method]
 * 1. Read the data from the database to the local in my machine.
 * Of course, for assignment, use the Linux-lab-->postgres.cs.stevens.edu to execute the data
 * 2. Build the array of the customer and product to store the data read. each time only one row data read in.
 * In the while loop to distribute the data and deal with the relationship between the customer, product and quantity.
 * Build the array of quantMax, quantMin, quant, quantSum and sameNum.
 * For each loop, I will update the quantMax or quantMin if have the same customer name and product name 
 * as while the sameNum will be add 1 for this customer and product and the quantSum will add the quantity of upload data.
 * 3. Build the array of monthMax, dayMax, yearMax, stateMax, monthMin, dayMin, yearMin, stateMin 
 * For each data read in, I will update the array if have the same customer and product. If large, give the information for the Max things.
 * If small, give the information for the Min things. 
 * 4.For the data read in, if the customer array and product array don't have the same, then will add the new data to the customer and product array.
 * As while the information about this customer and product will add to the each array.
 * 5. When all of the 500 data read in, quantMax will store the value of max quantity for that customer and product same as month,day, year and state.
 * And the key is that there is only one match for the customer and product combination. In this case, there are 50 combination of customer and product.
 * And also use a loop the calculate the average quantity for each customer and product.
 * 6. Finally, format the output.
 * 7. Please remember to change the user name, password and url!!!!!
 */

import java.sql.*;

public class Assignment1_2and3 {

	public static void main(String[] args) {
		String usr = "postgres";
		String pwd = "abc";
		String url = "jdbc:postgresql://localhost:5432/postgres";

		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}

		catch (Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");

			
			// Build the array of the customer and product
			 
			String[] customer = new String[500];
			for (int i = 0; i < 500; i++) {
				customer[i] = " ";
			}
			String[] product = new String[500];
			for (int i = 0; i < 500; i++) {
				product[i] = " ";
			}

			// Build three array: quant to store the quantity after the calculate the average. quantMax to store the Max quant information. quantMin to store the Min quant information.
			int[] quant = new int[500];
			for (int i = 0; i < 500; i++) {
				quant[i] = 0;
			}
			
			int[] quantMax = new int[500];
			int[] quantMin = new int[500];

			int[] sameNum = new int[500];
			for (int i = 0; i < 500; i++) {
				sameNum[i] = 1;
			}
			
			int[] quantSum = new int[500];
			
			// Information about the month, day and year. Separate to the Max and Min to store the different array
			int[] monthMax = new int[500];
			int[] dayMax = new int[500];
			int[] yearMax = new int[500];
			String[] stateMax = new String[500];
			for (int i = 0; i < 500; i++) {
				stateMax[i] = " ";
			}
			int[] monthMin = new int[500];
			int[] dayMin = new int[500];
			int[] yearMin = new int[500];
			String[] stateMin = new String[500];
			for (int i = 0; i < 500; i++) {
				stateMin[i] = " ";
			}

			int arrayId = -1;

			while (rs.next()) {
				int a = 0;
				// For loop to ergodic every customer and product has been store in the array to found out whether have the same customer name and product name to array
				for (int i = 0; i < 500; i++) {
					// The method when read in the same customer and product. Store the information of date and quantity to the quantMax and quantMin. And add the sameNum
					if (rs.getString("cust").equals(customer[i])
							&& rs.getString("prod").equals(product[i])) {
						quantSum[i]=quantSum[i]+rs.getInt("quant");
						sameNum[i]++;
						quant[i] = rs.getInt("quant");
						if (quant[i] > quantMax[i]) {
							quantMax[i] = quant[i];
							monthMax[i] = rs.getInt("month");
							dayMax[i] = rs.getInt("day");
							yearMax[i] = rs.getInt("year");
							stateMax[i] = rs.getString("state");
						} else if (quant[i] < quantMin[i]) {
							quantMin[i] = quant[i];
							monthMin[i] = rs.getInt("month");
							dayMin[i] = rs.getInt("day");
							yearMin[i] = rs.getInt("year");
							stateMin[i] = rs.getString("state");
						}
						a = 1;
						break;
					}
				}
				// The method when read in the different customer and product. Add this customer and this product
				if (a == 0) {
					arrayId++;
					customer[arrayId] = rs.getString("cust");
					product[arrayId] = rs.getString("prod");
					quantMin[arrayId] = rs.getInt("quant");
					quantMax[arrayId] =rs.getInt("quant"); 
				    quant[arrayId] =rs.getInt("quant");
				    quantSum[arrayId] = rs.getInt("quant");
					monthMax[arrayId]  = rs.getInt("month"); 
					monthMin[arrayId] = rs.getInt("month");
					dayMax[arrayId] = rs.getInt("day"); 
					dayMin[arrayId] = rs.getInt("day");
					yearMax[arrayId] = rs.getInt("year"); 
					yearMin[arrayId] = rs.getInt("year");
					stateMax[arrayId] =  rs.getString("state");
					stateMin[arrayId] = rs.getString("state");
				}
			}

			// Calculate the average of the quantity.
			for (int i = 0; i < 500; i++) {
				quant[i] = quantSum[i] / sameNum[i];
			}

			/*
			 * Manage the output to the table:
			 * 1. Character string data (customer name and product name) are left justified.
			 * 2. Numeric date (Man/Min Sales quantities) are right justified.
			 * 3. The data fields are in the format of MM/DD/YYYY
			 */
			System.out
					.println("CUSTOMER  PRODUCT   MAX_Q  DATE        ST  MIN_Q  DATE        ST  AVG_Q");
			System.out
					.println("========  ========  =====  ==========  ==  =====  ==========  ==  =====");
			for (int i = 0; i < arrayId; i++) {
				//customer
				System.out.format("%-10s", customer[i]);
				//product
				System.out.format("%-10s", product[i]);
				//Max_Q
				System.out.format("%5d  ", quantMax[i]);
				//date			
				if(String.valueOf(monthMax[i]).length()==1){
					System.out.print("0");
					System.out.print(monthMax[i]);
				} else {
					System.out.print(monthMax[i]);
				};
				System.out.print("/");
				if(String.valueOf(dayMax[i]).length()==1){
					System.out.print("0");
					System.out.print(dayMax[i]);
				} else {
					System.out.print(dayMax[i]);
				};
				System.out.print("/");
				System.out.print(yearMax[i]);
				System.out.print("  ");
				//MAX_ST
				System.out.format("%2s", stateMax[i]);
				//Min_Q
				System.out.format("%7d  ", quantMin[i]);
				//date				
				if(String.valueOf(monthMin[i]).length()==1){
					System.out.print("0");
					System.out.print(monthMin[i]);
				} else {
					System.out.print(monthMin[i]);
				};
				System.out.print("/");
				if(String.valueOf(dayMin[i]).length()==1){
					System.out.print("0");
					System.out.print(dayMin[i]);
				} else {
					System.out.print(dayMin[i]);
				};
				System.out.print("/");
				System.out.print(yearMin[i]);
				System.out.print("  ");
				//Min_ST
				System.out.format("%2s", stateMin[i]);
				//AVE
				System.out.format("%7d\n", quant[i]);
			}
		} 

		catch(SQLException e) 
		{
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}
}
