$(function(){
$( '#deleteDialog').on('click',function() {
 
  // もしキャンセルをクリックしたら
  if (!confirm('本当に削除してよろしいですか？')) {
 
    // submitボタンの効果をキャンセルし、クリックしても何も起きない
    return false;
 
 // 「OK」をクリックした際の処理
  } else {
 
    //書籍の削除が実行される
    return true;
 
  }
});
});
