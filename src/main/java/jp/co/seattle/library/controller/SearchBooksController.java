package jp.co.seattle.library.controller;

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

import jp.co.seattle.library.service.BooksService;


@Controller
public class SearchBooksController {
    final static Logger logger = LoggerFactory.getLogger(SearchBooksController.class);

    @Autowired
    private BooksService booksService;

    /**
     * 書籍を検索する
     *
     * @param 
     * @param 
     * @param 
     * 
     */

    //検索ボタン押下。ここに飛ぶ
    @Transactional
    @RequestMapping(value="/searchBook",method=RequestMethod.POST)

    public String insertBook(
            @RequestParam("matchType") String type,
            @RequestParam("keyWord") String searchKeyWord,
            Model model) {

        if (type.equals("perfectMatching")) {

            if (CollectionUtils.isEmpty(booksService.getSearchBookList(searchKeyWord))) {

                model.addAttribute("searchError", "該当する書籍はありません");

            } else {

                model.addAttribute("bookList", booksService.getSearchBookList(searchKeyWord));
            }
    }



        if (type.equals("partialMatching")) {

            if (CollectionUtils.isEmpty(booksService.getSearchBookList(searchKeyWord))) {
                model.addAttribute("searchError", "該当する書籍はありません");

            } else {
                model.addAttribute("bookList", booksService.getPartiallySearchBookList(searchKeyWord));
            }
        }

        return "home";
    }
}
