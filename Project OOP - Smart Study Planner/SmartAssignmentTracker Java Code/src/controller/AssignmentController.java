package controller;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignmentController {

	// -------------------------------------------------------------
	// FEATURE 1: THE SOFT DELETE
	// Fulfills the "Soft Delete" requirement 
	// -------------------------------------------------------------
	public void softDeleteAssignment(int assignmentId) {
		// We DO NOT use "DELETE FROM". We just update the status to 0 (False).
		String sql = "UPDATE assignments SET is_active = 0 WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, assignmentId);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Success: Assignment hidden from UI, but kept in Database backup!");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------
	// FEATURE 2: VIEW / DISPLAY DATA
	// Fulfills the "View/Display Data" requirement 
	// -------------------------------------------------------------
	public void loadTableData() {
		// The magic happens here: We ONLY select rows where is_active is 1 (True).
		// Anything that was soft-deleted (0) is completely ignored by the GUI.
		String sql = "SELECT * FROM assignments WHERE is_active = 1";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				String title = rs.getString("title");
				String course = rs.getString("course_code");
				// Add these to your DefaultTableModel to display in the GUI
				System.out.println("Loading Active Task: " + title + " for " + course);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------
	// FEATURE 3: UPDATE / EDIT DATA
	// -------------------------------------------------------------
	public void updateAssignment(int id, String newTitle, String newUrgency) {
		String sql = "UPDATE assignments SET title = ?, urgency_level = ? WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, newTitle);
			pstmt.setString(2, newUrgency);
			pstmt.setInt(3, id);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Success: Assignment Updated!");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------
	// FEATURE 4: SEARCH DATA
	// -------------------------------------------------------------
	public void searchAssignments(String keyword) {
		// Uses the LIKE operator to find partial matches, ignoring soft-deleted items
		String sql = "SELECT * FROM assignments WHERE title LIKE ? AND is_active = 1";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// The % symbols act as wildcards (e.g., searching "Lab" finds "Lab 1", "Physics Lab")
			pstmt.setString(1, "%" + keyword + "%");

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					System.out.println("Search Match Found: " + rs.getString("title"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

} // <-- The closing bracket was moved down here to safely enclose all methods!