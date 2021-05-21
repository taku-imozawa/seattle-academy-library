package jp.co.seattle.library.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

/**
 * 書籍編集コントローラー
 */
@Controller
public class EditController {
    final static Logger logger = LoggerFactory.getLogger(EditController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    /**
     * 編集画面に遷移する
     * @param locale
     * @param bookId
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/editBook", method = RequestMethod.POST)
    public String detailsBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        // デバッグ用ログ
        logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

        model.addAttribute("bookInfo", booksService.getBookInfo(bookId));

        return "editBook";
    }

    /**
     * 書籍情報を編集する
     * @param locale ロケール情報
     * @param bookId 書籍id
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param description 説明
     * @param publishDate 出版日
     * @param isbn ISBN
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/updateBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("publishDate") String publishDate,
            @RequestParam("description")String description, 
            @RequestParam("isbn") String isbn,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);
        
        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setBookId(bookId);
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setPublishDate(publishDate);
        bookInfo.setIsbn(isbn);
        bookInfo.setDescription(description);

        //必須項目,タイトル、著者名、出版社、出版日がNULLじゃないか、空文字になっていないかチェックする
        boolean isTitleValid = !StringUtils.isEmpty(title);
        boolean isAuthorValid = !StringUtils.isEmpty(author);
        boolean isPublisherValid = !StringUtils.isEmpty(publisher);
        boolean isPublishDateValid = !StringUtils.isEmpty(publishDate);

        //出版日がYYYYMMDD形式かどうか。存在する日にちかどうか確認。半角数字
        boolean isDateValid = publishDate.matches("^[0-9]*$");

        if (!(isTitleValid) || !(isAuthorValid) || !(isPublisherValid) || !(isPublishDateValid)) {
            model.addAttribute("addError", "必須項目が入力されていません");
            return "editBook";

        }
        if (!isDateValid) {
            model.addAttribute("PublisDateError", "出版日は半角数字で入力してください");
            return "editBook";

        }
        if (isDateValid) {
            try {
                DateFormat dt = new SimpleDateFormat("yyyyMMdd");
                dt.setLenient(false);
                // ←ここが画面などからの入力値になる
                dt.parse(publishDate);

            } catch (ParseException p) {
                model.addAttribute("dateError", "出版日は半角数字のYYYYMMDD形式で入力してください");
                return "editBook";
            }
        }

        if (!StringUtils.isEmpty(isbn) && !(isbn.matches("([0-9]{10}|[0-9]{13})?"))) {
            model.addAttribute("isbnError", " ISBNの桁数が違う、または半角数字ではありません");
            return "editBook";
        }

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookDetailsInfo", bookInfo);
                return "editBook";
            }
        }
        //変更された書籍情報をデータベースで更新する

        booksService.editBooks(bookInfo);

        model.addAttribute("resultMessage", "更新完了");

        //model.addAttribute("editMessage", "編集完了");

        //変更した書籍の詳細情報を表示するように実装
        //
        // int bookId = booksService.getBookId();
        model.addAttribute("bookDetailsInfo", bookInfo);
        //貸出ステータスの表示。貸出中の書籍は編集できないように実装するため、貸し出し可を出力
        model.addAttribute("RentStatus", "貸出可");

        //詳細画面に遷移する
        return "details";

    }
}

