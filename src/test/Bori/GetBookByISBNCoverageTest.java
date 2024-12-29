package test.Bori;

import Controller.CreateBillController;
import Model.Book;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

public class GetBookByISBNCoverageTest {
    @Test
    void testGetBookByISBN_NoImage() throws SQLException {
        // Setup
        String isbn = "123456789";
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("title")).thenReturn("Sample Book");
        when(rs.getString("category")).thenReturn("Fiction");
        when(rs.getDouble("selling_price")).thenReturn(19.99);
        when(rs.getString("author")).thenReturn("John Doe");
        when(rs.getInt("stock")).thenReturn(10);
        when(rs.getBlob("cover_image_path")).thenReturn(null);

        CreateBillController bc = new CreateBillController(conn);
        Book book = bc.getBookByISBN(isbn);
        assertNotNull(book);
        assertNull(book.getCoverImage());
    }

    @Test
    void testGetBookByISBN_WithImage() throws SQLException {
        String isbn = "987654321";
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        Blob coverImageBlob = mock(Blob.class);
        byte[] imageData = new byte[]{0, 1, 2, 3};

        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true); // Simulate book found
        when(rs.getString("title")).thenReturn("Sample Book With Image");
        when(rs.getString("category")).thenReturn("Non-fiction");
        when(rs.getDouble("selling_price")).thenReturn(25.99);
        when(rs.getString("author")).thenReturn("Jane Smith");
        when(rs.getInt("stock")).thenReturn(5);
        when(rs.getBlob("cover_image_path")).thenReturn(coverImageBlob); // Simulate image available
        when(coverImageBlob.getBytes(1, (int) coverImageBlob.length())).thenReturn(imageData);

        CreateBillController bc = new CreateBillController(conn);
        Book book = bc.getBookByISBN(isbn);


        assertNotNull(book);
        assertNotNull(book.getCoverImage());
    }

    @Test
    void testGetBookByISBN_NotFound() throws SQLException {
        String isbn = "000000000";
        Connection conn = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // Simulate no book found

        CreateBillController bc = new CreateBillController(conn);
        Book book = bc.getBookByISBN(isbn);

        assertNull(book);
    }
}
