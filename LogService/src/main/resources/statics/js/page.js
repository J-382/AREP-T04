let drawLogsTable = function(){
    alert("drawing");
};

let search = function(){
    document.getElementById("form").submit();
}

window.addEventListener("DOMContentLoaded", function(){
    drawLogsTable();
    document.getElementById("user_button").addEventListener('click', search);
});