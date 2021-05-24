
$(function(){
if ($("#sbox").val() == '') {
$("#sbtn").prop("disabled", true);
}
//入力された時の処理
$("#sbox").on("change", function() {
	if($("#sbox").val() != ''){
		$("#sbtn").prop("disabled", false);
	}else{
		$("#sbtn").prop("disabled", true);
	}
});
});