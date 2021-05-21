package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.LendingService;

/**
 * 
 * 書籍貸出しコントローラー
 *
 */
@Controller //APIの入り口
public class RentBooksController {
    final static Logger logger = LoggerFactory.getLogger(RentBooksController.class);

    @Autowired
    private LendingService lendingService;

    @Autowired
    private BooksService bookdService;




    /**
     * 対象書籍を貸し出す
     * 
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */

    @Transactional
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST)
    public String rentBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome rent! The client locale is {}.", locale);

        lendingService.rentBook(bookId);
        //書籍の詳細を表示させる
        model.addAttribute("bookDetailsInfo", bookdService.getBookInfo(bookId));
        //「貸出中」ステータスを表示させる
        model.addAttribute("RentStatus", "貸出中");
        //借りるボタンの非活性化
        model.addAttribute("RentDisable", "disabled");
        //書籍が貸し出されたら削除ボタンを非活性化する
        model.addAttribute("DeleteDisable", "disabled");
        //貸出中の書籍は削除できない旨をメッセージ表示する
        model.addAttribute("DeleteError", "※貸出中の書籍は削除できません");
        //書籍が貸し出されたら、編集ボタンを非活性化する
        model.addAttribute("EditDisable", "disabled");
        //貸出中の書籍は編集できない旨をメッセージ表示する
        model.addAttribute("EditError", "※貸出中の書籍は編集できません");
        return "details";
        }

}
