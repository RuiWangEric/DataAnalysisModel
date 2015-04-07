package Assignment1;

/*
 * Rui Wang 10382363
 * Q1. For each combination of customer and product, output the average sales quantities forNY, NJ and CT in 3 separate columns.
 * Answer:
 * [Instruct] 
 * For question1, I used arrays as the data structures of my program. 
 * I only read the database one time.
 * [The method]
 * 1. Read the data from the database to the local in my machine.
 * Of course, for assignment, use the Linux-lab-->postgres.cs.stevens.edu to execute the data
 * 2. Build the array of the customer and product to store the data read. each time only one row data read in.
 * In the while loop to distribute the data and deal with the relationship between the customer, product and quantity.
 * Build the array of quantNy, quantNj, quantCt,sameNumNy,sameNumNj,sameNumCt
 * For each loop, I will update the quantNy, quantNj and quantCt if have the same customer name and product name for different state
 * The most important thing is that I will judge whether the state is NY, NJ or CT. If not, it will not go into the loop.
 * 3. For the data read in, if the customer array and product array don't have the same, then will add the new data to the customer and product array.
 * As while the information about this customer and product will add to the each array.
 * 4. When all of the 500 data read in, quantMax will store the value of average quantity for that customer and product of each state.
 * And the key is that there is only one match for the customer and product combination. In this case, there are 50 combination of customer and product.
 * And also use a loop the calculate the average quantity for each customer and product for the state.
 * 5. Finally, format the output.
 * 7. Please remember to change the user name, password and url!!!!!  
 */

import java.sql.*;

public class Assignment1_1 {
	
	public static void main(String[] args) 
	{
		String usr = "postgres";
		String pwd = "abc";
		String url = "jdbc:postgresql://localhost:5432/postgres";
		
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

			//Build the array of the customer, product and state.
			 
			String[] customer = new String[500]; 
			for (int i = 0; i < 500; i++) {
				customer[i] = " ";
			}
			String[] product = new String[500];
			for (int i = 0; i < 500; i++) {
				product[i] = " ";
			}
			String[] state = new String[500];
			for (int i = 0; i < 500; i++) {
				state[i] = " ";
			}
			
			/*
			 * For the quantity, I separate to the quantNy, quantNj and quantCt to story the quantity for each customer and product. 
			 * About calculate the average of the quantity, I set up the array of the sameNumNy, sameNumNj, sameNumCt
			 */
			int[] quantNy = new int[500];
			int[] quantNj = new int[500];
			int[] quantCt = new int[500];
			int[] sameNumNy = new int[500];
			for (int i = 0; i < 500; i++) {
				sameNumNy[i] = 0;
			}
			int[] sameNumNj = new int[500];
			for (int i = 0; i < 500; i++) {
				sameNumNj[i] = 0;
			}
			int[] sameNumCt = new int[500];
			for (int i = 0; i < 500; i++) {
				sameNumCt[i] = 0;
			}
			int arrayId = -1;

			while (rs.next()) {
				// Fist, I judge that whether state is NY, NJ or CT, if not, don't go into the loop
				if (rs.getString("state").equals("NY")
						|| rs.getString("state").equals("NJ")
						|| rs.getString("state").equals("CT")) {
					int a = 0;
					// The method when read in the same customer and product. add the quantity and sameNum for each city
					// For loop to ergodic every customer and product has been store in the array to found out whether have the same customer name and product name
					for (int i = 0; i < 500; i++) {
						if (rs.getString("cust").equals(customer[i])
								&& rs.getString("prod").equals(product[i])) {
							if (rs.getString("state").equals("NY")) {
								quantNy[i] = quantNy[i] + rs.getInt("quant");
								sameNumNy[i]++;
							} else if (rs.getString("state").equals("NJ")) {
								quantNj[i] = quantNj[i] + rs.getInt("quant");
								sameNumNj[i]++;
							} else if (rs.getString("state").equals("CT")) {
								quantCt[i] = quantCt[i] + rs.getInt("quant");
								sameNumCt[i]++;
							}
							a = 1;
							break;
						}
					}
					//The method when read in the different customer and product. Add this customer and this product to the array
					if (a == 0) {
						arrayId++;
						customer[arrayId] = rs.getString("cust");
						product[arrayId] = rs.getString("prod");
						if (rs.getString("state").equals("NY")) {
							quantNy[arrayId] = rs.getInt("quant");
							sameNumNy[arrayId]++;
						} else if (rs.getString("state").equals("NJ")) {
							quantNj[arrayId] = rs.getInt("quant");
							sameNumNj[arrayId]++;
						} else if (rs.getString("state").equals("CT")) {
							quantCt[arrayId] = rs.getInt("quant");
							sameNumCt[arrayId]++;
						}
					}
				}
			}
			// If the same number equal to 0, add to 1 to calculate the average quantity. 
			for (int i = 0; i < 500; i++) {
				if (sameNumNy[i] == 0)
					sameNumNy[i] = 1;
				if (sameNumNj[i] == 0)
					sameNumNj[i] = 1;
				if (sameNumCt[i] == 0)
					sameNumCt[i] = 1;
			}
			
			
			// Calculate the average of the quantity.
			for (int i = 0; i < 500; i++) {
				quantNy[i] = quantNy[i] / sameNumNy[i];
				quantNj[i] = quantNj[i] / sameNumNj[i];
				quantCt[i] = quantCt[i] / sameNumCt[i];
			}
			
			
			/*
			 * Manage the output to the table:
			 * 1. Character string data (customer name and product name) are left justified.
			 * 2. Numeric date (Man/Min Sales quantities) are right justified.
			 * 3. The data fields are in the format of MM/DD/YYYY
			 */
			System.out.println("CUSTOMER  PRODUCT  NY_AVG  NJ_AVG  CT_AVG");
			System.out.println("========  =======  ======  ======  ======");
			
			
			for(int i=0; i<arrayId; i++) {
				//customer
				System.out.format("%-10s", customer[i]);
				//product
				System.out.format("%-9s", product[i]);
				//NY_AVG
				System.out.format("%6d", quantNy[i]);
				//NJ_AVG
				System.out.format("%8d", quantNj[i]);
				//CT_AVG
				System.out.format("%8d\n", quantCt[i]);

			}
	
		} catch (SQLException e) {
			System.out
					.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}

}
