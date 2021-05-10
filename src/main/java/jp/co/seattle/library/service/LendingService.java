package jp.co.seattle.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 
 * 書籍貸出しサービス
 * 
 *lendingテーブルに関する処理を実装する
 *
 */

@Service
public class LendingService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 
     * 書籍を貸出す
     * 
     * @param bookId 書籍Id
     * 
     */
    public void rentBook(int bookId) {
        String sql = "INSERT INTO lending(books_id,rent_date) VALUES ("
                + bookId
                + ",sysdate())";

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍がすでに貸し出されているかチェックする
     * 
     * @param bookId 書籍Id
     * @return　getRentId　lendingテーブルに対象書籍IDがいくつあったかカウントした数
     */

    public int lendingConfirmation(int bookId) {

        String sql = "SELECT COUNT(ID) FROM lending WHERE BOOKS_ID =" + bookId;

        int getRentId = jdbcTemplate.queryForObject(sql, Integer.class);
        return getRentId;
    }

    /**
     * 書籍を返却する
     * @param bookId 書籍ID
     */

    public void returnBook(int bookId) {
        String sql = "DELETE FROM lending WHERE BOOKS_ID =" + bookId;

        jdbcTemplate.update(sql);
    }

}
