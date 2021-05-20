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
 * 詳細表示コントローラー
 */
@Controller
public class DetailsController {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);

    @Autowired
    private BooksService bookdService;

    @Autowired
    private LendingService lendingService;

    /**
     * 詳細画面に遷移する
     * @param locale
     * @param bookId
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public String detailsBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        // デバッグ用ログ
        logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

        model.addAttribute("bookDetailsInfo", bookdService.getBookInfo(bookId));

        //書籍がまだ貸し出されていないかチェックするメソッドを呼び出す
        int idCount = lendingService.lendingConfirmation(bookId);

        // if文でカウントしたidがゼロか1で分岐させる
        if (idCount == 0) {
            //「貸出可」ステータスを表示させる
            model.addAttribute("RentStatus", "貸出可");

            //返却ボタンを非活性化する
            model.addAttribute("ReturnDisable", "disabled");

        } else {
            //書籍が貸し出された後は、貸出ボタン非活性化
            model.addAttribute("RentDisable", "disabled");
            //「貸出中」ステータスを表示させる
            model.addAttribute("RentStatus", "貸出中");

            //書籍が貸出中の時は、削除ボタン非活性化
            model.addAttribute("DeleteDisable", "disabled");
            //貸出中の書籍は削除できない旨のメッセージを表示
            model.addAttribute("DeleteError", "※貸出中の書籍は削除できません");

            //書籍が貸出中の時は、編集ボタン非活性化
            model.addAttribute("EditDisable", "disabled");
            //貸出中の書籍は編集できない旨のメッセージを表示
            model.addAttribute("EditError", "※貸出中の書籍は編集できません");

        }


        return "details";
    }



}
