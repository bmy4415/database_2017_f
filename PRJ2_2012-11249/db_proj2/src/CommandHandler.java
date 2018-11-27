import java.sql.*;
import java.util.*;

public class CommandHandler {
	Connection conn;
	
	public CommandHandler(Connection conn) {
		this.conn = conn;
	}
	
	private ArrayList<Integer> getSeatNumList(String s) {
		ArrayList<Integer> seatNumList = new ArrayList<Integer>();
		
		StringTokenizer st = new StringTokenizer(s, ",");
		
		while(st.hasMoreTokens()) { 
			String _seatNum = st.nextToken();
			int seatNum = Integer.parseInt(_seatNum.trim());
			seatNumList.add(seatNum);
		}
		
		return seatNumList;
	}
	
	private boolean seatNumValid(int p_id, ArrayList<Integer> seatNumList) throws SQLException {
		String sql = "select capacity from building where building.id = (select b_id from assign where p_id =  + " + p_id + ");";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int capacity = rs.getInt(1);
		
		for(Integer seatNum : seatNumList) {
			if(seatNum > capacity || seatNum < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	private ArrayList<Integer> getReservedSeats(int p_id) throws SQLException {
		ArrayList<Integer> reservedSeats = new ArrayList<Integer>();
		String sql = "select seat_num from reservation where p_id = " + p_id + ";";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			int seatNum = rs.getInt("seat_num");
			reservedSeats.add(seatNum);
		}
		
		return reservedSeats;
	}
	
	public void printAllBuildings() throws SQLException{
		//String sql = "select id, name, location, capacity, count(id) as assigned from building join assign on (building.id = assign.b_id) group by id order by id;";
		String sql = "select * from building order by id;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.printf("%-8s%-32s%-16s%-16s%-8s\n", "id", "name", "location", "capacity", "assigned");
		System.out.println("--------------------------------------------------------------------------------");

		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String location = rs.getString("location");
			int capacity = rs.getInt("capacity");
			//int assigned = rs.getInt("assigned");
			sql = "select count(p_id) as assigned from assign where b_id = " + id + ";";
			//System.out.println(sql);
			stmt = conn.prepareStatement(sql);
			ResultSet rs1 = stmt.executeQuery();
			rs1.next();
			int assigned = rs1.getInt(1);
			
			
			System.out.printf("%-8d%-32s%-16s%-16d%-8d\n", id, name, location, capacity, assigned);
		}
		System.out.println("--------------------------------------------------------------------------------");

		return;
	}
	
	public void printAllPerformances() throws SQLException {
		//String sql = "select id, name, type, price, count(*) as booked from performance join reservation on (performance.id = reservation.p_id) group by p_id, a_id order by(id);";
		String sql = "select * from performance order by id";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.printf("%-8s%-32s%-16s%-16s%-8s\n", "id", "name", "type", "price", "booked");
		System.out.println("--------------------------------------------------------------------------------");
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String type = rs.getString("type");
			int price = rs.getInt("price");
			// booked = rs.getInt("booked");
			sql = "select count(seat_num) as booked from reservation where p_id = " + id + ";";
			stmt = conn.prepareStatement(sql);
			ResultSet rs1 = stmt.executeQuery();
			rs1.next();
			int booked = rs1.getInt(1);
			
			System.out.printf("%-8d%-32s%-16s%-16d%-8d\n", id, name, type, price, booked);
		}
		System.out.println("--------------------------------------------------------------------------------");

		return;
	}
	
	public void printAllAudiences() throws SQLException {
		String sql = "select * from audience order by id;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.printf("%-8s%-40s%-16s%-16s\n", "id", "name", "gender", "age");
		System.out.println("--------------------------------------------------------------------------------");
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String gender = rs.getString("gender");
			int age = rs.getInt("age");
						
			System.out.printf("%-8d%-40s%-16s%-16d\n", id, name, gender, age);
		}
		System.out.println("--------------------------------------------------------------------------------");
		
