package jp.co.seattle.library.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.service.BooksService;

@Controller
public class PossibleRentListController {
    final static Logger logger = LoggerFactory.getLogger(PossibleRentListController.class);

    @Autowired
    private BooksService booksService;

    /**
     * 貸出可能な書籍を一覧表示する 
     * 
     */

    //貸出可能書籍一覧ボタンが押されたらここに飛んでくる
    @Transactional
    @RequestMapping(value = "/possibleRent", method = RequestMethod.GET)

    public String possibleRent(Model model) {
        List<BookInfo> resultBookList = booksService.getPossibleList();
        //貸出可能な書籍が存在したがチェック
        if (CollectionUtils.isEmpty(resultBookList)) {
            //　該当書籍なしの場合
            model.addAttribute("existError", "書籍は全て貸し出されています");
            return "possibleRent";
        }
        //取得した貸出可能な書籍のデータを画面に送る
        model.addAttribute("bookList", resultBookList);
        return "possibleRent";

    }
}
