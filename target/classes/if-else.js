var d = new Date();
var time = d.getHours();

if (time < 10) {
    document.write("<b>Доброе утро</b>");
}
else {
    document.write("<b>Добрый день</b>");
}