		return;
	}
	
	public void insertNewBuilding() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Building name: ");
		String name = scan.nextLine();
		System.out.print("Building location: ");
		String location = scan.nextLine();
		System.out.print("Building capacity: ");
		int capacity = scan.nextInt(); scan.nextLine();
		
		if(capacity < 1) { 
			System.out.println("Capacity should be larger than 0"); 
			return;
		}
		
		String sql = "insert into building (name, location, capacity) values('" + name + "', '" + location + "', " + capacity + ");";
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.executeUpdate();
		System.out.println("A building is successfully inserted");
		return;
	}
	
	public void removeBuilding() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Building id: ");
		int id = scan.nextInt(); scan.nextLine();
		
		
		/* building existence check */
		String sql = "select count(*) from building where id = " + id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int bldg_id_cnt = rs.getInt(1);
		if(bldg_id_cnt == 0) {
			System.out.println("Building " + id + " doesn't exist");
			return;
		}
		/* building existence check end */
		
		
		sql = "delete from reservation where p_id in (select p_id from assign where b_id = " + id + ");";
		PreparedStatement stmt1 = conn.prepareStatement(sql);
		stmt1.executeUpdate();
		
		sql = "delete from building where id = " + id + ";";
		PreparedStatement stmt2 = conn.prepareStatement(sql);
		stmt2.executeUpdate();
		
		System.out.println("A building is successfully removed");
		return;
	}
	
	public void insertNewPerformance() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Performance name: ");
		String name = scan.nextLine();
		System.out.print("Performance type: ");
		String type = scan.nextLine();
		System.out.print("Performance price: ");
		int price = scan.nextInt(); scan.nextLine();
		
		if(price < 0) { 
			System.out.println("Price should be 0 or more"); 
			return;
		}
		
		String sql = "insert into performance (name, type, price) values('" + name + "', '" + type + "', " + price + ");";
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.executeUpdate();
		System.out.println("A performance is successfully inserted");
		return;
	}
	
	public void removePerformance() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Performance id: ");
		int id = scan.nextInt(); scan.nextLine();
		
		
		/* performance existence check */
		String sql = "select count(*) from performance where id = " + id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int perf_id_cnt = rs.getInt(1);
		if(perf_id_cnt == 0) {
			System.out.println("Performance " + id + " doesn't exist");
			return;
		}
		/* performance existence check end */
		
		sql = "delete from performance where id = " + id + ";";
		PreparedStatement stmt1 = conn.prepareStatement(sql);
		stmt1.executeUpdate();
		
		System.out.println("A performance is successfully removed");
		return;
	}
	
	public void insertNewAudience() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Audience name: ");
		String name = scan.nextLine();
		System.out.print("Audience gender: ");
		String gender = scan.nextLine();
		/* gender valid check */
		if(!(gender.equals("M") || gender.equals("F"))) {
			System.out.println("Gender should be 'M' or 'F'");
			return;
		}
		/* gender valid check end */
		
		System.out.print("Audience age: ");
		int age = scan.nextInt(); scan.nextLine();
		
		/* age valid check */
		if(age < 1) { 
			System.out.println("Age should be more than 0"); 
			return;
		}
		/* age valid check end */
		
		String sql = "insert into audience (name, gender, age) values('" + name + "', '" + gender + "', " + age + ");";
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.executeUpdate();
		System.out.println("An audience is successfully inserted");
		return;
	}
	
	public void removeAudience() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Audience id: ");
		int id = scan.nextInt(); scan.nextLine();
		
		/* audience existence check */
		String sql = "select count(*) from audience where id = " + id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int aud_id_cnt = rs.getInt(1);
		if(aud_id_cnt == 0) {
			System.out.println("Audience " + id + " doesn't exist");
			return;
		}
		/* audience existence check end */
		
		sql = "delete from audience where id = " + id + ";";
		PreparedStatement stmt1 = conn.prepareStatement(sql);
		stmt1.executeUpdate();
		
		System.out.println("An audience is successfully removed");
		return;
	}
	
	public void assignPerformance() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Building ID: ");
		int b_id = scan.nextInt(); scan.nextLine();
		
		/* building existence check */
		String sql = "select count(*) from building where id = " + b_id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int bldg_id_cnt = rs.getInt(1);
		if(bldg_id_cnt == 0) {
			System.out.println("Building " + b_id + " doesn't exist");
			return;
		}
		/* building existence check end */
		
		System.out.print("Performance ID: ");
		int p_id = scan.nextInt(); scan.nextLine();
		
		/* performance assignment check */
		sql = "select count(*) from assign where p_id = " + p_id + ";";
		stmt0 = conn.prepareStatement(sql);
		rs = stmt0.executeQuery();
		rs.next();
		int perf_id_cnt = rs.getInt(1);
		if(perf_id_cnt > 0) {
			System.out.println("Performance " + p_id + " is already assigned");
			return;
		}
		/* performance assignment check end */
		
		sql = "insert into assign values(" + b_id + ", " + p_id + ")";
		PreparedStatement stmt1 = conn.prepareStatement(sql);
		
		stmt1.executeUpdate();
		System.out.println("Successfully assign a performance");
		return;
	}
	
	public void reservePerformance() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Performance ID: ");
		int p_id = scan.nextInt(); scan.nextLine();
		
		/* performance existence check */
		String sql = "select count(*) from performance where id = " + p_id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int perf_id_cnt = rs.getInt(1);
		if(perf_id_cnt == 0) {
			System.out.println("Performance " + p_id + " doesn't exist");
			return;
		}
		/* performance existence check end */
		
		System.out.print("Audience ID: ");
		int a_id = scan.nextInt(); scan.nextLine();
		
		/* audience existence check */
		sql = "select count(*) from audience where id = " + a_id + ";";
		stmt0 = conn.prepareStatement(sql);
		rs = stmt0.executeQuery();
		rs.next();
		int aud_id_cnt = rs.getInt(1);
		if(aud_id_cnt == 0) {
			System.out.println("Audience " + a_id + " doesn't exist");
			return;
		}
		/* audience existence check end */
		
		System.out.print("Seat number: ");
		String _seatNumList = scan.nextLine();
		
		/* performance assignment check */
		sql = "select count(*) from assign where p_id = " + p_id + ";";
		PreparedStatement stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		int bldg_id_cnt = rs.getInt(1);
		if(bldg_id_cnt < 1) {
			System.out.println("Performance " + p_id + " isn't assigned");
			return;
		}
		/* performance assignment check end */
		
		/* seat number valid check */
		ArrayList<Integer> seatNumList = getSeatNumList(_seatNumList);
		if(!seatNumValid(p_id, seatNumList)) {
			System.out.println("Seat number out of range");
			return;
		}
		/* seat number valid check end */
		
		/* seat already reserved check */
		ArrayList<Integer> reservedSeats = getReservedSeats(p_id);
		for(Integer seatNum : seatNumList) {
			if(reservedSeats.contains(seatNum)) {
				System.out.println("The seat is already taken");
				return;
			}
		}
		/* seat already reserved check end */
		
		for(Integer seatNum : seatNumList) {
			sql = "insert into reservation values(" + p_id + ", " + a_id + ", " + seatNum + ");";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
		}
		
		
		sql = "select age from audience where id = " + a_id + ";";
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		int age = rs.getInt(1);
		
		sql = "select price from performance where id = " + p_id + ";";
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		int price = rs.getInt(1);
		
		int seatNumListSize = seatNumList.size();
		
		
		System.out.println("Successfully book a performance");
		if(age <= 7) {
			System.out.println("Total ticket price is " + Math.round(seatNumListSize * price * 0));
		} else if(age <= 12) {
			System.out.println("Total ticket price is " + Math.round(seatNumListSize * price * 0.5));
		} else if(age <= 18) {
			System.out.println("Total ticket price is " + Math.round(seatNumListSize * price * 0.8));
		} else {
			System.out.println("Total ticket price is " + Math.round(seatNumListSize * price * 1));
		}
		
	}
	
	public void printPerformanceOnBuilding() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Building ID: ");
		int b_id = scan.nextInt(); scan.nextLine();
		
		/* building existence check */
		String sql = "select count(*) from building where id = " + b_id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int bldg_id_cnt = rs.getInt(1);
		if(bldg_id_cnt == 0) {
			System.out.println("Building " + b_id + " doesn't exist");
			return;
		}
		/* building existence check end */
		
		//sql = "select id, name, type, price, count(seat_num) as booked from performance, reservation where id in (select p_id from assign where b_id = " + b_id + ") and id = reservation.p_id order by id;";
		sql = "select * from performance where id in (select p_id from assign where b_id = " + b_id + ") order by id;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.printf("%-8s%-32s%-16s%-16s%-8s\n", "id", "name", "type", "price", "booked");
		System.out.println("--------------------------------------------------------------------------------");

		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String type = rs.getString("type");
			int price = rs.getInt("price");
			//int booked = rs.getInt("booked");
			sql = "select count(seat_num) as booked from reservation where p_id = " + id + ";";
			stmt = conn.prepareStatement(sql);
			ResultSet rs1 = stmt.executeQuery();
			rs1.next();
			int booked = rs1.getInt(1);
			
			System.out.printf("%-8d%-32s%-16s%-16d%-8d\n", id, name, type, price, booked);
		}
		System.out.println("--------------------------------------------------------------------------------");

		return;		
	}
	
	public void printAllBookedAudience() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Performance ID: ");
		int p_id = scan.nextInt(); scan.nextLine();
		
		/* performance existence check */
		String sql = "select count(*) from performance where id = " + p_id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int perf_id_cnt = rs.getInt(1);
		if(perf_id_cnt == 0) {
			System.out.println("Performance " + p_id + " doesn't exist");
			return;
		}
		/* performance existence check end */
		
		sql = "select id, name, gender, age from audience where id in (select a_id from reservation where p_id = " + p_id + " group by a_id) order by id;";
		stmt0 = conn.prepareStatement(sql);
		rs = stmt0.executeQuery();
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.printf("%-8s%-40s%-16s%-16s\n", "id", "name", "gender", "age");
		System.out.println("--------------------------------------------------------------------------------");
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String gender = rs.getString("gender");
			int age = rs.getInt("age");
						
			System.out.printf("%-8d%-40s%-16s%-16d\n", id, name, gender, age);
		}
		System.out.println("--------------------------------------------------------------------------------");
		
		return;
	}
	
	public void printBookedSeatInfo() throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Performance ID: ");
		int p_id = scan.nextInt(); scan.nextLine();
		
		/* performance existence check */
		String sql = "select count(*) from performance where id = " + p_id + ";";
		PreparedStatement stmt0 = conn.prepareStatement(sql);
		ResultSet rs = stmt0.executeQuery();
		rs.next();
		int perf_id_cnt = rs.getInt(1);
		if(perf_id_cnt == 0) {
			System.out.println("Performance " + p_id + " doesn't exist");
			return;
		}
		/* performance existence check end */
		
		/* performance assignment check */
		sql = "select count(*) from assign where p_id = " + p_id + ";";
		PreparedStatement stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		int bldg_id_cnt = rs.getInt(1);
		if(bldg_id_cnt < 1) {
			System.out.println("Performance " + p_id + " isn't assigned");
			return;
		}
		/* performance assignment check end */
		
		sql = "select capacity from building where id = (select b_id from assign where p_id = " + p_id + ");";
		stmt = conn.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		int capacity = rs.getInt(1);
		
		System.out.println("--------------------------------------------------------------------------------");
		System.out.printf("%-40s%-40s\n", "seat_number", "audience_id");
		System.out.println("--------------------------------------------------------------------------------");
		
		for(int i=1; i<=capacity; i++) {
			sql = "select count(a_id) as id, a_id from reservation where p_id = " + p_id + " and seat_num = " + i + ";";
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			rs.next();
			int cnt = rs.getInt(1);
			if(cnt == 0) {
				System.out.printf("%-40s\n", i);
			}
			else {
				int a_id = rs.getInt(2);
				System.out.printf("%-40s%-40s\n", i, a_id);
			}
		}
		
		System.out.println("--------------------------------------------------------------------------------");
		return;
	}
}
