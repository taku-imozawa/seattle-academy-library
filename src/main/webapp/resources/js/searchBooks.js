/**
 * 
 */
/** */


$(function(){
if ($("#sbox").val().length == 0) {
$("#sbtn").prop("disabled", true);
}
$("#sbox").on("keydown keyup keypress change", function() {
if ($(this).val().length < 1) {
$("#sbtn").prop("disabled", true);
} else {
$("#sbtn").prop("disabled", false);
}
});
});
