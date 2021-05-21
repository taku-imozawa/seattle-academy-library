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
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.service.BooksService;

@Controller
public class SearchBooksController {
    final static Logger logger = LoggerFactory.getLogger(SearchBooksController.class);

    @Autowired
    private BooksService booksService;

    /**
     * 書籍を検索する
     *
     * @param matchType 部分一致ボタンと完全一致ボタンのname
     * @param keyWord　検索窓に入力された文字
     * 
     */

    //検索ボタン押下。ここに飛ぶ
    @Transactional
    @RequestMapping(value = "/searchBook", method = RequestMethod.POST)

    public String searchBook(
            @RequestParam("matchType") String type,
            @RequestParam("keyWord") String searchKeyWord,
            Model model) {

        //完全一致ボタンが選択されている場合
        if (type.equals("perfectMatching")) {
            List<BookInfo> resultBookList = booksService.getSearchBookList(searchKeyWord);
            if (CollectionUtils.isEmpty(resultBookList)) {
                //　該当書籍なしの場合
                model.addAttribute("searchError", "該当する書籍はありません");
                return "home";
            }
            model.addAttribute("bookList", resultBookList);
            return "home";

        }
        //部分一致ボタンが選択されている場合
        if (type.equals("partialMatching")) {
            List<BookInfo> resultBookList = booksService.getPartiallySearchBookList(searchKeyWord);
            if (CollectionUtils.isEmpty(resultBookList)) {
                //　該当書籍なしの場合
                model.addAttribute("searchError", "該当する書籍はありません");
                return "home";
            }
            model.addAttribute("bookList", resultBookList);
            return "home";
        }

        return "home";
    }
}
