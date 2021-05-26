package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select id,title,author,publisher,publish_date,thumbnail_url from books order by title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "SELECT * FROM books where id ="
                + bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        return bookDetailsInfo;
    }

    //bookIdを取得するメソッド
    //
    public int getBookId() {
        String sql = "SELECT MAX(id) FROM books";
        int getBookId = jdbcTemplate.queryForObject(sql, int.class);
        return getBookId;
    }



    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
     */
    public void registBook(BookDetailsInfo bookInfo) {

        String sql = "INSERT INTO books (title, author,publisher,publish_date,thumbnail_name,thumbnail_url,isbn,description,reg_date,upd_date) VALUES ('"
                + bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
                + bookInfo.getPublishDate() + "','"
                + bookInfo.getThumbnailName() + "','"
                + bookInfo.getThumbnailUrl() + "','"
                + bookInfo.getIsbn() + "','"
                + bookInfo.getDescription() + "',"
                + "sysdate(),"
                + "sysdate())";

        jdbcTemplate.update(sql); 
        
    }

    /**
     * 書籍を編集する
     * 
     * @param bookInfo 書籍情報
     */
    public void editBooks(BookDetailsInfo bookInfo) {

        String sql = "UPDATE books SET "
                + "TITLE ='" + bookInfo.getTitle() + "',"
                + "AUTHOR ='" + bookInfo.getAuthor() + "',"
                + "PUBLISHER ='" + bookInfo.getPublisher() + "',"
                + "PUBLISH_DATE = '" + bookInfo.getPublishDate() + "',"
                + "THUMBNAIL_URL ='" + bookInfo.getThumbnailUrl() + "',"
                + "THUMBNAIL_NAME ='" + bookInfo.getThumbnailName() + "',"
                + "UPD_DATE = sysdate(),"
                + "DESCRIPTION ='" + bookInfo.getDescription() + "',"
                + "ISBN = '" + bookInfo.getIsbn() + "'"
                + "WHERE ID =" + bookInfo.getBookId();

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を削除する
     * 
     * @param bookId 書籍ID
     */
    public void deleteBook(int bookId) {
        String sql = "delete from books where id =" + bookId;
        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を検索する
     * 
     * @param SearchKeyword 検索窓に入力されたキーワード
     */
    //完全一致検索の場合
    public List<BookInfo> getSearchBookList(String searchKeyWord) {

    List<BookInfo> getedBookList = jdbcTemplate.query(
            "select id,title,author,publisher,publish_date,thumbnail_url from books where title = " + "'"
                        + searchKeyWord + "'",
                new BookInfoRowMapper());

        return getedBookList;
    }

    //部分一致検索の場合
    public List<BookInfo> getPartiallySearchBookList(String searchKeyWord) {

        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select id,title,author,publisher,publish_date,thumbnail_url from books where title like '%"
                        + searchKeyWord + "%'",
                new BookInfoRowMapper());

        return getedBookList;
    }

    //貸出可能な書籍の書籍データを取得する
    /**貸出し可能な書籍のみを表示する
     * 
     * @return　貸出し可能な書籍リスト
     */
    public List<BookInfo> getPossibleList() {

        List<BookInfo> getedBookList = jdbcTemplate.query(
                "SELECT id,title,author,publisher,publish_date,thumbnail_url FROM books WHERE"
                        + " NOT EXISTS (SELECT books_id FROM lending WHERE lending.books_id = books.id)ORDER BY title ASC",
                new BookInfoRowMapper());

        return getedBookList;
    }
    }
