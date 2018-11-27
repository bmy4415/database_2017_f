import java.sql.*;
import java.util.Scanner;

public class proj2 {
	static String serverName = "147.46.15.147";
	static String dbName = "db2012-11249";
	static String userName = "u2012-11249";
	static String password = "b43ac718f5a2"; //moon quiz사이트 참조
	static String url = "jdbc:mariadb://" + serverName + "/" + dbName;
	static Connection conn = null;
	static Scanner scan = new Scanner(System.in);
	
	private static void openConnection() {
		try {
			conn = DriverManager.getConnection(url, userName, password);
			return;
		}
		catch (SQLException e) {
			System.out.println("SQL exception occured in open" + e);
			conn = null;
			return;
		}
	}
	
	private static void closeConnection() {
		try {
			conn.close();
			return;
		}
		catch (SQLException e) {
			System.out.println("SQL exception occured in close" + e);
		}
	}
	
	private static void printMenu() {
		System.out.println("============================================================");
		System.out.println("1. print all buildings");
		System.out.println("2. print all performances");
		System.out.println("3. print all audiences");
		System.out.println("4. insert a new building");
		System.out.println("5. remove a building");
		System.out.println("6. insert a new performance");
		System.out.println("7. remove a performance");
		System.out.println("8. insert a new audience");
		System.out.println("9. remove an audience");
		System.out.println("10. assign a performance to a building");
		System.out.println("11. book a performance");
		System.out.println("12. print all performances which assigned at a building");
		System.out.println("13. print all audiences who booked for a performance");
		System.out.println("14. print ticket booking status of a performance");
		System.out.println("15. exit");
		System.out.println("============================================================");
		
	}
	
	public static void main(String[] args) {
		openConnection(); // set connection by using url, userNae, password
		if(conn == null) { return; } // connetion not made
		
		try {
			handleCommand();
		} catch (SQLException e) {
			System.out.println("SQL exception occured in command" + e);
		}
		
		scan.close();
		closeConnection();
	}
	
	private static void handleCommand() throws SQLException {
		int selectedAction;
		CommandHandler ch = new CommandHandler(conn);
		printMenu();
		while(true) {
			System.out.print("Select your action: ");
			selectedAction = scan.nextInt();
			
			if(selectedAction == 15) { System.out.println("Bye!"); break; }
			if(selectedAction > 15 || selectedAction <= 0) { System.out.println("Invalid action"); }
			
			switch(selectedAction) {
			case 1 :
				ch.printAllBuildings();
				break;
			case 2 :
				ch.printAllPerformances();
				break;
			case 3 :
				ch.printAllAudiences();
				break;
			case 4 :
				ch.insertNewBuilding();
				break;
			case 5 :
				ch.removeBuilding();
				break;
			case 6 :
				ch.insertNewPerformance();
				break;
			case 7 : 
				ch.removePerformance();
				break;
			case 8 :
				ch.insertNewAudience();
				break;
			case 9 : 
				ch.removeAudience();
				break;
			case 10 : 
				ch.assignPerformance();
				break;
			case 11 :
				ch.reservePerformance();
				break;
			case 12 :
				ch.printPerformanceOnBuilding();
				break;
			case 13 : 
				ch.printAllBookedAudience();
				break;
			case 14 : 
				ch.printBookedSeatInfo();
				break;
			}
			
			System.out.println();
		}
	}
}
