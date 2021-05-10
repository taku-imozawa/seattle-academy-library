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
     * 書籍返却コントローラー
     * 
     *  
     */
    @Controller //APIの入り口   
    public class ReturnBooksController {
        final static Logger logger = LoggerFactory.getLogger(ReturnBooksController.class);

        @Autowired
        private LendingService lendingService;

        @Autowired
        private BooksService bookdService;

        /**
         *書籍を返却する 
         * 
         * @param locale ロケール情報
         * @param bookId 書籍ID
         * @param model モデル情報
         * @return 遷移先画面名　書籍詳細画面
         * 
         */
        @Transactional
        @RequestMapping(value = "/returnBook", method = RequestMethod.POST)
        public String returnBook(
                Locale locale,
                @RequestParam("bookId") Integer bookId,
                Model model) {
            logger.info("Welcome returnBook! The client locale is {}.", locale);

            lendingService.returnBook(bookId);
            //書籍の詳細情報を表示
            model.addAttribute("bookDetailsInfo", bookdService.getBookInfo(bookId));
            //「貸出可」ステータスを表示
            model.addAttribute("RentStatus", "貸出可");
            //書籍が返却された後は、返却ボタン非活性化
            model.addAttribute("ReturnDisable", "disabled");


            return "details";
        }


}
