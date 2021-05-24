$(function(){
  //削除ボタンが押されたら、下記の処理が動き出す。
$( '#deleteDialog').on('click',function() {
 
  // もし、ダイアログのキャンセルボタンをクリックしたら
  if (!confirm('本当に削除してよろしいですか？')) {
 
    // submitボタンの効果をキャンセルし、詳細画面に戻る
    return false;
 
  } 
});
});
