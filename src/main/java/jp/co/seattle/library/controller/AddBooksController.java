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
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class AddBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/addBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "addBook";
    }

    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
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
    @RequestMapping(value = "/insertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
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
            return "addBook";

        }
        if (!isDateValid) {
            model.addAttribute("PublisDateError", "出版日は半角数字で入力してください");
            return "addBook";

        }
        if (isDateValid) {
            try {
                DateFormat dt = new SimpleDateFormat("yyyyMMdd");
                dt.setLenient(false);

                dt.parse(publishDate);

            } catch (ParseException p) {
                model.addAttribute("dateError", "日付が正しくありません");
                return "addBook";
            }
        }


        //ISBNが10or13桁
        boolean isIsbnValid = !!StringUtils.isEmpty(isbn);
        //isbnがからじゃなかった時、半角数字かどうかチェック
        if (isIsbnValid) {
            boolean isValid = isbn.matches("^[0-9]*$");
            //isbnが10桁or13桁、半角数字かチェック
            int isbnNum = isbn.length();
            if (!isValid) {
                model.addAttribute("isbnError", " ISBNの桁数が違う、または半角数字ではありません");
                return "addBook";
            }
            if (isbnNum != 10 || isbnNum != 13) {
                model.addAttribute("isbnError", " ISBNの桁数が違う、または半角数字ではありません");
                return "addBook";
            }


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
                return "addBook";
            }
        }


        // 書籍情報を新規登録する
        booksService.registBook(bookInfo);

        model.addAttribute("resultMessage", "登録完了");

        // TODO 登録した書籍の詳細情報を表示するように実装
        //bookIdを取得するメソッドを使う
        //        booksService.getBookId();
        int bookId = booksService.getBookId();
        //書籍の詳細情報を取得する。 getBookInfo(int bookId)

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));

        //  詳細画面に遷移する
        return "details";
    }

}