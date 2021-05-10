package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


@Controller //APIの入り口
public class BulkBookController {
    final static Logger logger = LoggerFactory.getLogger(BulkBookController.class);

    @Autowired
    private BooksService booksService;

    @RequestMapping(value = "/bulkBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String bulkBook(Model model) {
        return "bulkBooks";
    }

    @Transactional
    @RequestMapping(value = "/bulkRegistBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String bulkRegistBook(Locale locale,
            @RequestParam("csvFile") MultipartFile file,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);


        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));) {
            String line;
            ArrayList<BookDetailsInfo> bookInfos = new ArrayList<BookDetailsInfo>();
            boolean isInValid = false;
            String errormsg = "";

            int i = 0;

            while ((line = br.readLine()) != null) {
                i++;

                String[] data = line.split(",");
                //バリデーションチェック
                boolean isTitleValid = !StringUtils.isEmpty(data[0]);
                boolean isAuthorValid = !StringUtils.isEmpty(data[1]);
                boolean isPublisherValid = !StringUtils.isEmpty(data[2]);
                boolean isPublishDateValid = !StringUtils.isEmpty(data[3]);

                boolean isDateValid = data[3].matches("^[0-9]*$");

                if (!(isTitleValid) || !(isAuthorValid) || !(isPublisherValid) || !(isPublishDateValid)) {
                    errormsg += i + "行目に必須項目が入力されていません\n";
                    isInValid = true;

                }
                if (!(isDateValid)) {
                    errormsg += i + "行目の出版日は半角数字で入力してください\n";
                    isInValid = true;

                }
                if (isDateValid) {
                    try {
                        DateFormat dt = new SimpleDateFormat("yyyyMMdd");
                        dt.setLenient(false);
                        // ←ここが画面などからの入力値になる
                        dt.parse(data[3]);

                    } catch (ParseException p) {
                        errormsg += i + "行目の日付が正しくありません\n";
                        isInValid = true;
                    }

                }

                if (!StringUtils.isEmpty(data[4])
                        && !(data[4].matches("([0-9]{10}|[0-9]{13})?"))) {
                    errormsg += i + "行目のISBNは10桁もしくは13桁の数字で入力してください。";
                    isInValid = true;
                }

                BookDetailsInfo bookInfo = new BookDetailsInfo();
                bookInfo.setTitle(data[0]);
                bookInfo.setAuthor(data[1]);
                bookInfo.setPublisher(data[2]);
                bookInfo.setPublishDate(data[3]);
                bookInfo.setIsbn(data[4]);
                bookInfo.setDescription(data[5]);


                bookInfos.add(bookInfo);
            }
            if (isInValid) {
                model.addAttribute("bulkError", errormsg);
                return "bulkBooks";
            }

            for (BookDetailsInfo book : bookInfos) {
                booksService.registBook(book);
            }
            model.addAttribute("resultMessage", "登録完了");
            return "bulkBooks";

        } catch (IOException e) {
            String error = "エラーです。";
            return error;


        } catch (Exception ioe) {
            String error = "エラーです";
            return error;
        }

    }
}
